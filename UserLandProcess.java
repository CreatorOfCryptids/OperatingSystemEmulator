import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{
    
    private Thread thread;
    private Semaphore sem;
    private boolean isExpired;

    UserLandProcess(){
        thread = new Thread(this);
        sem = new Semaphore(0);
        isExpired = false;

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
     * @return true when the java thread is not alive.
     */
    public boolean isDone(){
        return !thread.isAlive();
    }

    /**
     * Releases (inclements) the semaphore, allowing this thread to run.
     */
    public void start(){
        dbMes("Start");
        sem.release();
    }

    /**
     * Acquires (decriments) the semaphore, stoping this thread from running.
     */
    public void stop(){
        dbMes("Stop");
        sem.acquireUninterruptibly();
    }

    /**
     * Aquires the semaphore, then calls main.
     */
    public void run(){
        dbMes("Run");
        sem.acquireUninterruptibly();
        main();
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

    private void dbMes(String message){
        OS.dbMes("USERLAND_PROCESS (" + this.getClass() + "): " + message);
    }
}