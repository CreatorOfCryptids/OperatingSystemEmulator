import java.util.concurrent.Semaphore;

public class Kernel implements Runnable{
    public Scheduler scheduler;
    private Thread thread;
    private Semaphore sem;

    Kernel(){
        scheduler = new Scheduler();
        thread = new Thread(this);
        sem = new Semaphore(1);
        
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
                    OS.retval = this.createProcess((UserLandProcess) OS.parameters.get(0));

                    /* Testing.
                    createProcess(new IdleProcess());
                    createProcess(new GoodbyeWorld());
                    /**/
                    break;
                case SWITCH:
                    //System.out.println("kernel.switch");
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
        if (scheduler.currentlyRunning != null)
            scheduler.currentlyRunning.stop();
        }
}
