public class PCB {
    
    private static int nextPID = 0;
    private int pid;
    private UserLandProcess ulp;
    private OS.Priority priority;
    private int timeouts;

    public PCB(UserLandProcess up, OS.Priority priority){
        this.ulp = up;
        this.pid = nextPID++;
        this.priority = priority;
        this.timeouts = 0;
    }

    /**
     * Calls request stop on the ULP.
     */
    public void requestStop(){

        dbMes("Request Stop");

        ulp.requestStop();
        timeOut();
    }

    /**
     * Stops the ULP.
     */
    public void stop(){

        dbMes("Stop");

        ulp.stop();

        while(ulp.isStopped() == false){
            try{
                Thread.sleep(10);
            } catch (Exception e){
                dbMes("ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * Checks if the process is done running
     * @return True if the process is done running, false otherwize.
     */
    public boolean isDone(){
        return ulp.isDone();
    }

    /**
     * Checks if the proess is stopped.
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
     * DEBUGGING HELP!
     * 
     * @return A String containing the name of the ULP.
     */
    public String toString(){
        return "PCB: " + ulp.getClass();
    }

    private void dbMes(String Message){
        OS.dbMes("PCB (" + ulp.getClass() + "): " + Message);
    }
}
