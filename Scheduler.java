import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Scheduler {
    
    private LinkedList<UserLandProcess> q;
    private Semaphore sem;
    private Timer timer;
    public UserLandProcess currentlyRunning;

    Scheduler(){
        q = new LinkedList<UserLandProcess>();
        timer = new Timer();
        sem = new Semaphore(1);
        
        timer.scheduleAtFixedRate(new Interupt(), 250, 250);
    }

    private class Interupt extends TimerTask{
        public void run(){
            switchProcess();
        }
    }

    public int createProcess(UserLandProcess up){
        // Add Kernelland Process to the list.
        sem.acquireUninterruptibly();

        if(up != null)
            q.add(up);

        //Starts it if its the first one.
        if(q.size() == 1){
            currentlyRunning = up;
            up.start();
        }

        sem.release();

        return up.getPID();
    }

    public void switchProcess(){
        sem.acquireUninterruptibly();

        //Stop current process, and remove it if is done.
        if (currentlyRunning != null){
            // TODO: Fix this shit.
            if (currentlyRunning.isDone()){
                q.removeFirst();
                currentlyRunning.requestStop();
                currentlyRunning = q.getFirst();
            }
            else if (q.size()>0){
                currentlyRunning.requestStop();
                q.add(q.removeFirst());
                currentlyRunning = q.getFirst();
            }

            currentlyRunning.start();
        }
        else    
            System.out.println("There are " + q.size() + " processes in the queueue");

        sem.release();

        // // Stop current process if exists
        // if(currentlyRunning != null)
        //     currentlyRunning.requestStop();

        // // Send to back if not done
        // if(currentlyRunning != null && currentlyRunning.isDone() && q.size()>0){
        //     q.removeFirst();
        //     currentlyRunning = q.getFirst();
        //     currentlyRunning.run();
        // }
            
        // else if(q.size() > 0){
        //     currentlyRunning = q.get(0);
        //     q.add(q.removeFirst());
        //     currentlyRunning.run();
        // }
        // else
        //     //new IdleProcess().run();
        //     System.out.println("There are " + q.size() + " processes in the queueue");

        // Set proccess at begining to currentlly running and run if it exists. Otherwise run idle.
    }
}
