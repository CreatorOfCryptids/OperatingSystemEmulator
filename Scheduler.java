import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    
    private LinkedList<UserLandProcess> q;
    private Timer timer;
    public UserLandProcess currentlyRunning;

    Scheduler(){
        q = new LinkedList<UserLandProcess>();
        timer = new Timer();
        
        timer.scheduleAtFixedRate(new Interupt(), 250, 250);
    }

    private class Interupt extends TimerTask{
        public void run(){
            switchProcess();
        }
    }

    public int createProcess(UserLandProcess up){
        // Create new kernalLand process
        if(up != null)
            // Adds it to the list.
            q.add(up);

        //Starts it if its the first one.
        if(q.size() == 1)
            up.start();

        return up.getPID();
    }

    public void switchProcess(){

        // Stop current process if exists
        if(currentlyRunning != null)
            currentlyRunning.requestStop();

        // Send to back if not done
        if(currentlyRunning != null && currentlyRunning.isDone() && q.size()>0)
            q.removeFirst();
        
        else if(q.size() > 0){
            q.add(q.removeFirst());
            currentlyRunning = q.getFirst();
            currentlyRunning.run();
        }
        else
            new IdleProcess().run();

        // Set proccess at begining to currentlly running and run if it exists. Otherwise run idle.
        
        
        
        /*if(q.size()>0){
            UserLandProcess sendToBack = q.removeFirst();
            sendToBack.requestStop();

            currentlyRunning = q.getFirst();
            currentlyRunning.run();

            if (!sendToBack.isDone())
                q.add(sendToBack);
        }
        else{

        }/**/
        
    }
}
