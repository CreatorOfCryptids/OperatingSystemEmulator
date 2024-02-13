import java.util.ArrayList;

public class OS {
    
    private static Kernel kernel;
    public static CallType currentCall;
    public static ArrayList<Object> parameters;
    public static Object retval;

    public enum CallType{
        CREATE, SWITCH, SLEEP
    }

    public enum Priority{
        REALTIME, INTERACTIVE, BACKGROUND
    }

    /**
     * Adds a process to the scheduler.
     * 
     * @param up The process to be added.
     * @return The PID of the new process.
     */
    public static int createProcess(UserLandProcess up) {

        dbMes("OS: Creating new Process: " + up.getClass());

        // Reset the parameters
        parameters.clear();
        parameters.add(up);
        parameters.add(Priority.INTERACTIVE);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal
        switchToKernel();
        int errors = 0;
        //*
        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            
            try{
                return (int) retval;
            } catch (Exception e){
                errors++;
            }
            //dbMes("ERROR: Cannot be cast :" + errors);
        }/**/
    }

    public static int createProcess(UserLandProcess up, Priority priority){

        dbMes("OS: Creating new Process: " + up.getClass() + " Priority: " + priority.toString());

        parameters.clear();
        parameters.add(up);
        parameters.add(priority);

        currentCall = CallType.CREATE;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){}
            dbMes("ERROR: Cannot be cast");
        }/**/
    }

    /**
     * Starts the initial process of the OS.
     * 
     * @param init The first process to run.
     */
    public static void startUp(UserLandProcess init){
        
        dbMes("OS: StartUp");

        kernel = new Kernel();
        parameters = new ArrayList<Object>();
        retval = new Object();

        createProcess(init);
        createProcess(new IdleProcess(), Priority.BACKGROUND);
    }

    /**
     * Switches to the next process in the queue.
     */
    public static void switchProcess(){
        dbMes("OS: Switching process");

        retval = null;
        parameters.clear();

        currentCall = CallType.SWITCH;

        switchToKernel();
    }

    public static void sleep(int milliseconds){

        dbMes("OS: Sleep");

        retval = null;
        parameters.clear();

        parameters.add(milliseconds);
        
        currentCall = CallType.SLEEP;

        switchToKernel();
    }

    /**
     * Starts the Kernel and stops the current process.
     */
    private static void switchToKernel(){
        dbMes("OS: Switching to kernel");

        kernel.start();

        kernel.stopCurrentProcesss();
    }

    /**
     * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
     * 
     * @param message The message printed to the terminal.
     */
    public static void dbMes(String message){
        System.out.println("\t"+message);
    }
}
