import java.util.ArrayList;

public class OS {
    
    private static Kernel kernel;
    public static CallType currentCall;
    public static ArrayList<Object> parameters;
    public static Object retval;

    public enum CallType{
        CREATE, INIT, IDLE, SWITCH
    }

    public static int createProcess(UserLandProcess up){
        // Reset the parameters
        retval = null;
        parameters.clear();
        // Add paremeters to list

        // Set currentCall
        currentCall = CallType.CREATE;
        // TODO: Switch to Kernal
        kernel.start();
        // Cast and return retval
    }

    public static void startUp(UserLandProcess init){
        kernel = new Kernel();

        createProcess(init);

        createProcess(new IdleProcess());
    }

    public void switchProcess(){
        // TODO: Eventually
    }

}
