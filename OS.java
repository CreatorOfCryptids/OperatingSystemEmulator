import java.util.ArrayList;

public class OS {
    
    private static Kernel kernel;
    public static CallType currentCall;
    public static ArrayList<Object> parameters;
    public static Object retval;

    public enum CallType{
        CREATE, SWITCH
    }

    /**
     * Adds a process to the scheduler.
     * @param up The process to be added.
     * @return The PID of the new process.
     */
    public static int createProcess(UserLandProcess up) {

        OS.dbMes("OS: Creating new Process: " + up.getClass());

        // Reset the parameters
        parameters.clear();
        parameters.add(up);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal
        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){}
        }
    }

    /**
     * Starts the initial process of the OS.
     * @param init The first process to run.
     */
    public static void startUp(UserLandProcess init){
        
        OS.dbMes("OS: StartUp");

        kernel = new Kernel();
        parameters = new ArrayList<Object>();
        retval = new Object();

        createProcess(init);
        createProcess(new IdleProcess());
    }

    /**
     * Switches to the next process in the queue.
     */
    public static void switchProcess(){
        OS.dbMes("OS: Switching process");

        retval = null;
        parameters.clear();

        currentCall = CallType.SWITCH;

        switchToKernel();
    }

    /**
     * Starts the Kernel and stops the current process.
     */
    public static void switchToKernel(){
        OS.dbMes("OS: Switching to kernel");

        kernel.start();

        kernel.stopCurrentProcesss();
    }

    /**
     * Prints a message to the terminal to help bebugging.
     * @param message The message printed to the terminal.
     */
    public static void dbMes(String message){
        //System.out.println(message);
    }
}
