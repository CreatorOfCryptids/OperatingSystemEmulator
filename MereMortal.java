public class MereMortal extends UserLandProcess{

    public void main() {
        for(int i=0; i<2; i++){
            int fid = OS.open("RAND 300");

            System.out.printf("I AM MORTAL, I WILL DIE ONE DAY. FID = %d, Rand = %d\n", fid, OS.read(fid, 1)[0]);

            OS.sleep(20);
        }

        System.out.println("I WILL DIE NOW, AVENGE MEEEEE!!!"); // Idek, but this tests what happens, and lets us know it's dead.
    }
}
