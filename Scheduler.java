import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Scheduler {
    
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
            OS.debug("SCHEDULER: Interupt.");
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
            // If it is the first one, set it to currently running.
            if(queue.isEmpty() && currentlyRunning == null){
                currentlyRunning = up;
            }
            // Otherwize, add it to the end of the queueue.
            else{
                queue.addLast(up);
            }
        }
        
        OS.debug("SCHEDULER: Added process " + up.getClass() + ". There are " + queue.size() + " processes in the queueue.");
        
        sem.release();

        return up.getPID();
    }

    /**
     * Switches to the next process in the queue.
     */
    public void switchProcess(){

        sem.acquireUninterruptibly();
        OS.debug("SCHEDULER: Switching Process.");
        OS.debug("SCHEDULER: currentlyRunning: " + currentlyRunning.getClass());


        // Check if the currently Running process is still alive
        if (currentlyRunning.isDone() == false){   // If it is still running, stop it and add it to the end of the queueue.
            OS.debug("SCHEDULER: Case: Still alive.");
            queue.addLast(currentlyRunning);
            currentlyRunning = queue.removeFirst();
            //currentlyRunning.start();
        }
        else{   // Otherwize, set the first item on the queue to be currently running.
            OS.debug("SCHEDULER: Case: Someone died.");
            if (queue.size() == 0){    
                OS.debug("SCHEDULER: Queueue is empty :/");
                OS.debug(" SCHEDULER: Currently running: " + currentlyRunning.getClass());
            }
            else {
                currentlyRunning = queue.removeFirst();
                currentlyRunning.start();
            }
        }

        sem.release();
    }
}
