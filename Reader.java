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
            String output = new String(OS.read(ffsFID, 4));
            System.out.println("READER: From file: " + output);

            cooperate();

            // Close file
            System.out.println("READER: Closing File");
            OS.close(ffsFID);


            // Sleep to avoid ban hammer.
            OS.sleep(200);
        }
    }
}
