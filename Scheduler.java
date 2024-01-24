import java.util.LinkedList;
import java.util.Timer;

public class Scheduler {
    
    private LinkedList<UserLandProcess> q;
    private Timer timer;
    public UserLandProcess currentlyRunning;

    Scheduler(){
        q = new LinkedList<UserLandProcess>();
        timer = new Timer();
        
    }

    public int createProcess(UserLandProcess up){
        if(up != null)
            q.add(up);

        if()
        switchProcess();
    }

    public void switchProcess(){
        UserLandProcess sendToBack = q.removeFirst();
        currentlyRunning = q.getFirst();

        if (!sendToBack.isDone())
            q.add(sendToBack);
        
        
    }
}
