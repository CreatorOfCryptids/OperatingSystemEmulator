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
            try{
                sem.acquire();
            } catch (Exception e) {}
            
            switch (OS.currentCall){
                case CREATE:
                    createProcess((UserLandProcess)OS.parameters.get(0));
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
            OS.currentCall = OS.CallType.IDLE;
            //scheduler.currentlyRunning.run();
            sem.release();
        }
    }
}
