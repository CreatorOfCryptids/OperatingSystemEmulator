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
        // Reset the parameters
        retval = null;
        parameters.clear();

        // Add paremeters to list
        parameters.add(up);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal
        kernel.start();

        // return PID
        return up.getPID();
    }

    /**
     * Starts the initial process of the OS.
     * @param init The first process to run.
     */
    public static void startUp(UserLandProcess init){
        
        kernel = new Kernel();
        parameters = new ArrayList<Object>();

        createProcess(init);
        createProcess(new IdleProcess());
    }

    /**
     * Switches the current process.
     */
    public static void switchProcess(){
        //System.out.println("OS.switch");
        currentCall = CallType.SWITCH;
        kernel.start();
    }

}
