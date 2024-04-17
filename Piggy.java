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
            System.out.println("Oink!!! " + number + " pointer = " + pointer);
            for(int j = 0; j<99; j++){
                for(int i=0; i<oink.length;i++){
                    write(pointer+i+(j*UserLandProcess.PAGE_SIZE), oink[i % oink.length]);
                    cooperate();
                }
            }
            
            OS.sleep(1500);
        }
    }
}
