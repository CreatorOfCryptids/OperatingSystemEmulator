import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.time.Clock;

public class Scheduler{
    
    private LinkedList<PCB> realTimeQ;              // The queueues of running prosesses.
    private LinkedList<PCB> interactiveQ;   
    private LinkedList<PCB> backgroundQ;

    private LinkedList<SleepingProcess> sleeping;   // The queueue of sleeping processes 
    private Clock clock;                            // Clock

    private Random rand;
    private Semaphore sem;                          // The semaphore that makes sure that the threads don't overlap with eachother.
    private Timer timer;                            // Schedules an interrupt for every 250 ms
    public PCB currentlyRunning;                    // The process that is currently running.

    /**
     * Constructor.
     */
    Scheduler(){
        realTimeQ = new LinkedList<PCB>();
        interactiveQ = new LinkedList<PCB>();
        backgroundQ = new LinkedList<PCB>();

        sleeping = new LinkedList<SleepingProcess>();
        clock = Clock.systemUTC();

        rand = new Random();
        timer = new Timer();
        sem = new Semaphore(1);
        
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
                sleeping.removeFirst();
            }
                
        }
    }

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

        dbMes("Sleeping has " + sleeping.size() + " members in the queue");

        switchProcess();
    }

    /**
     * Helper Method: Returns the queue that corresponds to the given Priority level.
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
    }

    /**
     * DEBUGGING HELPER!!!
     * 
     * @param message The debug message.
     */
    public void dbMes(String message){
        //OS.dbMes("SCHEDULER: " + message);
    }
}
