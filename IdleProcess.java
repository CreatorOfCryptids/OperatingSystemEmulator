public class IdleProcess extends UserLandProcess{
    public void main(){
        while(true){    
            OS.debug("IDLE: Idle :)");
            cooperate();
            try {
                Thread.sleep(50);
            } catch(Exception e) {}
        }
    }
}