public class Piggy extends UserLandProcess{
    
    int number;

    public Piggy(int number){
        super();
        this.number = number;
    }

    public void main(){

        System.out.println("Allocating memory now Oink!!!");

        int pointer = OS.allocateMemory(99 * UserLandProcess.PAGE_SIZE);
        byte[] oink = "OINK!!!".getBytes();
        cooperate();
        

        while(true){
            cooperate();
            
            for(int i=0; i<oink.length;i++){
                write(pointer+i+(0*UserLandProcess.PAGE_SIZE), oink[i % oink.length]);
                cooperate();
            }
            
            System.out.println("Oink!!! " + number + " pointer = " + pointer);
            
            OS.sleep(1500);
        }
    }
}
