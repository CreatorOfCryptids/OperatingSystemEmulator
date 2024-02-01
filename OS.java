import java.util.ArrayList;

public class OS {
    
    private static Kernel kernel;
    public static CallType currentCall;
    public static ArrayList<Object> parameters;
    public static Object retval;

    public enum CallType{
        CREATE, INIT, IDLE, SWITCH
    }

    /**
     * Adds a process to the schedule.
     * @param up The process to be added.
     * @return The PID of the process.
     */
    public static int createProcess(UserLandProcess up) {

        System.out.println("OS: Creating new Process");

        // Reset the parameters
        parameters.clear();

        // Add paremeters to list
        parameters.add(up);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal
        kernel.start();

        // TODO: return PID 
        //return (int) retval;
        return up.getPID();
    }

    /**
     * Starts the initial process of the OS.
     * @param init The first process to run.
     */
    public static void startUp(UserLandProcess init){
        
        kernel = new Kernel();
        parameters = new ArrayList<Object>();
        retval = new Object();

        createProcess(init);
        createProcess(new IdleProcess());
    }

    public static void switchProcess(){
        // Reset the parameters
        retval = null;
        parameters.clear();

        // Add paremeters to list

        // Set currentCall
        currentCall = CallType.SWITCH;

        // Switch to Kernal
        kernel.start();
    }

    /**
     * Switches the current process.
     */
    public static void switchToKernel(){
        //System.out.println("OS.switch");
        kernel.start();
        //TODO: how stop()?
        // kernel.scheduler.currentlyRunning.stop();//
    }

}
