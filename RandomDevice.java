import java.util.Random;

public class RandomDevice implements Device{

    private static final int RAND_COUNT = 10;
    private Random[] rands = new Random[RAND_COUNT];

    /**
     * Creates a new random device.
     * 
     * @param s The seed for the randome device.
     * @return The FID of the random device.
     */
    public int open(String s) {

        int fid = -1;
        for(int i = 0; i<RAND_COUNT; i++){
            if(rands[i] == null){
                fid = i;
                break;
            }
        }

        dbMes("OPEN: fid = " + fid + "String = \"" + s + "\"");

        if(fid == -1)
            return -1;
        else if(s != null && s.equals("") == false)
            rands[fid] = new Random(s.hashCode());
        else
            rands[fid] = new Random();
        
        return fid++;
    }

    /**
     * Closes the specified random device.
     * 
     * @param id The FID of the specifed random device.
     */
    public void close(int id) {
        dbMes("Close().");
        rands[id] = null;
    }

    /**
     * Reads the next random bytes of the random device.
     * 
     * @param id The FID of the random device.
     * @param size The amount of bytes to be read from the device.
     */
    public byte[] read(int id, int size) {
        dbMes("Read().");
        byte[] retval = new byte[size];

        rands[id].nextBytes(retval);

        return retval;
    }

    /**
     * Moves forward by the specifed amount of bytes.
     * 
     * @param id The FID of the specifed random device.
     * @param to The amount of bytes to be eaten.
     */
    public void seek(int id, int to) {
        dbMes("Seek()");
        for(int i = 0; i<to; i++)
            rands[id].nextInt();
    }

    /**
     * Random devices cannot be written to, so this will only return 0.
     * 
     * @param id A specifyer for a Random Device. This will not be used.
     * @param data The data that would be writen to the device if it could be writen.
     * @return 0. You can't write to this thing, dumbass.
     */
    public int write(int id, byte[] data) {
        dbMes("Write()???");
        return 0;
    }

    /**
    * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
    * 
    * @param message The message printed to the terminal.
    */
    private void dbMes(String message){
        OS.dbMes("RAND_DEV: " + message);
    }
}
