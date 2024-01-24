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

    public int createProcess(UserLandProcess up){
        return scheduler.createProcess(up);
    }

    public void run(){
        while(true){
            sem.acquireUninterruptibly();
            switch (OS.currentCall){
                case CREATE:
                    this.createProcess((UserLandProcess)OS.parameters.get(0));
                case SWITCH:
                    OS.switchProcess();
                case IDLE:
                    // TODO: Later
                case INIT:
                    // TODO: Later
            }
            scheduler.currentlyRunning.run();
        }
    }

}
