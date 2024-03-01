public class VFS implements Device{

    public final int CODELEN = 4;   // Length of the device code Strings.
    private DeviceMap[] devices;    // A list of the devices currently in use.
    private RandomDevice randDev;   // A reference to the random device object.
    private FakeFileSystem FFSDev;  // A reference to the FakeFileSystem object.

    VFS(){
        devices = new DeviceMap[Device.DEVICE_COUNT];
        randDev = new RandomDevice();
        FFSDev = new FakeFileSystem();
    }

    /**
     * Opens the desired Device.
     * 
     * @param s A string corresponding to the desired Device.
     */
    public int open(String s) {
        dbMes("Open() String: \"" + s + "\"");

        // Loop thru the list to find the first empty entry.
        for(int i = 0; i<Device.DEVICE_COUNT; i++){
            if(devices[i] == null){

                DeviceMap temp = new DeviceMap(s);

                // Checki if the device is valid.
                if(temp.getDevIndex() != -1){   // If yes, add to the list and return it's index.
                    devices[i] = temp;
                    dbMes("");
                    return i;
                }
                else{                           // Otherwize, return failure.
                    dbMes("ERROR: Could not open Device.");
                    return -1;
                }
            }
        }

        // If no entries, return error code.
        dbMes("ERROR: No available entries.");
        return -1;
    }

    /**
     * Closes the desired Device.
     * 
     * @param id The FID of the desired Device.
     */
    public void close(int id) {
        dbMes("Close() FID: " + id);

        if (id >= 0 && id < Device.DEVICE_COUNT){
            devices[id].close();
            devices[id] = null;     // Null this index so it can be reused.
        }
    }

    /**
     * Reads from the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param size The amount of data to be read.
     */
    public byte[] read(int id, int size) {
        dbMes("Read(): FID: " + id + " Size: " + size);

        if (id >= 0 && id < Device.DEVICE_COUNT){
            return devices[id].read(size);
        }
        else{
            return new byte[]{-1};
        }
    }

    /**
     * Moves the curser within the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param to The distance to move the curser.
     */
    public void seek(int id, int to) {
        dbMes("Seek(): FID: " + id + "To: " + to);

        if (id >= 0 && id < Device.DEVICE_COUNT){
            devices[id].seek(to);
        }
    }

    /**
     * Writes data to the desired Device.
     * 
     * @param id The FID of the desired Device.
     * @param data The data to be writen to the device
     * @return The amount of data successfully writen to the Device.
     */
    public int write(int id, byte[] data) {
        dbMes("Write(): FID: " + id);

        if (id >= 0 && id < Device.DEVICE_COUNT){
            return devices[id].write(data);
        }
        else{
            return -1;
        }
    }
    
    /**
    * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
    * 
    * @param message The message printed to the terminal.
    */
    private void dbMes(String message){
        OS.dbMes("||VFS: " + message);
    }

    /**
     * The DeviceMap class.
     * 
     * Stores A device pointer and an index for that device pointer.
     */
    private class DeviceMap{

        private Device dev;         // The type of device that this entry maps to.
        private int devIndex = 0;   // The index for the file within the device.

        DeviceMap(String s){
            String deviceSpecifyer = s.substring(0, CODELEN);         // Device strings are only 3 char long in my implementation.

            if(deviceSpecifyer.equals("RAND")){
                dev = randDev;
                dbMes("Case: Random");
            }
            else if (deviceSpecifyer.equals("FILE")){
                dev = FFSDev;
                dbMes("Case: File");
            }
            else{
                dbMes("VFS: Invalid File Specifyer");
                devIndex = -1;
            }

            devIndex = (devIndex == -1) ? 
                -1 :                            // Keep the devIndex at -1 so the VFS knows it's invalid.
                dev.open(s.substring(CODELEN)); // Pass the rest of the string to the devices's open().
        }

        public byte[] read(int size) {
            return dev.read(devIndex, size);
        }
    
        public void seek(int to) {
            dev.seek(devIndex, to);
        }
    
        public int write(byte[] data) {
            return dev.write(devIndex, data);
        }

        public void close(){
            dev.close(devIndex);
        }

        public int getDevIndex(){
            return devIndex;
        }

        private void dbMes(String message){
            OS.dbMes("|||DeviceMap: " + message);
        }

        /**
         * Debugging.
         * 
         * @return A string containing the information contained in this DeviceMap.
         */
        public String toString(){
            return this.dev.getClass() + " devIndex: " + devIndex;
        }
    }
}
