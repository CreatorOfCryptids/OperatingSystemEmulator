import java.util.concurrent.Semaphore;

public class Kernel implements Runnable{

    private Scheduler scheduler;
    private Thread thread;
    private Semaphore sem;

    Kernel(){
        scheduler = new Scheduler();
        thread = new Thread(this);
        sem = new Semaphore(0);
        
        thread.start();
    }

    /**
     * Starts the kernel so It can perform the desired operation.
     */
    public void start(){
        sem.release();      // Increments the sem allowing the thread to run.
    }

    /**
     * Takes an object. Makes sure that it is a UserlandProcess, then passes it to Scheduler.
     * @param up The UserlandProcess to send to Scheduler.
     * @return The PID of the Process, or -1 for an error.
     */
    private int createProcess(Object up){

        if (up instanceof UserLandProcess)
            return scheduler.createProcess((UserLandProcess)up);
        else{
            dbMes("Object passed to Create Process was not a UserlandProcess");
            return -1;
        }

    }

    /**
     * 
     */
    private void switchProcess(){
        scheduler.switchProcess();
    }

    /**
     * 
     */
    private void sleep(Object miliseconds){
        if (miliseconds instanceof Integer){
            scheduler.sleep((int)miliseconds);
        }
        else{
            dbMes("Object passed to sleep() was not an int.");
        }
    }

    /**
     * Runs the Call left by the OS.
     */
    public void run(){

        while(true){
                
            sem.acquireUninterruptibly();

            switch (OS.currentCall){
                case CREATE:
                    dbMes("Create Process");;
                    OS.retval = this.createProcess(OS.parameters.get(0));
                    break;

                case SWITCH:
                    dbMes("Switch process");
                    switchProcess();
                    break;

                case SLEEP:
                    sleep(OS.parameters.get(0));
                    break;
                
                default:
                    dbMes("Unknown Current Call.");
            }
            
            dbMes("Resuming currentProcess.");
            scheduler.currentlyRunning.start();
        }
    }

    /**
     * Stops the currently running process so the kernel can start.
     */
    public void stopCurrentProcesss() {

        dbMes("Stoping current process.");

        if (scheduler.currentlyRunning != null)
            scheduler.currentlyRunning.stop();
    }

    private void dbMes(String message){
        OS.dbMes("KERNEL: " + message);
    }
}
