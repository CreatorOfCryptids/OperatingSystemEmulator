public class VFS implements Device{

    private final int DEVICE_COUNT = 10;    // The max amount of devices this OS can hold.

    private DeviceMap[] devices;            // A list of the devices currently in use.
    private RandomDevice randDev;           // A reference to the random device object.
    private FakeFileSystem FFSDev;          // A reference to the FakeFileSystem object.

    VFS(){
        devices = new DeviceMap[DEVICE_COUNT];
        randDev = new RandomDevice();
        FFSDev = new FakeFileSystem();

    }

    public int open(String s) {
        dbMes("Open() String: \"" + s + "\"");

        // Loop thru the list to find the first empty entry.
        for(int i = 0; i<DEVICE_COUNT; i++){
            if(devices[i] == null){
                devices[i] = new DeviceMap(s);

                return i;
            }
        }

        // If no entries, return error code.
        return -1;
    }

    public void close(int id) {
        dbMes("Close() FID: " + id);

        devices[id].close();
        devices[id] = null;     // Null this index so it can be reused.
    }

    public byte[] read(int id, int size) {
        dbMes("Read(): FID: " + id + " Size: " + size);

        return devices[id].read(size);
    }

    public void seek(int id, int to) {
        dbMes("Seek(): FID: " + id + "To: " + to);

        devices[id].seek(to);
    }

    public int write(int id, byte[] data) {
        dbMes("Write(): FID: " + id);

        return devices[id].write(data);
    }
    
    private void dbMes(String message){
        OS.dbMes("VFS: " + message);
    }

    private class DeviceMap{

        private final int CODELEN = 3;   // Length of the device code Strings.

        private Device dev;     // The type of device that this entry maps to.
        private int devIndex;   // The index for the file within the device.

        DeviceMap(String s){
            String deviceSpecifyer = s.substring(0, CODELEN);         // Device strings are only 3 char long.

            if(deviceSpecifyer.equals("RAN"))
                dev = randDev;
            else if (deviceSpecifyer.equals("FFS"))
                dev = FFSDev;
            
            devIndex = dev.open(deviceSpecifyer.substring(CODELEN +1));  // Remove space so everything after that will be passed to the devices's open().
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

        /**
         * Debugging.
         */
        public String toString(){
            return this.dev.getClass() + " devIndex: " + devIndex;
        }
    }
}
