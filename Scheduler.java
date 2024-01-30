import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Scheduler {
    
    private LinkedList<UserLandProcess> q;  // The queueue of running prosesses.
    private Semaphore sem;                  // The semaphore that makes sure that the threads don't overlap with eachother.
    private Timer timer;                    // Schedules an interrupt for every 250 ms
    public UserLandProcess currentlyRunning;// THe process that is currently running.

    /**
     * Constructor.
     */
    Scheduler(){
        q = new LinkedList<UserLandProcess>();
        timer = new Timer();
        sem = new Semaphore(1);
        
        timer.scheduleAtFixedRate(new Interupt(), 250, 250);
    }

    /**
     * Packages the process into a timerTask.
     */
    private class Interupt extends TimerTask{
        public void run(){
            switchProcess();
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
            if(q.isEmpty()){
                currentlyRunning = up;
                up.start();
            }
            // Otherwize, add it to the end of the queueue.
            else{
                q.add(up);
            }
        }
        
        sem.release();

        return up.getPID();
    }

    /**
     * Switches to the next process in the queue.
     */
    public void switchProcess(){
        try{
            sem.acquire();
        } catch (Exception e) {}
        // Make sure that currently running has been initialized.
        if (currentlyRunning == null){  // This should mean that the queueue is empty
            if (!q.isEmpty()){          // But if its not, run the next item.
                currentlyRunning = q.removeFirst();
                currentlyRunning.start();
            }
        }
        else if (!currentlyRunning.isDone()){
            currentlyRunning.cooperate();
            q.add(currentlyRunning);
            currentlyRunning = q.removeFirst();
            currentlyRunning.start();
        }
        else{
            if (q.isEmpty()){
                System.out.println("Queueue is empty :/");
            }
            else {
                currentlyRunning = q.removeFirst();
                currentlyRunning.start();
            }
        }

        sem.release();
    }
}
