public class MultiFIle extends UserLandProcess{

    public void main() {

        System.out.println("Opening devices.");

        // Open file
        int ffsFID = OS.open("FILE test.txt");
        if(ffsFID == -1){
            dbMes("ERROR: Issue opening test.txt");
        }

        // Open rand
        int randFID = OS.open("RAND 69");
        if(randFID == -1){
            dbMes("ERROR: Issue opening test.txt");
        }

        while(true){

            System.out.println("Reading from Rand");
            byte[] data = OS.read(randFID, 1);
            System.out.printf("Read %d from Rand\n", data[0]);

            System.out.println("Writing to test.txt");
            if(OS.write(ffsFID, data) == -1)
                dbMes("ERROR: Issue writing to test.txt");;

            OS.sleep(30);
        }
    }
    
}
