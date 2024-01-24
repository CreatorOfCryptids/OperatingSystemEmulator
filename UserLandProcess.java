import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{
    
    Thread thread = new Thread();
    Semaphore sem = new Semaphore(1);
    boolean isExpired = false;
    
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

    public void start() throws InterruptedException{
        sem.release();
    }

    public void stop() throws InterruptedException{
        sem.acquire();
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
}