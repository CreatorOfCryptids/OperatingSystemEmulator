import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.time.Clock;
import java.util.HashMap;

public class Scheduler{

    private PCB currentlyRunning;                   // The process that is currently running.
    private LinkedList<PCB> realTimeQ;              // The queueues of running prosesses.
    private LinkedList<PCB> interactiveQ;   
    private LinkedList<PCB> backgroundQ;

    private HashMap<Integer, PCB> processMap;       // List of processes
    private HashMap<Integer, PCB> awaitingMessage;  // The list of processes waiting for a message.

    private LinkedList<SleepingProcess> sleeping;   // The queueue of sleeping processes 
    private Clock clock;                            // Clock

    private Random rand;                            // The random number generator for the random 
    private Semaphore sem;                          // The semaphore that makes sure that the threads don't overlap with eachother.
    private Timer timer;                            // Schedules an interrupt for every 250 ms

    private Kernel kernel;                          // A reference to the Kernel
    

    /**
     * Constructor.
     */
    Scheduler(Kernel kernel){
        this.processMap = new HashMap<Integer, PCB>();

        this.realTimeQ = new LinkedList<PCB>();
        this.interactiveQ = new LinkedList<PCB>();
        this.backgroundQ = new LinkedList<PCB>();

        awaitingMessage = new HashMap<Integer, PCB>();

        this.sleeping = new LinkedList<SleepingProcess>();
        this.clock = Clock.systemUTC();

        this.rand = new Random();
        this.timer = new Timer();
        this.sem = new Semaphore(1);

        this.kernel = kernel;
        
        timer.scheduleAtFixedRate(new Interupt(), 250, 250);
    }

    /**
     * Packages the Interrupt into a timerTask.
     */
    private class Interupt extends TimerTask{
        public void run(){
            dbMes("Interupt.                                    !!!");
            
            currentlyRunning.requestStop();

            dbMes("Sleeping processes: "+sleeping.toString());

            // Wake up any sleeping processes that should be awoken.
            while(sleeping.isEmpty() == false && sleeping.getFirst().awaken()){
                sleeping.removeFirst();
            }
                
        }
    }

    // Scheduler Functionality:

    /**
     * Adds a kernelland process to the queueue.
     * 
     * @param up The userland process to be added.
     * @return The PID of the added process.
     */
    public int createProcess(UserLandProcess up, OS.Priority priority){

        sem.acquireUninterruptibly();

        // Check if the process exists, if it doesn't return an error.
        if(up == null)
            return -1;
        
        PCB newProcess = new PCB(up, priority);

        processMap.put(newProcess.getPID(), newProcess);

        // If it is the first process, set it to currentlyRunning.
        if(currentlyRunning == null){
            currentlyRunning = newProcess;
        }
        else{   // Otherwize, add it to the end of the correct queueue.
            returnToQueue(newProcess);
        }
        
        dbMes("Added process " + up.getClass() + " to the " + priority.toString() + " queueue.");
        
        sem.release();

        return newProcess.getPID();
    }

    /**
     * Switches to the next process in the queue.
     */
    public void switchProcess(){

        sem.acquireUninterruptibly();

        dbMes("Switching Process.");
        dbMes("currentlyRunning before switch: " + currentlyRunning.toString());

        // Check if the currently Running process is still alive
        if (currentlyRunning.isDone() == false){   // If it is still running, move it to the end of the correct queueue.
            dbMes("Case: Still alive.");
            returnToQueue(currentlyRunning);
        }
        else{
            dbMes("Case: Someone died.");

            // Close the dead process's devices, if it hasn't done so already.
            int[] deviceIDs = currentlyRunning.getDeviceIDs();
            for(int i = 0; i< deviceIDs.length; i++)
                if(deviceIDs[i] != -1){
                    dbMes("Removed device.");
                    kernel.close(deviceIDs[i]);
                }
            
            // Remove dead process from HashMap
            processMap.remove(currentlyRunning.getPID());
        }
        
        // Get the next process in the queue and set it to currentlyRunning.
        currentlyRunning = getRandomQueue().removeFirst();

        // If the process was waiting for a message, add it to OS.retVal so it can access the message.
        if (currentlyRunning.isWaitingForMessage())
            OS.retval = currentlyRunning.getMessage();
        
        dbMes("currentlyRunning after switch: " + currentlyRunning.toString());

        sem.release();
    }

    /**
     * Puts the currently running process into the sleep queue in the right location. Does this 
     * by wrapping the process in a SleepingProcess object that stores how when it should be 
     * woken up.
     * 
     * @param miliseconds The amount of time that should pass before the process is woken up.
     */
    public void sleep(int miliseconds){

        dbMes("Sleep");
        currentlyRunning.timeOutReset();

        // Put it in a wrapper to keep track of when it should wake up.
        SleepingProcess sp = new SleepingProcess(currentlyRunning, miliseconds);

        if(sleeping.isEmpty()){
            sleeping.add(sp);
        }
        else{
            boolean inserted = false;
            for(int i=0; i<sleeping.size(); i++){
                // Insert it before the first sleeping process that has a later wake up time than it.
                if(sleeping.get(i).getWakeUpTime() >= sp.getWakeUpTime()){
                    sleeping.add(i, sp);
                    inserted = true;
                    break;
                }
            }

            if(!inserted){  // If there are no processes with a later wake up time, this won't happen in the loop.
                sleeping.add(sp);
            }
        }

        substituteProcess();
    }

    // Accessors:

