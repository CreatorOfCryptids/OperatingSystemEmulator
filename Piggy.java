public class Piggy extends UserLandProcess{
    
    int number;

    public Piggy(int number){
        super();
        this.number = number;
    }

    public void main(){

        System.out.println("Allocating memory now Oink!!!");

        int pointer = OS.allocateMemory(99 * UserLandProcess.PAGE_SIZE).get();
        byte[] oink = ("OINK!!! " + number).getBytes();
        cooperate();

        for(int j=0; j<PCB.MEM_MAP_SIZE-1; j++){
            for(int i=0; i<oink.length;i++){
                write(pointer+i+(j*UserLandProcess.PAGE_SIZE), oink[i % oink.length]);
            }
        }
        
        while(true){
            cooperate();
            
            // for(int i=0; i<oink.length;i++){
            //     write(pointer+i+(0*UserLandProcess.PAGE_SIZE), oink[i % oink.length]);
            // }
            
            System.out.println("Oink!!! " + number + " pointer = " + pointer);
            
            OS.sleep(1500);
        }
    }
}
