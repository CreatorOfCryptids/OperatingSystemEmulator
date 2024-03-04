import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{
    
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
        main();

        // I am doing this because thead.isAlive() didn't give us the right answer and This wouldn't be able to cooperate the right way. 
        isDone = true;
        System.out.println("I died.");
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
     * DEBUGGING HELPER!!!
     * 
     * @param message The debugging message.
     */
    protected void dbMes(String message){
        OS.dbMes("USERLAND_PROCESS (" + this.getClass() + "): " + message);
    }
}