import java.util.Optional;

public interface Device {

    public static final int DEVICE_COUNT = 10;  // The max amount of devices this OS can hold.

    /**
     * Opens the desired Device.
     * 
     * @param device A string corresponding to the desired Device.
     */
    Optional<Integer> open(String s);

    /**
     * Reads from the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param size The amount of data to be read.
     */
    Optional<byte[]> read(int id, int size);

    /**
     * Moves the curser within the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param to The distance to move the curser.
     */
    void seek(int id, int to);

    /**
     * Writes to the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param data The data to be writen to the Device.
     */
    Optional<Integer> write(int id, byte[] data);

    /**
     * Closes the desired Device.
     * 
     * @param id The FID of the desired Device.
     */
    void close(int id);
}
