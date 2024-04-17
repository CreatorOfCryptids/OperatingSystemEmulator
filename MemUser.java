public class MemUser extends UserLandProcess{

    public void main(){
        System.out.println("MEM USER: Allocating memory now.");

        int pointer1 = OS.allocateMemory(UserLandProcess.PAGE_SIZE*2);
        String test = "Testy McTest Face";
        byte[] testBytes = test.getBytes();

        System.out.println("MEM USER: Wrting \""+test+"\" to Pointer: " + pointer1 + ". Byte length = " + testBytes.length);

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
        
        System.out.println("Read: \"" + new String(readBytes) + "\" (" + testBytes.length + " bytes) from pointer " + pointer1);

        // Testing.
        for(byte b : readBytes){
            dbMes("" + b);
        }

        cooperate();

        while(true){

            System.out.println("Allocating " + UserLandProcess.PAGE_SIZE + " bytes.");

            int pointer2 = OS.allocateMemory(UserLandProcess.PAGE_SIZE);

            test = "TEST";
            testBytes = test.getBytes();

            for(int i=0; i<testBytes.length; i++)
                write(pointer2 + i, testBytes[i]);
            
            System.out.println("MEM USER: Wrting \""+test+"\" to mem now. Byte length = " + testBytes.length);

            cooperate();

            OS.sleep(100);

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
