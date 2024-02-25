public class PCB implements Device{
    
    private static int nextPID = 0;
    private int pid;
    private UserLandProcess ulp;
    private OS.Priority priority;
    private int timeouts;
    private int[] deviceIDs;

    public PCB(UserLandProcess up, OS.Priority priority){
        this.ulp = up;
        this.pid = nextPID++;
        this.priority = priority;
        this.timeouts = 0;
        this.deviceIDs = new int [10];
    }

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

    /**
     * Returns the PID of the process.
     * 
     * @return the PID of the process.
     */
    public int getPID(){
        return pid;
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
        OS.dbMes("PCB (" + ulp.getClass() + "): " + Message);
    }
}
