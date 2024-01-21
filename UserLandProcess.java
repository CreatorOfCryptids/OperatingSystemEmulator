import java.util.concurrent.Semaphore;

abstract class UserLandProcess implements Runnable{
    
    Thread thread = new Thread();
    Semaphore sem = new Semaphore(1);
    boolean expired;
    
    void requestStop(){

    }

    abstract void main();

    boolean isStopped(){
        if ()
    }

    boolean isDone(){

    }

    void start(){

    }

    void stop(){

    }

    void run(){

    }

    void cooperateI(){

    }
}