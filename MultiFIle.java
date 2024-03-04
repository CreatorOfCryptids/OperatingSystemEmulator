public class MultiFIle extends UserLandProcess{

    public void main() {
        while(true){

            System.out.println("Opening devices.");

            // Open file
            int ffsFID = OS.open("FILE test.txt");
            if(ffsFID == -1){
                dbMes("ERROR: Issue opening test.txt");
            }

            // Open rand
            int randFID = OS.open("RAND");
            if(randFID == -1){
                dbMes("ERROR: Issue opening test.txt");
            }

            cooperate();

            // Read from rand
            System.out.println("Reading from Rand");
            byte[] data = OS.read(randFID, 4);
            System.out.println("Read "+ new String(data) +" from Rand");
            
            // Write to file
            System.out.println("Writing to test.txt");
            if(OS.write(ffsFID, data) == -1)
                dbMes("ERROR: Issue writing to test.txt");

            cooperate();

            System.out.println("MF: Seeking");
            OS.seek(ffsFID, 0);
            System.out.println("MF: Seeked");

            cooperate();

            // Close files.
            System.out.println("Closing files");
            OS.close(ffsFID);
            OS.close(randFID);
            System.out.println("Files closed.");

            cooperate();

            // Sleep.
            OS.sleep(200);
        }
    }
}
