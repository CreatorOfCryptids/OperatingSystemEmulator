import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{
    
    private Thread thread = new Thread();
    private Semaphore sem = new Semaphore(1);
    private boolean isExpired = false;
    public static int processCount = 0;
    private int PID = processCount++;
    
    public void requestStop(){
        isExpired = true;
    }

    public abstract void main();

    public boolean isStopped(){
        return (sem.availablePermits() == 0);
    }

    public boolean isDone(){
        return !thread.isAlive();
    }

    public void start(){
        sem.release();
    }

    public void stop(){
        sem.acquireUninterruptibly();
    }

    public void run(){
        sem.acquireUninterruptibly();
        main();
    }

    public void cooperate(){
        if(isExpired == true){
            isExpired = false;
            OS.switchProcess();
        }
    }
    
    public int getPID(){
        return PID;
    }
}