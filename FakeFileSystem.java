import java.io.RandomAccessFile;

public class FakeFileSystem implements Device{

    private RandomAccessFile files[];

    /**
     * Constructor.
     */
    FakeFileSystem(){
        files = new RandomAccessFile[Device.DEVICE_COUNT];
    }

    /**
     * Opens the desired File.
     * 
     * @param device The desired file's name.
     */
    public int open(String s) {

        int fid = -1;
        s = s.trim();   // Trim any uneeded whitespace.

        // Look for an open entry
        for(int i=0; i<Device.DEVICE_COUNT; i++){
            if(files[i] == null){   // If we find one, store the entry.
                fid = i;
                break;
            }
        }
        
           // Get rid of any leading/tailing whitespace.

        dbMes("OPEN: fid = " + fid + " String = \"" + s + "\"");

        if(fid == -1){
            // If we didn't find an open entry, return failure.
            dbMes("ERROR: No availible entry.");
            return -1;
        }
        else if (s != null && s.equals("") == false){
            // If the string is valid, open the file and return the fid.
            try{
                files[fid] = new RandomAccessFile(s, "rw"); //Trim any whitespace from the string.
                dbMes("FID: " + fid);
                return fid;
            }
            catch (Exception e){
                dbMes("Open(): ERROR: " + e.getMessage());
                return -1;
            }
        }
        else 
            return -1;
    }

    /**
     * Reads from the desired File.
     * 
     * @param id The FID of the desired File.
     * @param size The amount of data to be read.
     */
    public byte[] read(int id, int size) {
        byte[] retval = new byte[size];

        dbMes("Read()");

        try{
            files[id].read(retval);
            dbMes("Read: \"" + new String(retval) + "\"");
            return retval;
        }
        catch (Exception e){
            dbMes("Read(): ERROR: " + e.getMessage());
            return new byte[]{-1};
        }
    }

    /**
     * Moves the curser within the file.
     * 
     * @param id The FID of the file.
     * @param to The distance to move the curser.
     */
    public void seek(int id, int to) {
        dbMes("Seek()");
        try{
            files[id].seek(to);
            dbMes("Seeked.");
        }
        catch(Exception e){
            dbMes("Seek(): ERROR: " + e.getMessage());
        }
    }

    /**
     * Writes to the desired File.
     * 
     * @param id The FID of the desired File.
     * @param data The data to be writen to the File.
     */
    public int write(int id, byte[] data) {
        dbMes("Write() \"" + new String(data) + "\"");

        try{
            files[id].write(data);
            return data.length;
        } 
        catch(Exception e){
            dbMes("Write(): ERROR: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Closes the desired File.
     * 
     * @param id The FID of the desired File.
     */
    public void close(int id) {
        dbMes("Close()");
        if(files[id] == null){
            dbMes("Close(): ERROR: Null device");
        }
        else{
            try{
                files[id].close();
            }
            catch (Exception e){
                dbMes("Close(): ERROR: " + e.getMessage());
            }

            files[id] = null;
        }
    }

    /**
    * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
    * 
    * @param message The message printed to the terminal.
    */
    private void dbMes(String message){
        OS.dbMes("|||FAKE_FILE_SYS: " + message);
    }
}
