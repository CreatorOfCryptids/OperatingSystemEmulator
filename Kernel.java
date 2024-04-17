import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Kernel implements Runnable{

    private Thread thread;
    private Semaphore sem;

    private Scheduler scheduler;
    private VFS vfs;

    private boolean[] freeMemMap;   // Stores which physical memory pages are free (true for free, false for in-use)
    private int swapFID;            // Stores the document used to store overflow memory on disk
    private int pageCount;          // Stores the amount pages writen to memory.

    /**
     * Constructor.
     */
    Kernel(){
        thread = new Thread(this, "Kernel");
        sem = new Semaphore(0);
        scheduler = new Scheduler(this);
        vfs = new VFS();

        freeMemMap = new boolean[UserLandProcess.PAGE_COUNT];
        for(int i = 0; i<UserLandProcess.PAGE_COUNT; i++)
            freeMemMap[i] = true;

        swapFID = vfs.open("FILE swap");
        pageCount = 0;

        
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

                // Process Management:
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
                
                // Device I/O Management:
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

                // Interprocess Communication Management
                case GETPID:
                    dbMes("Get this PID");
                    getPID();
                    break;

                case SEARCHPID:
                    dbMes("GetPID");
                    searchPID(OS.parameters.get(0));
                    break;

                case SEND_MESSAGE:
                    dbMes("Send Message.");
                    sendMessage(OS.parameters.get(0));
                    break;
                
                case WAIT_MESSAGE:
                    dbMes("Get Message.");
                    waitForMessage();
                    break;
                
                // Virtual Memory Management.
                case GET_MAPPING:
                    dbMes("Get Mapping.");
                    getMemoryMapping(OS.parameters.get(0));
                    break;

                case ALLOCATE:
                    dbMes("Allocate Memory.");
                    allocateMemory(OS.parameters.get(0));
                    break;

                case FREE:
                    dbMes("Free memory.");
                    freeMemory(OS.parameters.get(0), OS.parameters.get(1));
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

    // Interprocess Communication Management:

    /**
     * Returns the PID of the currentlyRunning process.
     */
    public void getPID(){
        OS.retval = scheduler.getCurrentlyRunning().getPID();
    }

    /**
     * Returns the PID of the named process.
     * 
     * @param name The name of the desired Process.
     */
    public void searchPID(Object name){

        // Check inputs
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

        // Check inputs.
        if(message instanceof Message){

            // Make copy of the message.
            Message mes = new Message(scheduler.getCurrentlyRunning().getPID(), (Message) message);

            // Check if the message sent correctly, for debugging.
            if(!scheduler.sendMessage(mes)){
                dbMes("Sending message failed.");
            }
        }
        else{
            dbMes("Object passed to kernel is not a message.");
        }
    }

    /**
     * Returns the first message in the process's message queue.
     */
    public void waitForMessage(){

        Message retMessage = scheduler.getCurrentlyRunning().getMessage();

        if(retMessage != null){
            dbMes("Case: Already has message :)");
            OS.retval = retMessage;
        }
        else {
            dbMes("Case: Send into awaitingMessage.");
            scheduler.addToWaitMes();
        }
            
    }

    // Virtual Memory Management:

    /**
     * Adds the specified virtual page into the TLB.
     * 
     * @param virtualPageNum The desired virtual page number.
     */
    private void getMemoryMapping(Object virtualPageNum){

        Random rand = new Random();
        int tlbIndex = rand.nextInt(2);

        // Make sure the ULP is asking for a valid section of memory.
        if (virtualPageNum instanceof Integer && (int) virtualPageNum >= 0 && (int) virtualPageNum <UserLandProcess.PAGE_COUNT){
            
            VirtualToPhysicalMap map = getCurrentlyRunning().getMemoryMapping((int) virtualPageNum);

            if (map != null){
                
                if (map.physicalPageNum.isPresent()){
                    dbMes("getMemoryMapping(): CASE: Physical is present.");
                    UserLandProcess.tlb[tlbIndex][0] = (int) virtualPageNum;
                    UserLandProcess.tlb[tlbIndex][1] = map.physicalPageNum.get();
                }
                else{
                    dbMes("getMemoryMapping(): CASE: Disk Is present.");
                    // Find a physical empty physical page
                    int freePage = -1;

                    for(int i=0; i<UserLandProcess.PAGE_COUNT;i++){
                        if (freeMemMap[i] == true){
                            freePage = i;
                            dbMes("getMemoryMapping(): Found free memory.");
                            break;
                        }
                    }

                    // If an empty page is not found, banish a process's data to disk.
                    if (freePage == -1){

                        dbMes("getMemoryMapping(): Time to banish!!!");

                        Optional<VirtualToPhysicalMap> banished;

                        do{
                            banished = scheduler.getRandomProcess().getPhysicalPage();
                        } while (banished.isPresent() && banished.get().physicalPageNum.isEmpty());

                        freePage = banished.get().physicalPageNum.get();
                        byte[] banishedPage = new byte[UserLandProcess.PAGE_SIZE];

                        for(int i = 0; i<UserLandProcess.PAGE_SIZE; i++){
                            banishedPage[i] = UserLandProcess.memory[freePage + i];
                        }

                        dbMes("GetMemoryMapping() Writing to disk!                                          !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");       

                        vfs.write(swapFID, banishedPage);

                        banished.get().physicalPageNum = Optional.empty();
                        banished.get().diskPageNum = Optional.of(pageCount++);
                    }

                    // Set map's physical page number.
                    map.physicalPageNum = Optional.of(freePage);

                    // Populate data
                    if (map.diskPageNum.isPresent()){
                        vfs.seek(swapFID, map.diskPageNum.get() * UserLandProcess.PAGE_SIZE);
                        byte[] diskPageData = vfs.read(swapFID, UserLandProcess.PAGE_SIZE);
                        vfs.seek(swapFID, pageCount * UserLandProcess.PAGE_SIZE);

                        for(int i=0; i<UserLandProcess.PAGE_SIZE; i++){
                            UserLandProcess.memory[i + (map.diskPageNum.get() * UserLandProcess.PAGE_SIZE)] = diskPageData[i];
                        }
                    }
                    else{
                        for (int i = 0; i< UserLandProcess.PAGE_SIZE; i++)
                            UserLandProcess.memory[(freePage * UserLandProcess.PAGE_SIZE) + i] = 0;
                    }
                }
                // If it's out of bounds, return failure.
                // else{
                    
                //     dbMes("getMapping(): Virtual Page Number " + (int) virtualPageNum + " out of bounds.");
                // }
            }
            else{
                UserLandProcess.tlb[tlbIndex][0] = (int) virtualPageNum;
                    UserLandProcess.tlb[tlbIndex][1] = -1;
                dbMes("getMapping(): Virtual Page Number " + (int) virtualPageNum + " not allocated.");
            }
        }
        else{
            UserLandProcess.tlb[tlbIndex][0] = (int) virtualPageNum;
            UserLandProcess.tlb[tlbIndex][1] = -1;
            dbMes("Object passed to getMemoryMapping() was not an integer or out of bounds.");
            OS.retval = -1;
        }
    }

    /**
     * Adds physical addresses to the currently running process's virtual memory map.
     * 
     * @param size The amount of pages the ULP wants.
     * @return The start of the allocated virtual memory address.
     */
    private void allocateMemory(Object size) {
        // Check inputs.
        if (size instanceof Integer){

            VirtualToPhysicalMap[] VtPM = new VirtualToPhysicalMap[(int)size];
            for (int i = 0; i< (int) size; i++)
                VtPM[i] = new VirtualToPhysicalMap();

            OS.retval = getCurrentlyRunning().allocateMemory(VtPM);

            // int addressCount = 0;

            // OS.retval = -1;     // Set the return value to failure, becuase it will only be changed on success.

            // // Loop thru the open physcial memory to find open physical addresses.
            // for(int i=0; i<UserLandProcess.PAGE_COUNT; i++){

            //     // If the current memory is free, add to list.
            //     if (freeMemMap[i] = true){

            //         physicalAddresses[addressCount++].physicalPageNum = Optional.of(i);

            //         // If we have enough addresses, have PCB add to virtual memory, and break.
            //         if (addressCount == (int) size){
            //             OS.retval = getCurrentlyRunning().allocateMemory(physicalAddresses);
            //             dbMes("OS: allocated " + addressCount + " pages to virtual memory.");

            //             // Set the memory as in use.
            //             for(int j = 0; j<addressCount; j++){
            //                 //if (physicalAddresses[j].physicalPageNum.isPresent())
            //                     freeMemMap[physicalAddresses[j].physicalPageNum.get()] = false;
            //             }

            //             break;
            //         }
            //     }
            // }
        }
        else{
            dbMes("Object passed to kernel is not an integer.");
            OS.retval = -1;
        }
    }
    
    /**
     * Frees the designated memory for use by other processes.
     * 
     * @param pointer The begginging of the virtal memory to be freed.
     * @param size THe amount of virtual memory to be freed.
     * @return True if the memory has been successfully freed. False if otherwize.
     */
    private void freeMemory(Object pointer, Object size) {
        if (pointer instanceof Integer && size instanceof Integer){
            int[] physicalAddress = getCurrentlyRunning().freeMemory((int) pointer, (int) size);
            for(int i=0; i<physicalAddress.length; i++){
                if (physicalAddress[i] != -1)
                    freeMemMap[physicalAddress[i]] = true;
            }

            OS.retval = ((int) size == physicalAddress.length);
        }
        else{
            dbMes("Object passed to kernel is not an integer.");
            OS.retval = false;
        }
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
     * A mutator that frees memory used by dead processes.
     * 
     * @param deadMemory An array with the memory addresses to be freed.
     */
    public void freeDeadMemory(VirtualToPhysicalMap[] deadMemory){
        dbMes("Freeing dead memory.");
        for(int i = 0; i<deadMemory.length; i++)
            if(deadMemory[i] != null && deadMemory[i].physicalPageNum.isPresent())
                freeMemMap[deadMemory[i].physicalPageNum.get()] = true;
    }

    /**
     * DEBUGGING HELPER!!!
     * 
     * @param message The debug message.
     */
    private void dbMes(String message){
        OS.dbMes("|KERNEL: " + message);
    }
}
