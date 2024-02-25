import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device{

    private final int MAX_FILES = 10;
    private RandomAccessFile files[];

    FakeFileSystem(){
        files = new RandomAccessFile[MAX_FILES];
    }

    public int open(String s) {
        int fid=-1;
        for(int i=0; i<MAX_FILES; i++){
            if(files[i] == null){
                fid = i;
                break;
            }
        }

        dbMes("OPEN: fid = " + fid + "String = \"" + s + "\"");

        if(fid == -1){
            return -1;
        }
        else if (s != null && s.equals("") == false){
            try{
                files[fid] = new RandomAccessFile(s, "rw");
                return fid;
            }
            catch (FileNotFoundException e){
                dbMes("Open(): ERROR: FileNotFound");
                return -1;
            }
        }
        else 
            return -1;
    }

    public void close(int id) {
        dbMes("Close()");
        if(files[id] == null){
            dbMes("Close(): ERROR: Null device");
        }
        else{
            try{
                files[id].close();
            }
            catch (IOException e){
                dbMes("Close(): ERROR: " + e.getMessage());
            }

            files[id] = null;
        }
    }

    public byte[] read(int id, int size) {
        byte[] retval = new byte[size];

        dbMes("Read()");

        try{
            files[id].read(retval);
            return retval;
        }
        catch (IOException e){
            dbMes("Read(): ERROR: " + e.getMessage());
            return new byte[]{-1};
        }
    }

    public void seek(int id, int to) {
        dbMes("Seek()");
        try{
            files[id].seek(files[id].getFilePointer() + to);
        }
        catch(IOException e){
            dbMes("Seek(): ERROR: " + e.getMessage());
        }
    }

    public int write(int id, byte[] data) {
        dbMes("Write()");

        try{
            files[id].write(data);
            return data.length;
        } 
        catch(IOException e){
            dbMes("Write(): ERROR: " + e.getMessage());
            return -1;
        }
    }

    /**
    * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
    * 
    * @param message The message printed to the terminal.
    */
    private void dbMes(String message){
        OS.dbMes("FAKE_FILE_SYS: " + message);
    }
}
