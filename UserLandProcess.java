import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{
    
    private Thread thread;
    private Semaphore sem;
    private boolean isExpired;
    public static int processCount = 0;
    private int PID;

    UserLandProcess(){
        thread = new Thread();
        sem = new Semaphore(0);
        isExpired = false;
        PID = processCount++;

        thread.start();
    }
    
    /**
     * Sets the boolean indicating that this process' quantum has expired.
     */
    public void requestStop(){
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
        sem.release();
    }

    /**
     * Acquires (decriments) the semaphore, stoping this thread from running.
     */
    public void stop(){
        sem.acquireUninterruptibly();
    }

    /**
     * Aquires the semaphore, then calls main.
     */
    public void run(){
        while(thread.isAlive()){
            try{
                sem.acquire();
            } catch(Exception e){}
        }
        main();
    }

    /**
     * If the task is expired, then it unexpiers it and calles OS.switchProcesss()
     */
    public void cooperate(){
        //System.out.println("Cooperate");
        if(isExpired == true)
            isExpired = false;
        OS.switchProcess();
    }
    
    /**
     * EXTRA FUNCTION. Accesses the PID of the Userland Process
     * @return The PID of the program.
     */
    public int getPID(){
        return PID;
    }
}