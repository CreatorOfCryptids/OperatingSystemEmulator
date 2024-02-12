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
    private int createProcess(Object up, Object priority){

        if (up instanceof UserLandProcess && priority instanceof OS.Priority)
            return scheduler.createProcess((UserLandProcess)up, (OS.Priority) priority);
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
            dbMes("ERROR: Object passed to sleep() was not an int.");
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
                    OS.retval = this.createProcess(OS.parameters.get(0), OS.parameters.get(1));
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
            
            dbMes("Resuming currentProcess: " + scheduler.currentlyRunning.getClass());
            scheduler.currentlyRunning.start();
        }
    }

    /**
     * Stops the currently running process so the kernel can start.
     */
    public void stopCurrentProcesss() {

        if (scheduler.currentlyRunning != null){
            dbMes("Stopping: " + scheduler.currentlyRunning.getClass());
            scheduler.currentlyRunning.stop();
        }
        else{
            dbMes("Current Process is null");
        }
            
    }

    private void dbMes(String message){
        OS.dbMes("KERNEL: " + message);
    }
}
