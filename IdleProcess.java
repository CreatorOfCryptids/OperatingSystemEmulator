public class IdleProcess extends UserLandProcess{
    public void main(){
        
        System.out.println("Idle :)");

        while(true){    
            cooperate();
            try {
                Thread.sleep(50);
            } catch(Exception e) {}
        }
    }

    public String toString(){
        return "Idle";
    }
}