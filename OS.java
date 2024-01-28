import java.util.ArrayList;

public class OS {
    
    private static Kernel kernel;
    public static CallType currentCall;
    public static ArrayList<Object> parameters = new ArrayList<Object>();
    public static Object retval;

    public enum CallType{
        CREATE, INIT, IDLE, SWITCH
    }

    public static int createProcess(UserLandProcess up) {
        // Reset the parameters
        retval = null;
        parameters.clear();

        // Add paremeters to list
        parameters.add(up);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal\
        kernel.start();

        // return PID
        return up.getPID();
    }

    public static void startUp(UserLandProcess init){
        
        kernel = new Kernel();

        createProcess(init);
        createProcess(new IdleProcess());
    }

    public static void switchProcess(){
        currentCall = CallType.SWITCH;
        kernel.start();
    }

}
