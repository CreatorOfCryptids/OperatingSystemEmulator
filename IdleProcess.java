public class IdleProcess extends UserLandProcess{
    public void main(){
        while(true){    
            System.out.println("IDLE: Idle :)");

            cooperate();
            try {
                Thread.sleep(50);
            } catch(Exception e) {}
        }
    }
}