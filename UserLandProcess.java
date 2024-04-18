import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{

    public static final int PAGE_SIZE = 1024;
    public static final int PAGE_COUNT = 1024;
    public static final int MEM_SIZE = PAGE_COUNT*PAGE_SIZE;   // 1024 Pages with 1024 bytes each.
    public static int[][] tlb = new int[][]{{-1,-1},{-1,-1}};  // [][0 = Virtual, 1 = Physical]
    
    public static byte[] memory = new byte[MEM_SIZE];    // Virtual memory.

    private Thread thread;      // The thread for this process
    private Semaphore sem;      // The semaphore for this process
    private boolean isExpired;  // Is this process timed out?
    private boolean isDone;     // Is this process done?

    /**
     * Constructor.
     */
    UserLandProcess(){
        thread = new Thread(this, ("ULP: " + this.getClass()));
        sem = new Semaphore(0);
        isExpired = false;
        isDone = false;

        thread.start();
    }
    
    /**
     * Sets the boolean indicating that this process' quantum has expired.
     */
    public void requestStop(){
        dbMes("Stop Requested");
        isExpired = true;
    }

    /**
     * Will represent the main of our "program."
     */
    public abstract void main();

    /**
     * Indicates if the semaphore is 0.
     * @return Returns true if the semaphore is 0.
     */
    public boolean isStopped(){
        return (sem.availablePermits() == 0);
    }

    /**
     * Friendship ended with thread.isAlive(). Keeping track of it ourseves is the real homie now.
     * 
     * @return true when the java thread is not alive.
     */
    public boolean isDone(){
        return isDone;
    }

    /**
     * Releases (inclements) the semaphore, allowing this thread to run.
     */
    public void start(){
        sem.release();
        dbMes("Start, sem: " + (sem.availablePermits()));
    }

    /**
     * Acquires (decriments) the semaphore, stoping this thread from running.
     */
    public void stop(){
        dbMes("Stop, sem: " + (sem.availablePermits()));
        sem.acquireUninterruptibly();
        
    }

    /**
     * Aquires the semaphore, then calls main.
     */
    public void run(){
        dbMes("Run");
        sem.acquireUninterruptibly();
        try{
            main();
            isDone = true;
            System.out.println(this.getClass().getSimpleName() + " died.");
        }
        catch(Exception e){
            // Catch exceptions so that they don't effect other processes.
            System.out.println(e.getLocalizedMessage());
            isDone = true;
            System.out.println(this.getClass().getSimpleName() + " failed.");
        }

        // I am doing this because thead.isAlive() didn't give us the right answer and This wouldn't be able to cooperate the right way. 
        
        OS.switchProcess();
    }

    /**
     * If the task is expired, then it unexpiers it and calles OS.switchProcesss()
     */
    public void cooperate(){
        dbMes("Cooperate, isExpired: " + isExpired);
        if(isExpired == true){
            isExpired = false;
            OS.switchProcess();
            //sem.acquireUninterruptibly();
        }
    }

    /**
     * Reads data from memory.
     * 
     * @param address The address to be read.
     * @return The memory in said address, or -1 on failure.
     */
    public byte read(int address){
        dbMes("Reading from " + address);
        int mapping = getPhysicalAddress(address);
        if (mapping != -1){
            return memory[mapping];
        }
        else return -1;

    }

    /**
     * Writes data to memory.
     * 
     * @param address The address to be writen to.
     * @param value The value to be writen.
     */
    public void write(int address, byte value){
        
        int mapping = getPhysicalAddress(address);

        if (mapping != -1){
            dbMes("Writing " + value + " to mapping " + mapping);
            memory[mapping] = value;
        }
        else{
            dbMes("Writing failed.");
        }

    }

    /**
     * Clears the TLB entries
     */
    public static void clearTLB(){
        tlb[0][0] = -1;
        tlb[0][1] = -1;
        tlb[1][0] = -1;
        tlb[1][1] = -1;
    }

    /**
     * Helper Method: Gets the physical address from the procvided virtual address.
     * 
     * @param address The desired virtal address in memory.
     * @return The physical address of the specified virtal memory address.
     */
    private int getPhysicalAddress(int address){

        int page = address / 1024;
        int offset = address % 1024;
        
        // Check if TLB has the virtual memory.
        if (tlb[0][0] == page){
            return tlb[0][1] * PAGE_SIZE + offset;
        }
        else if (tlb[1][0] == page){
            return tlb[1][1] * PAGE_SIZE + offset;
        }
        
        // If the TLB doesn't have the virual memory map, get the virtual memory from OS.
        OS.getMapping(page);

        // Check for the memory from OS.
        if (tlb[0][0] == page){
            return tlb[0][1] * PAGE_SIZE + offset;
        }
        else if (tlb[1][0] == page){
            return tlb[1][1] * PAGE_SIZE + offset;
        }
        else{
            dbMes("getPhysicalMapping(): Didn't find page address after calling OS.getMapping(" + page + ").");
            dbMes("TLB: \n" + tlb[0][0] + " " + tlb[0][1] +"\n" + tlb[1][0] + " " + tlb[1][1]);
            return -1;
        }
    }

    /**
     * DEBUGGING HELPER!!!
     * 
     * @param message The debugging message.
     */
    protected void dbMes(String message){
        OS.dbMes("USERLAND_PROCESS (" + this.getClass() + "): " + message);
    }
}