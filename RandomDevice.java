import java.util.Optional;
import java.util.Random;

public class RandomDevice implements Device{

    private Random[] rands;

    RandomDevice(){
        rands = new Random[Device.DEVICE_COUNT];
    }

    /**
     * Creates a new random device.
     * 
     * @param s The seed for the random device.
     * @return The FID of the random device.
     */
    public Optional<Integer> open(String s) {

        int fid = -1;

        // Look for an open space.
        for(int i = 0; i<Device.DEVICE_COUNT; i++){
            if(rands[i] == null){   // If one is found, store to fid.
                fid = i;
                break;
            }
        }

        dbMes("OPEN: fid = " + fid + " String = \"" + s + "\"");

        if(fid == -1){
            // If an open space wasn't found return error.
            dbMes("ERROR: No available entries.");
            return Optional.empty();
        }
        else if(s != null && s.equals("") == false){
            // If they gave us a seed, use that to seed the rand.
            rands[fid] = new Random(s.hashCode());  // Hash so they can send anything as the seed and it wont break.
        }
        else{
            // Otherwize, seed rand with rand.
            rands[fid] = new Random();
        }
        
        dbMes("FID: " + fid);
        return Optional.of(fid);
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
    public Optional<byte[]> read(int id, int size) {
        dbMes("Read().");
        byte[] retval = new byte[size];

        rands[id].nextBytes(retval);

        return Optional.of(retval);
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
    public Optional<Integer> write(int id, byte[] data) {
        dbMes("Write()???");
        return Optional.of(0);
    }

    /**
    * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
    * 
    * @param message The message printed to the terminal.
    */
    private void dbMes(String message){
        OS.dbMes("|||RAND_DEV: " + message);
    }
}
