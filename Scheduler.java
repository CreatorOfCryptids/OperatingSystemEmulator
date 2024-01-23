import java.util.LinkedList;
import java.util.Timer;

public class Scheduler {
    
    private LinkedList<UserLandProcess> q;
    private Timer timer;
    public UserLandProcess currentUP;

    Scheduler(){
        q = new LinkedList<UserLandProcess>();
        timer = new Timer(interupt, 250);
    }

    public int createProcess(UserLandProcess up){
        q.add(up);
        switchProcess();
    }

    public void switchProcess(){
        UserLandProcess sendToBack = q.removeFirst();
        currentUP = q.getFirst();
        q.add(sendToBack);
    }
}
