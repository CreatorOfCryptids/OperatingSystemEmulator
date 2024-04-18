import java.util.LinkedList;
import java.util.Optional;

public class PCB{
    
    private static int nextPID = 0;             // Stores the number of PCBs.
    private int pid;                            // This PCB's PID.
    public static final int MEM_MAP_SIZE = 100;// The size of the memoryMap.

    private UserLandProcess ulp;                // The ULP under this PCB's control
    private OS.Priority priority;               // The ULP's priority.
    private int timeouts;                       // The number of times that this ULP has gone to timeout.

    private int[] deviceIDs;                    // The index of differnt Devices that this ULP has access to.
    private VirtualToPhysicalMap[] memoryMap;   // Stores this processes data. The index is the spot in virual memeory, and the value is the physical/disk address.

    private LinkedList<Message> messages;       // The queue of messages sent to this process.
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

        this.deviceIDs = new int [Device.DEVICE_COUNT];
        for(int i=0; i<Device.DEVICE_COUNT;i++) {
            deviceIDs[i] = -1;
        }

        this.memoryMap = new VirtualToPhysicalMap[MEM_MAP_SIZE];
        // for(int i=0; i<MEM_MAP_SIZE; i++){
        //     memoryMap[i] = new VirtualToPhysicalMap();
        // }

        this.messages = new LinkedList<Message>();
        this.awaitingMessage = false;
    }

    // Interfacing with the ULP:

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

    // Message Mangagement:

    /**
     * Adds a new message to this process's message queue.
     * 
     * @param mes The new message.
     */
    public void addMessage(Message mes){
        dbMes("New message added.");
        messages.add(mes);
    }

    /**
     * Gets the first message in the message queue.
     * 
     * @return The first message in the message queue, null if there isn't anything in the queue.
     */
    public Message getMessage(){

        if(messages.isEmpty() == false){
            dbMes("Messages is not empty");
            awaitingMessage = false;
            return messages.removeFirst();
        }
        else{
            dbMes("Messages is empty :(");
            awaitingMessage = true;
            return null;
        }
    }

    /**
     * Accessor for awaitingMessage.
     * 
     * @return True, if this PCB is expecting a message. False if not.
     */
    public boolean isWaitingForMessage(){
        return awaitingMessage;
    }

    // Memory Management:

    /**
     * Returns the VirtualToPhysicalMaps of the provided virtualPageNumber.
     * 
     * @param virtualPageNum The virtual pointer that corresponds to a phycical address in memory.
     * @return The VirtualToPhysicalAddress address of the virtual pointer. COULD RETURN NULL
     */
    public VirtualToPhysicalMap getMemoryMapping(int virtualPageNum){
        return memoryMap[virtualPageNum];
    }

    /**
     * Creates a virtual memory mapping for a physical page address.
     * 
     * @param physicalAddress The address for the start of the allocated physical page.
     * @return The virtual memory address, or -1 on failure.
     */
    public int allocateMemory(VirtualToPhysicalMap[] physicalAddresses){
        // Find empty memory with enough space.
        int index = -1;

        // Loop looking for a sequence of empty entries
        for(int i=0; i<MEM_MAP_SIZE - physicalAddresses.length; i++){   // Stop when there isn't enough size left in the map for a continuous allocation.

            // If we find an empty entry, store the start index, and check if it has a large enough size.
            if (memoryMap[i] == null){
                index = i;

                // Loop until we hit a non-empty index, we hit the end of the map, or we get to the rght size.
                for(; i<MEM_MAP_SIZE && i<(index + physicalAddresses.length); i++){
                    if (memoryMap[i] != null){
                        index = -1;
                        break;
                    }
                }

                // If the index is valid, exit out of the search loop.
                if (index != -1){
                    break;
                }
            }
        }

        // If we found a valid index, put the physical addresses into the memory map.
        if (index != -1){

            dbMes("AllocateMemory(): Valid address found!");

            for(int i = 0; i<physicalAddresses.length; i++)
                memoryMap[index + i] = physicalAddresses[i];
        }
        else{
            dbMes("Invalid address.");
        }
            

        // Return the start index. This will return -1 if a valid entry isn't found.
        return index;
    }

    /**
     * Removes the designated pages from the memory map.
     * 
     * @param virtualPagePointer The start of the virtual pages to be cleared.
     * @param size The amount of pages to be removed.
     */
    public int[] freeMemory(int virtualPagePointer, int size){
        int[] freedMemory = new int[size];
        for(int i = 0; i<size && i<MEM_MAP_SIZE; i++){
            if (memoryMap[virtualPagePointer + i].physicalPageNum.isPresent())
                freedMemory[i] = memoryMap[virtualPagePointer + i].physicalPageNum.get();
            else
            freedMemory[i] = -1;
            memoryMap[virtualPagePointer + i] = null;
        }
        return freedMemory;
    }

    /**
     * The getMemoryMapping() accessor.
     * 
     * @return The memoryMap of this PCB.
     */
    public VirtualToPhysicalMap[] getMemoryMapping(){
        return memoryMap;
    }

    /**
     * Returns the first physical page in use by this PCB.
     * 
     * @return An Optional containing a VirtualToPhysicalMap with a physical address.
     */
    public Optional<VirtualToPhysicalMap> getPhysicalPage(){
        for(int i = 0; i<MEM_MAP_SIZE; i++)
            if (memoryMap[i] != null && memoryMap[i].physicalPageNum.isPresent())
                return Optional.of(memoryMap[i]);
        
        return Optional.empty();
    }

    // Debugging helper methods:

    /**
     * DEBUGGING HELPER!!! Accessor for the size of the messages queue.
     * 
     * @return The size of the messages queue.
     */
    public int getMessagesSize(){
        return messages.size();
    }

    /**
     * DEBUGGING HELPER!!!
     * 
     * @return A String containing the name of the ULP.
     */
    public String toString(){
        return "PCB: " + ulp.getClass();
    }

    /**
     * DEBUGGING HELPER!!!
     * 
     * @param Message
     */
    private void dbMes(String Message){
        OS.dbMes("||PCB (" + ulp.getClass() + "): " + Message);
    }
}
