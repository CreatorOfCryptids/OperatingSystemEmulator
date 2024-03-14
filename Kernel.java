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
        thread = new Thread(this, "Kernel");
        sem = new Semaphore(0);
        scheduler = new Scheduler(this);
        vfs = new VFS();
        
        thread.start();
    }

    // Kernel Interface:

    /**
     * Starts the kernel so It can perform the desired operation.
     */
    public void start(){
        sem.release();      // Increments the sem allowing the thread to run.
    }

    /**
     * The functionality inside the Kernel's Thread.
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

                case GETPID:
                    dbMes("Get this PID");
                    getThisPID();
                    break;

                case SEARCHPID:
                    dbMes("GetPID");
                    getPID(OS.parameters.get(0));
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

                case SEND_MESSAGE:
                    dbMes("Send Message.");
                    sendMessage(OS.parameters.get(0));
                    break;
                
                case GET_MESSAGE:
                    dbMes("Get Message.");
                    waitForMessage();
                    break;
                
                default:
                    dbMes("Unknown Current Call: " + OS.currentCall);
            }
            
            dbMes("Resuming currentProcess: " + scheduler.getCurrentlyRunning().toString());
            scheduler.getCurrentlyRunning().start();
        }
    }

    // Call Methods:

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

        // Device Management:

    /**
     * Opens the desired Device.
     * 
     * @param device A string corresponding to the desired Device.
     */
    private void open(Object device){
        if (device instanceof String){


            int index = vfs.open((String) device);
            int pcbIndex = -1;

            // Check if the VFS opened the Device.
            if(index != -1){        // On success, try to add to PCB's Device index.
                dbMes("VFS didn't fail, index: " + index);
                pcbIndex = scheduler.getCurrentlyRunning().open(index);

                if(pcbIndex == -1){  // If adding to PCB's index fails, close the correct vfs entry.
                    vfs.close(index);
                    dbMes("PCB didn't have any availible room.");
                }
                else{
                    dbMes("PCB index: " + pcbIndex);
                }
            }
            else{
                dbMes("VFS.open() failed.");
            }
            OS.retval = pcbIndex;   // Return the pPCB's index to the ULP. Will be -1 on a failure.
        }
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
        // Check the parameters for validity
        if(id instanceof Integer && size instanceof Integer){
            // Make sure the id is in the correct range.
            if ((int) id >= 0 && (int) id < Device.DEVICE_COUNT){
                // Return the data.
                OS.retval = vfs.read(scheduler.getCurrentlyRunning().getFID((int) id), (int)size);
            }
            else{                
                // If its outside the range, return failure.
                dbMes("READ_ERROR: FID: " + (int) id + " out of bounds");
                OS.retval = -1; 
            }
        }
        else{
            dbMes("Objects passed to Kernel.read() were not integers.");
            OS.retval = -1;     // If Objects are not valid, return failure.
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
            // Make sure the id is in the correct range.
            if ((int) id >= 0 && (int) id < Device.DEVICE_COUNT){
                vfs.seek(scheduler.getCurrentlyRunning().getFID((int) id), (int)to);
                dbMes("Seeked.");
            }
            else{
                dbMes("SEEK_ERROR: FID " + (int) id + " out of bounds.");
            }
        }
        else{
            dbMes("Objects passed to Kernel.read() were not integers.");
        }
    }

    /**
     * Writes to the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param data The data to be writen to the Device.
     */
    public void write(Object id, Object data) {
        //dbMes("id type: " +id.getClass() + " data type: " + data.getClass());
        if(id instanceof Integer && data instanceof byte[]){
            // Make sure the id is in the correct range.
            if ((int) id >= 0 && (int) id < Device.DEVICE_COUNT){
                // Otherwize return the data.
                OS.retval = vfs.write(scheduler.getCurrentlyRunning().getFID((int) id), (byte[])data);
            } 
            else{
                // If its outside the range, return failure.
                dbMes("WRITE_ERROR: FID " + (int) id + " out of bounds.");
                OS.retval = -1; 
            }
        }
        else{
            dbMes("WRITE_ERROR: Objects passed to Kernel.write() were not of the right type.");
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
            // Make sure the FID is in the correct range.
            if ((int) id >= 0 && (int) id < Device.DEVICE_COUNT){
                vfs.close(scheduler.getCurrentlyRunning().close((int) id));
            }
            else{
                dbMes("CLOSE_ERROR: FID " + (int) id + " out of bounds.");
            }
        }
        else{
            dbMes("Object passed to Kernel.close() was not an integer.");
        }
    }

    /**
     * Returns the PID of the currentlyRunning process.
     */
    public void getThisPID(){
        OS.retval = scheduler.getCurrentlyRunning().getPID();
    }

    /**
     * Returns the PID of the named process.
     * 
     * @param name The name of the desired Process.
     */
    public void getPID(Object name){

        if(name instanceof String){
            OS.retval = scheduler.getPID((String) name);
        }
        else{
            OS.retval = -1;
        }
    }

    /**
     * Adds a message to the specified process.
     * 
     * @param message The message to be sent
     */
    public void sendMessage(Object message){
        if(message instanceof Message){
            Message mes = new Message(scheduler.getCurrentlyRunning().getPID(), (Message) message);
            if(!scheduler.sendMessage(mes)){
                dbMes("Sending message failed.");
            }
        }
        else{
            dbMes("OS: Object passed to kernel is not a message.");
        }
    }

    /**
     * Returns the first message in the process's message queue.
     */
    public void waitForMessage(){
        Message retMessage = scheduler.getCurrentlyRunning().getMessage();
        if(retMessage != null)
            OS.retval = retMessage;
        else 
            scheduler.waitForMessage();
    }

    // Helper Methods:

    /**
     * An accessor for the currentlyRuning PCB in scheduler.
     * 
     * @return The currentlyRunning PCB in schedueler
     */
    public PCB getCurrentlyRunning(){
        return scheduler.getCurrentlyRunning();
    }

    /**
     * Stops the currently running process so the kernel can start.
     *
    public void stopCurrentProcesss() {
        if (scheduler.getCurrentlyRunning() != null){
            dbMes("Stopping: " + scheduler.getCurrentlyRunning().getClass());
            scheduler.getCurrentlyRunning().stop();
        }
        else{
            dbMes("Current Process is null");
        }
    }*/
    
    /**
     * DEBUGGING HELPER!!!
     * 
     * @param message The debug message.
     */
    private void dbMes(String message){
        OS.dbMes("|KERNEL: " + message);
    }
}
