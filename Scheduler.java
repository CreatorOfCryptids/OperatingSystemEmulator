import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.time.Clock;
import java.util.HashMap;

public class Scheduler{

    private HashMap<Integer, PCB> processMap;

    private PCB currentlyRunning;                   // The process that is currently running.
    private LinkedList<PCB> realTimeQ;              // The queueues of running prosesses.
    private LinkedList<PCB> interactiveQ;   
    private LinkedList<PCB> backgroundQ;

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
            dbMes("Interupt.");
            
            currentlyRunning.requestStop();

            while(sleeping.isEmpty() == false && sleeping.getFirst().awaken()){
                dbMes(sleeping.toString());
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
        else{   // Otherwize, add it to the end of the queueue.
            getCorrespondingQueue(priority).addLast(newProcess);
        }
        
        dbMes("Added process " + up.getClass() + ". There are " + getCorrespondingQueue(priority).size() + " processes in the " + priority.toString() + " queueue.");
        
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
            getCorrespondingQueue(currentlyRunning.getPriority()).addLast(currentlyRunning);
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
            
            // Remove dead process from hashMap
            processMap.remove(currentlyRunning.getPID());
        }
        
        // Get the next process in the queue and set it to currently running.
        currentlyRunning = getRandomQueue().removeFirst();

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

        SleepingProcess sp = new SleepingProcess(currentlyRunning, miliseconds);

        if(sleeping.isEmpty()){
            sleeping.add(sp);
        }
        else{
            for(int i=0; i<sleeping.size(); i++){
                if(sleeping.get(i).getWakeUpTime() < sp.getWakeUpTime()){
                    sleeping.add(i+1, sp);
                    break;
                }
            }
        }

        dbMes("Sleeping has " + sleeping.size() + " members in the queue: " + sleeping.toString());

        currentlyRunning = getRandomQueue().removeFirst();
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

        for(Map<Integer, PCB>.entry(null, null))
        // TODO : this

        return -1;
    }

    // Helper Methods:

    /**
     * Returns the queue that corresponds to the given Priority level.
     * 
     * @param priority The priority level of the desired queue
     * @return The LinkedList that corresponds to the passed priority level
     */
    private LinkedList<PCB> getCorrespondingQueue(OS.Priority priority){
        if(priority == OS.Priority.REALTIME)
            return realTimeQ;
        else if (priority == OS.Priority.INTERACTIVE)
            return interactiveQ;
        else if (priority == OS.Priority.BACKGROUND){
            return backgroundQ;
        }
        else{
            dbMes("ERROR: Incorrect Priority");
            return backgroundQ;
        }
    }

    /**
     * Randomly chooses which queue should be ran next.
     * 
     * @return The queue that was randomly selected.
     */
    private LinkedList<PCB> getRandomQueue(){
        int qSelection = 0;

        if(realTimeQ.isEmpty() == false){
            qSelection = rand.nextInt(10);
        }
        else if(interactiveQ.isEmpty() == false){
            qSelection = rand.nextInt(4);
        }
        else
            qSelection = rand.nextInt(1);
        
        dbMes("Next Q: " + qSelection);

        if(qSelection >= 4){
            dbMes("NextQ RealTime");
            return realTimeQ;
        }
        else if(qSelection >=1){
            // We only check if this is empty if realTime is also empty. Checking here for safety.
            if (interactiveQ.isEmpty() == false){
                dbMes("NextQ interactive");
                return interactiveQ;
            }
            else{
                dbMes("NextQ background");
                return backgroundQ;
            } 
        }
        else{
            dbMes("NextQ background");
            return backgroundQ;
        }
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
         * The getProcess() accessor.
         * 
         * @return The process in the SleepingProcess Wrapper.
         */
        public PCB getProcess(){
            return process;
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
                LinkedList<PCB> q = getCorrespondingQueue(process.getPriority());

                q.add(process);

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
