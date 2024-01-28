public class GoodbyeWorld extends UserLandProcess{

    public void main() {
        while(true){
            System.out.println("Goodbye, World!");

            // Chill pill
            try {
                Thread.sleep(50);
            }
            catch (Exception e) {}

            cooperate();
        }
    }
    
}
