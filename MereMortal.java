public class MereMortal extends UserLandProcess{

    public void main() {

        System.out.println("Starting MereMortal.");

        for(int i=0; i<3; i++){

            System.out.println("MORTAL: Opening RandomDevice");
            int fid = OS.open("RAND 300");
            if(fid == -1){
                dbMes("ERROR: Issue opening test.txt");
            }

            cooperate();

            System.out.printf("I AM MORTAL, I WILL DIE ONE DAY. FID = %d, Rand = %d\n", fid, OS.read(fid, 1)[0]);

            OS.sleep(20);
        }

        System.out.println("I WILL DIE NOW, AVENGE MEEEEE!!!"); // Idek, but this tests what happens, and lets us know it's dead.
    }
}
