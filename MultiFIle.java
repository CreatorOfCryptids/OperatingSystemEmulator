public class MultiFIle extends UserLandProcess{

    public void main() {

        int ffsFID = OS.open("FILE test.txt");
        int randFID = OS.open("RAND 69");

        while(true){

            System.out.println("Writing to test.txt");
            byte[] data = OS.read(randFID, 1);
            OS.write(ffsFID, data);

            OS.sleep(30);
        }
    }
    
}
