public class HelloWorld extends UserLandProcess{
    public void main(){
        while(true){
            System.out.println("Hello, World!");

            // Chill pill
            try {
                Thread.sleep(50);
            }
            catch (Exception e) {}

            cooperate();
        }
    }
}
