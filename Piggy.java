public class Piggy extends UserLandProcess{
    
    int number;

    public Piggy(int number){
        super();
        this.number = number;
    }

    public void main(){

        int pointer = OS.allocateMemory(100 * UserLandProcess.PAGE_SIZE);

        cooperate();

        while(true){
            cooperate();
            System.out.println("Oink!!! " + number);
            OS.sleep(1500);
        }
    }
}
