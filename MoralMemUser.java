public class MoralMemUser extends UserLandProcess{
    public void main(){

        System.out.println("Allocating memory");

        int pointer = OS.allocateMemory(3072);

        System.out.println("Memory allocated at " + pointer);

        cooperate();

        System.out.println("I will die now. Later looser");
    }
}