    /**
     * The getCurrentlyRunning() accessor.
     * 
     * @return The currentlyRunning process's PCB.
     */
    public PCB getCurrentlyRunning(){
        return currentlyRunning;
    }

    /**
     * Return the PID of the desired process.
     * 
     * @param name The name of the desired process.
     * @return The PID of the desired process, or -1 on failure.
     */
    public int getPID(String name){

        // look thru each process to see if it's name matches.
        for(Map.Entry<Integer, PCB> pcb: processMap.entrySet()){
            if(pcb.getValue().getName().equals(name)){
                dbMes("getPID(): Found process " + name);
                return pcb.getKey();
            }
        }

        dbMes("getPID(): Process " + name + " not found");
        return -1;
    }

    /**
     * Adds a message to the end of the targeted Process's queue and returns if successful.
     * 
     * @param mes The message to be sent
     * @return True if sending was successful. False on failure.
     */
    public boolean sendMessage(Message mes) {

        dbMes("Sending message to PID: " + mes.getTarget() + " From PID: " + mes.getSender());

        // Check if the target process is waiting for a message.
        if(awaitingMessage.containsKey(mes.getTarget())){

            dbMes("Found target in awaitingMessage Map.");
            PCB noLongerWaiting = awaitingMessage.remove(mes.getTarget());

            noLongerWaiting.addMessage(mes);

            // It's no longer waiting for a message, so add it back to the queues.
            returnToQueue(noLongerWaiting);

            return true;
        }
        // Otherwise look if the target process is not waiting
        else if(processMap.containsKey(mes.getTarget())){

            dbMes("Found target in processMap");

            processMap.get(mes.getTarget()).addMessage(mes);

            return true;
        }
        // If it doesn't exist, print error and return false.
        else{
            dbMes("ERROR: process " + mes.getTarget() + " does not exist.");
            return false;
        }
    }

    /**
     * Removes the currentlyRunning process from the queue, and adds it to the awaitingMessages map.
     */
    public void addToWaitMes(){

        dbMes("addToWaitMes()");

        awaitingMessage.put(currentlyRunning.getPID(), currentlyRunning);

        substituteProcess();
    }

    // Helper Methods:

    /**
     * Adds the process back into its correct queue.
     * 
     * @param returner The queue to be returned to its queue.
     */
    private void returnToQueue(PCB returner){
        if (returner.getPriority() == OS.Priority.REALTIME){
            realTimeQ.add(returner);
        }
        else if (returner.getPriority() == OS.Priority.INTERACTIVE){
            interactiveQ.add(returner);
        }
        else{
            backgroundQ.add(returner);
        }
    }

    /**
     * Randomly chooses which queue should be ran next.
     * 
     * @return The queue that was randomly selected.
     */
    private LinkedList<PCB> getRandomQueue(){

        int qSelection = 0;

        // Only select queues that have processes in them
        if(realTimeQ.isEmpty() == false){
            qSelection = rand.nextInt(10);
        }
        else if(interactiveQ.isEmpty() == false){
            qSelection = rand.nextInt(4);
        }
        else{
            qSelection = rand.nextInt(1);
        }
        
        dbMes("Next Q: " + qSelection);

        if(qSelection >= 4){
            return realTimeQ;
        }
        else if(qSelection >=1){
            // We only check if this is empty if realTime is also empty. Checking here for safety.
            if (interactiveQ.isEmpty() == false){                
                return interactiveQ;
            }
            else{
                // If intereactive is empty, default to background.
                return backgroundQ;
            } 
        }
        else{
            return backgroundQ;
        }
    }

    /**
     * Replaces currentlyRunning safely without adding the old process back into the queues.
     */
    private void substituteProcess(){

        dbMes("Substituting Process.");

        // Get the next process in the queue and set it to currently running.
        currentlyRunning = getRandomQueue().removeFirst();

        // If the process is recived a message, add it to OS.retVal
        if (currentlyRunning.isWaitingForMessage())
            OS.retval = currentlyRunning.getMessage();

        dbMes("currentlyRunning after Substitution: " + currentlyRunning.toString());

    }

    /**
     * A helper class that helps to keep sleeping processes organized.
     */
    private class SleepingProcess{

        private long wakeUpTime;    // When the process should be woken up
        private PCB process;        // The process that is being put to sleep.

        /**
         * Constructor.
         * 
         * @param process The process that is being put to sleep.
         * @param miliseconds How long before it should be woken up.
         */
        SleepingProcess(PCB process, int miliseconds){
            this.wakeUpTime = clock.millis() + miliseconds;
            this.process = process;
        }
        
        /**
         * Accesses the time that this process should be woken up.
         * 
         * @return The time that the process should be woken up.
         */
        public long getWakeUpTime(){
            return wakeUpTime;
        }

        /**
         * Returns the process to the right queue if it is time to wake up.
         * 
         * @return True if it woke up, false otherwize.
         */
        public boolean awaken(){
            if (this.wakeUpTime < clock.millis()){

                returnToQueue(process);

                dbMes("Waking up " + process.toString());

                return true;
            }
            else {
                return false;
            }   
        }

        /**
         * The toString() method. Helps with debugging
         * 
         * @return A string containing the information in the list.
         */
        public String toString(){
            return "Sleepy " + process.toString();
        }
    }

    // Debugging Helper Methods:

    /**
     * DEBUGGING HELPER!!!
     * 
     * @param message The debug message.
     */
    public void dbMes(String message){
        OS.dbMes("||SCHEDULER: " + message);
    }
}
