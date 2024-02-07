public class PCB {
    
    private static int nextPID = 0;
    private int pid;
    private UserLandProcess up;

    public PCB(UserLandProcess up){
        this.up = up;
        this.pid = nextPID++;
    }

    public void stop(){

        dbMes("Stop");

        up.stop();
        while(up.isStopped() == false){
            try{
                Thread.sleep(10);
            } catch (Exception e){
                dbMes("ERROR: " + e.getMessage());
            }
        }
    }

    public boolean isDone(){
        return up.isDone();
    }

    public void start() {

        dbMes("Start");

        up.start();
    }

    public int getPID(){
        return pid;
    }

    private void dbMes(String Message){
        OS.dbMes("PCB: " + Message);
    }
}
