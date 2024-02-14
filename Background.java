public class Background extends UserLandProcess{

    public void main() {
        while(true){
            for(int i = 0; i<5; i++){
                System.out.println("Background.");

                // Chill pill
                try {
                    Thread.sleep(40);
                }
                catch (Exception e) {}
                
                cooperate();
            }
            OS.sleep(200);
        }
    }
    
}
