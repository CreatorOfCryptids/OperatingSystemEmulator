import java.util.LinkedList;

public class PCB{
    
    private static int nextPID = 0;         // Stores the number of PCBs.
    private int pid;                        // This PCB's PID.
    private UserLandProcess ulp;            // The ULP under this PCB's control
    private OS.Priority priority;           // The ULP's priority.
    private int timeouts;                   // The number of times that this ULP has gone to timeout.
    private int[] deviceIDs;                // The index of differnt Devices that this ULP has access to.
    private LinkedList<Message> messages;   // The queue of messages sent to this process.
    private boolean awaitingMessage;

    /**
     * Constructor
     * 
     * @param up The ULP to be managed.
     * @param priority The ULP's priority.
     */
    public PCB(UserLandProcess up, OS.Priority priority){
        this.ulp = up;

        this.pid = nextPID++;
        this.priority = priority;
        this.timeouts = 0;
        this.awaitingMessage = false;

        this.deviceIDs = new int [10];
        for(int i=0; i<Device.DEVICE_COUNT;i++) {
            deviceIDs[i] = -1;
        }

        this.messages = new LinkedList<Message>();
    }

    // Interfacing with the USP:

    /**
     * Calls request stop on the ULP.
     */
    public void requestStop(){

        dbMes("Request Stop");

        ulp.requestStop();

        timeOut();  // If if hasn't put itself to sleep before the interrupt then it has been timed out.
    }

    /**
     * Stops the ULP.
     */
    public void stop(){

        dbMes("Stop");

        ulp.stop();
    }

    /**
     * Checks if the process is done running
     * 
     * @return True if the process is done running, false otherwize.
     */
    public boolean isDone(){
        return ulp.isDone();
    }

    /**
     * Checks if the proess is stopped.
     * 
     * @return True if the process is stopped, false otherwize.
     */
    public boolean isStopped(){
        return ulp.isStopped();
    }

    /**
     * Starts the ULP.
     */
    public void start() {

        dbMes("Start.");

        ulp.start();
    }

    // Keeping Tabs on the ULP:

    /**
     * Returns the PID of the process.
     * 
     * @return the PID of the process.
     */
    public int getPID(){
        return pid;
    }

    /**
     * The getName() accessor.
     * 
     * @return The name of the ULP.
     */
    public String getName(){
        return ulp.getClass().getSimpleName();
    }

    /**
     * Returns the priority of the process.
     * 
     * @return The priority of the process
     */
    public OS.Priority getPriority(){
        return this.priority;
    }

    /**
     * Marks that this process has timed out, and decreases priority if necessary.
     */
    public void timeOut(){

        timeouts++;
        dbMes("Timed out " + timeouts);

        if(timeouts >=5){
            if(this.priority == OS.Priority.REALTIME)
                priority = OS.Priority.INTERACTIVE;
            else if(this.priority == OS.Priority.INTERACTIVE)
                priority = OS.Priority.BACKGROUND;

            dbMes("Demoted. New priority: " + this.priority.toString());
            
            timeouts = 0;
        }
    }

    /**
     * Resets the timeout count so that the process is not unfairly demoted.
     */
    public void timeOutReset(){
        timeouts = 0;
    }

    // Device Management:

    /**
     * Adds the PID of a new Device to this PCB's device array. Returns -1 if there are no available spots.
     * 
     * @param newID The FID of a new Device.
     * @return This PCB's index for that Device, or -1 on failure.
     */
    public int open(int newID){
        for(int i = 0; i<Device.DEVICE_COUNT; i++) {
            if(deviceIDs[i] == -1){
                deviceIDs[i] = newID;
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the FID of a device from this PCB's device index.
     * 
     * @param index
     * @return The FID of the desired Device index.
     */
    public int getFID(int index){
        dbMes("getFID");
        return deviceIDs[index];
    }

    /**
     * Sets the index of a device to be -1.
     * 
     * @param index The index within the device map to be set to -1.
     * @return The FID of the closed device.
     */
    public int close(int index){
        int retval = deviceIDs[index];
        deviceIDs[index] = -1;
        return retval;
    }

    /**
     * Returns the IDs of the process.
     * 
     * @return An array of FIDs.
     */
    public int[] getDeviceIDs(){
        return deviceIDs;
    }

    /**
     * Adds a new message to this process's message queue.
     * 
     * @param mes The new message.
     */
    public void addMessage(Message mes){
        messages.add(mes);
    }

    /**
     * Gets the first message in the message queue.
     * 
     * @return The first message in the message queue, null if there isn't anything in the queue.
     */
    public Message getMessage(){
        if(messages.isEmpty() == false){
            awaitingMessage = false;
            return messages.removeFirst();
        }
        else{
            awaitingMessage = true;
            return null;
        }
    }

    public boolean getAwatingMessage(){
        return awaitingMessage;
    }

    // Debugging helper methods:

    /**
     * DEBUGGING HELPER!
     * 
     * @return A String containing the name of the ULP.
     */
    public String toString(){
        return "PCB: " + ulp.getClass();
    }

    /**
     * DEBUGGING HELPER!
     * 
     * @param Message
     */
    private void dbMes(String Message){
        OS.dbMes("||PCB (" + ulp.getClass() + "): " + Message);
    }
}
