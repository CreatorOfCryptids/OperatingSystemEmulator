public class MemUser extends UserLandProcess{
    public void main(){
        System.out.println("MEM USER: Allocating memory now.");

        int pointer1 = OS.allocateMemory(2048);
        String test = "Testy McTest Face";
        byte[] testBytes = test.getBytes();

        System.out.println("MEM USER: Wrting \""+test+"\" to mem now. Byte length = " + testBytes.length);

        for(int i=0; i<testBytes.length; i++)
            write(pointer1 + i, testBytes[i]);

        cooperate();
            
        byte[] readBytes = new byte[testBytes.length];

        for(int i=0; i<testBytes.length; i++)
            readBytes[i] = read(pointer1 + i);

        if (new String(readBytes).equals(test)){
            System.out.println("Read/Write successful!!!");
        }
        else{
            System.out.println("Read/Write FAILED!!!");
        }
        
        System.out.println("Read: \"" + new String(readBytes) + '\"');

        cooperate();

        while(true){
            int pointer2 = OS.allocateMemory(1024);

            test = "TEST";
            testBytes = test.getBytes();

            for(int i=0; i<testBytes.length; i++)
                write(pointer2 + i, testBytes[i]);
            
            System.out.println("MEM USER: Wrting \""+test+"\" to mem now. Byte length = " + testBytes.length);

            cooperate();

            readBytes = new byte[testBytes.length];

            for(int i=0; i<testBytes.length; i++)
                readBytes[i] = read(pointer2 + i);

            if (new String(readBytes).equals(test)){
                System.out.println("Read/Write successful!!!");
            }
            else{
                System.out.println("Read/Write FAILED!!!");
            }

            System.out.println("Read: \"" + new String(readBytes) + "\" Read length = " + readBytes.length);

            cooperate();

            OS.freeMemory(pointer2, 1024);

            OS.sleep(300);
        }
    }
}
