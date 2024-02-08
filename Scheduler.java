import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.time.Clock;

public class Scheduler{
    
    private LinkedList<UserLandProcess> queue;  // The queueue of running prosesses.
    private Semaphore sem;                      // The semaphore that makes sure that the threads don't overlap with eachother.
    private Timer timer;                        // Schedules an interrupt for every 250 ms
    public UserLandProcess currentlyRunning;    // THe process that is currently running.

    /**
     * Constructor.
     */
    Scheduler(){
        queue = new LinkedList<UserLandProcess>();
        timer = new Timer();
        sem = new Semaphore(1);
        
        timer.scheduleAtFixedRate(new Interupt(), 250, 250);
    }

    /**
     * Packages the process into a timerTask.
     */
    private class Interupt extends TimerTask{
        public void run(){
            dbMes("Interupt.");
            currentlyRunning.requestStop();
        }
    }

    /**
     * Adds a kernelland process to the queueue.
     * @param up The userland process to be added.
     * @return The PID of the added process.
     */
    public int createProcess(UserLandProcess up){

        sem.acquireUninterruptibly();

        // Check if the process exists,
        if(up != null){
            // If it is the first process, set it to currentlyRunning.
            if(queue.isEmpty() && currentlyRunning == null){
                currentlyRunning = up;
            }
            else{   // Otherwize, add it to the end of the queueue.
                queue.addLast(up);
            }
        }
        
        dbMes("Added process " + up.getClass() + ". There are " + queue.size() + " processes in the queueue.");
        
        sem.release();

        return 0;
    }

    /**
     * Switches to the next process in the queue.
     */
    public void switchProcess(){

        sem.acquireUninterruptibly();
        dbMes("Switching Process.");
        dbMes("currentlyRunning before switch: " + currentlyRunning.getClass());

        // Check if the currently Running process is still alive
        if (currentlyRunning.isDone() == false){   // If it is still running, move it to the end of the queueue.
            dbMes("Case: Still alive.");
            queue.addLast(currentlyRunning);
            currentlyRunning = queue.removeFirst();
        }
        else{   // Otherwize, set the first item on the queue to be currently running.
            dbMes("Case: Someone died.");
            if (queue.size() >= 0){ // Make sure there is something on the queue to run.
                currentlyRunning = queue.removeFirst();
                currentlyRunning.start();
            }
            else
                dbMes("Queue is empty :/");
        }

        dbMes("currentlyRunning after switch: " + currentlyRunning.getClass());

        sem.release();
    }

    /**
     * 
     */
    public void sleep(int miliseconds){
        
    }

    public void dbMes(String message){
        OS.dbMes("SCHEDULER: " + message);
    }
}
