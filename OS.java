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

        OS.debug("OS: Creating new Process: " + up.getClass());

        // Reset the parameters
        parameters.clear();

        // Add paremeters to list
        parameters.add(up);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal
        switchToKernel();

        // TODO: return PID 
        //while (retval == null){}
        //return (int) retval;
        while(true){
            try{
                return (int) retval;
            } catch (Exception e){}
        }
        //return pid;
    }

    /**
     * Starts the initial process of the OS.
     * @param init The first process to run.
     */
    public static void startUp(UserLandProcess init){
        
        OS.debug("OS: StartUp");

        kernel = new Kernel();
        parameters = new ArrayList<Object>();
        retval = new Object();

        createProcess(init);
        createProcess(new IdleProcess());
    }

    public static void switchProcess(){
        OS.debug("OS: Switching process");
        // Reset the parameters
        retval = null;
        parameters.clear();

        // Add paremeters to list

        // Set currentCall
        currentCall = CallType.SWITCH;

        // Switch to Kernal
        switchToKernel();
    }

    /**
     * Switches the current process.
     */
    public static void switchToKernel(){
        OS.debug("OS: Switching to kernel");

        kernel.start();

        kernel.stopCurrentProcesss();
    }

    public static void debug(String message){
        //System.out.println(message);
    }
}
