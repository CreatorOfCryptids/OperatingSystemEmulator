public class PigMother extends UserLandProcess{
    
    public void main(){

        for(int i = 0; i<11; i++){
            OS.createProcess(new Piggy(i), OS.Priority.BACKGROUND);
            cooperate();
        }

        System.out.println("\nOINK OINKED, your memory is yOINKed!!!");
        System.out.printf("\n<( ,  , )>\n(  (..)  )\tOink Oink!!! Some processes' memory useage is more equal than others!!!\n    --\n\n");
    }
}
