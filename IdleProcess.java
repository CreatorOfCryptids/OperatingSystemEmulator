public class IdleProcess extends UserLandProcess{

    public void main(){
        while(true){
            cooperate();
            try {
                Thread.sleep(50);
            } catch(Exception e) {}
        }
    }

}