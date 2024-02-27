import java.util.concurrent.Semaphore;

public class Kernel implements Runnable{

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
     * Opens the desired Device.
     * 
     * @param device A string corresponding to the desired Device.
     */
    private void open(Object device){
        if (device instanceof String)
            OS.retval = vfs.open((String) device);
        else{
            dbMes("Object passed to Kernel.open() was not a string.");
            OS.retval = -1;
        }
    }

    /**
     * Reads from the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param size The amount of data to be read.
     */
    public void read(Object id, Object size) {
        if(id instanceof Integer && size instanceof Integer){

        }
        else{
            dbMes("Objects passed to Kernel.read() were not integers.");
            OS.retval = -1;
        }
    }

    /**
     * Moves the curser within the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param to The distance to move the curser.
     */
    public void seek(Object id, Object to) {
        if(id instanceof Integer && to instanceof Integer){

        }
        else{
            dbMes("Objects passed to Kernel.read() were not integers.");
            OS.retval = -1;
        }
    }

    /**
     * Writes to the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param data The data to be writen to the Device.
     */
    public void write(Object id, Object data) {
        if(id instanceof Integer && data instanceof Byte[]){

        }
        else{
            dbMes("Objects passed to Kernel.read() were not integers.");
            OS.retval = -1;
        }
    }
    
    /**
     * Closes the desired Device.
     * 
     * @param id The FID of the desired Device.
     */
    public void close(Object id) {
        if (id instanceof Integer){

        }
        else{
            dbMes("Object passed to Kernel.close() was not an integer.");
        }
    }

    /**
     * Runs the Call left by the OS.
     */
    public void run(){

        while(true){
            
            sem.acquireUninterruptibly();   // Block progress until called.

            // Switch to correct function baced on current call.
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

                case READ:
                    dbMes("READ");
                    read(OS.parameters.get(0), OS.parameters.get(1));
                    break;

                case SEEK:
                    dbMes("Seek");
                    seek(OS.parameters.get(0), OS.parameters.get(1));
                    break;
                
                case WRITE:
                    dbMes("Write");
                    write(OS.parameters.get(0), OS.parameters.get(1));
                    break;

                case CLOSE:
                    dbMes("Close");
                    close(OS.parameters.get(0));
                    break;
                
                default:
                    dbMes("Unknown Current Call: " + OS.currentCall);
            }
            
            dbMes("Resuming currentProcess: " + scheduler.getCurrentlyRunning().toString());
            scheduler.getCurrentlyRunning().start();
        }
    }

    /**
     * Stops the currently running process so the kernel can start.
     */
    public void stopCurrentProcesss() {
        if (scheduler.getCurrentlyRunning() != null){
            dbMes("Stopping: " + scheduler.getCurrentlyRunning().getClass());
            scheduler.getCurrentlyRunning().stop();
        }
        else{
            dbMes("Current Process is null");
        }
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
