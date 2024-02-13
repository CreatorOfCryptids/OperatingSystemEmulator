public class Insomniac extends UserLandProcess{

    public void main() {
        while(true){
            System.out.println("Never sleep :(");
            // Chill pill
            try {
                Thread.sleep(50);
            }
            catch (Exception e) {}
            
            cooperate();
        }
    }
}
