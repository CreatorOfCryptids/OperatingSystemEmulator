import java.util.concurrent.Semaphore;

public class Kernel {
    public Scheduler scheduler;
    private Thread thread;
    private Semaphore sem;

    Kernel(){
        scheduler = new Scheduler();
        thread = new Thread();
        sem = new Semaphore(1);

        thread.start();
    }

    public void start() throws InterruptedException{
        sem.release();
    }

    public void run(){
        while(true){
            sem.acquireUninterruptibly();
            OS.currentCall();
            scheduler.currentUP.run();
        }
    }

}
