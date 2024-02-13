public class GoodbyeWorld extends UserLandProcess{

    public void main() {
        while(true){
            for(int i = 0; i<5; i++){
                System.out.println("Goodbye, Mars!");

                // Chill pill
                try {
                    Thread.sleep(20);
                }
                catch (Exception e) {}
                
                cooperate();
            }
            OS.sleep(200);
        }
    }    
}
