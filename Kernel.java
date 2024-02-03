import java.util.concurrent.Semaphore;

public class Kernel implements Runnable{
    public Scheduler scheduler;
    private Thread thread;
    private Semaphore sem;

    Kernel(){
        scheduler = new Scheduler();
        thread = new Thread(this);
        sem = new Semaphore(0);
        
        thread.start();
    }

    public void start(){
        sem.release();      // Increments the sem allowing the thread to run.
    }

    public int createProcess(UserLandProcess up){
        return scheduler.createProcess(up);
    }

    public void run(){
        while(true){
                
            sem.acquireUninterruptibly();

            switch (OS.currentCall){
                case CREATE:
                    OS.debug("KERNEL: Create Process");;
                    OS.retval = this.createProcess((UserLandProcess) OS.parameters.get(0));
                    break;
                case SWITCH:
                    OS.debug("KERNEL: Switch process");
                    scheduler.switchProcess();
                    break;
                case IDLE:
                    // TODO: Later
                    break;
                case INIT:
                    // TODO: Later
                    break;
            }

            scheduler.currentlyRunning.start();
        }
    }

    public void stopCurrentProcesss() {
        OS.debug("KERNEL: Stoping current process.");
        if (scheduler.currentlyRunning != null)
            scheduler.currentlyRunning.stop();
        }
}
