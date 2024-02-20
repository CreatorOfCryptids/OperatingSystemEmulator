import java.util.concurrent.Semaphore;

public class Kernel implements Runnable, Device{

    private Thread thread;
    private Semaphore sem;
    private Scheduler scheduler;
    private VFS vfs;

    /**
     * Constructor.
     */
    Kernel(){
        thread = new Thread(this);
        sem = new Semaphore(0);
        scheduler = new Scheduler();
        vfs = new VFS();
        
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
     * 
     * @param up The UserlandProcess to send to Scheduler.
     * @return The PID of the Process, or -1 for an error.
     */
    private void createProcess(Object up, Object priority){

        if (up instanceof UserLandProcess && priority instanceof OS.Priority)
            OS.retval = scheduler.createProcess((UserLandProcess)up, (OS.Priority) priority);
        else{
            dbMes("Object passed to kernel.createProcess() was not a UserlandProcess");
            OS.retval = -1;
        }

    }

    /**
     * Switches the currentlyRunning process out for the next process in the queue.
     */
    private void switchProcess(){
        scheduler.switchProcess();
    }

    /**
     * Puts the currently running process to sleep.
     * 
     * @param miliseconds The amount of time the process will be asleep for.
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
     * Opens the desired device.
     * 
     * @param device A string corresponding to the desired device.
     */
    private void open(Object device){
        if (device instanceof String)
            OS.retval = vfs.open((String) device);
        else{
            dbMes("Object passed to kernel.open() was not a string");
            OS.retval = -1;
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
                    createProcess(OS.parameters.get(0), OS.parameters.get(1));
                    break;

                case SWITCH:
                    dbMes("Switch process");
                    switchProcess();
                    break;

                case SLEEP:
                    dbMes("Sleep");
                    sleep(OS.parameters.get(0));
                    break;

                case OPEN:
                    dbMes("Open");
                    open(OS.parameters.get(0));
                    break;
                
                default:
                    dbMes("Unknown Current Call.");
            }
            
            dbMes("Resuming currentProcess: " + scheduler.currentlyRunning.toString());
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

    public int open(String s) {
        
    }

    public void close(int id) {
        
    }

    public byte[] read(int id, int size) {
        
    }

    public void seek(int id, int to) {
        
    }

    public int write(int id, byte[] data) {
        
    }
    
    /**
     * DEBUGGING HELPER!!!
     * 
     * @param message The debug message.
     */
    private void dbMes(String message){
        OS.dbMes("KERNEL: " + message);
    }
}
