public class Reader extends UserLandProcess{
    public void main(){
        
        
        while(true){
            
            // Open file
            System.out.println("READER: open file");
            int ffsFID = OS.open("FILE test.txt");
            if(ffsFID == -1){
                dbMes("ERROR: Issue opening test.txt");
            }

            cooperate();

            System.out.println("Reader: Seeking");
            OS.seek(ffsFID, 0);

            // Read file.
            System.out.println("Reader: Reading");
            byte output = OS.read(ffsFID, 1)[0];
            System.out.printf("READER: From file: %d\n", output);

            cooperate();

            // Close file
            System.out.println("READER: Closing File");
            OS.close(ffsFID);


            // Sleep to avoid ban hammer.
            OS.sleep(200);
        }
    }
}
