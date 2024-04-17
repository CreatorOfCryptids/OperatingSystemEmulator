import java.util.ArrayList;

public class OS {
    
    private static Kernel kernel;               // A reference to the Kernel.
    public static CallType currentCall;         // The current call to the Kernel.
    public static ArrayList<Object> parameters; // The Parameters for the Kernel Call.
    public static Object retval;                // The return value from the Kernel.

    public enum CallType{
        ALLOCATE, CLOSE, CREATE, FREE, SEARCHPID, GETPID, GET_MAPPING, OPEN, READ, SEEK, SEND_MESSAGE, SLEEP, SWITCH, WAIT_MESSAGE, WRITE
    }

    public enum Priority{
        REALTIME, INTERACTIVE, BACKGROUND
    }

    // Init Methods:

    /**
     * Starts the initial process of the OS.
     * 
     * @param init The first process to run.
     */
    public static void startUp(UserLandProcess init){
        
        dbMes("OS: StartUp");

        kernel = new Kernel();
        parameters = new ArrayList<Object>();
        retval = new Object();

        createProcess(init);
        createProcess(new IdleProcess(), Priority.BACKGROUND);
    }

    // Kernel Calls:

    /**
     * Adds a process to the scheduler.
     * 
     * @param up The process to be added.
     * @return The PID of the new process.
     */
    public static int createProcess(UserLandProcess up) {

        dbMes("OS: Creating new Process: " + up.getClass());

        // Reset the parameters
        parameters.clear();
        parameters.add(up);
        parameters.add(Priority.INTERACTIVE);

        // Set currentCall
        currentCall = CallType.CREATE;

        // Switch to Kernal
        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } catch(Exception ex){}
            }
        }
    }

    /**
     * Adds a process to the scheduler.
     * 
     * @param up The process to be added.
     * @param priority The priority of the new process.
     * @return The PID of the new Process.
     */
    public static int createProcess(UserLandProcess up, Priority priority){

        dbMes("OS: Creating new Process: " + up.getClass() + " Priority: " + priority.toString());

        parameters.clear();
        parameters.add(up);
        parameters.add(priority);

        currentCall = CallType.CREATE;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } catch(Exception ex){}
            }
        }
    }

    /**
     * Switches to the next process in the queue.
     */
    public static void switchProcess(){
        dbMes("OS: Switching process");

        retval = null;
        parameters.clear();

        currentCall = CallType.SWITCH;

        switchToKernel();
    }

    /**
     * Pauses the currentlyRunning process and doesn't put it back in the queue unitl after the specified time has elapsed.
     * 
     * @param milliseconds The amount of time that the process wants to sleep for.
     */
    public static void sleep(int milliseconds){

        dbMes("OS: Sleep");

        retval = null;
        parameters.clear();

        parameters.add(milliseconds);
        
        currentCall = CallType.SLEEP;

        switchToKernel();
    }

    /**
     * Opens the desired device and returns the File IDentification number (FID) of the device.
     * 
     * @param device
     * @return The FID of the opened device, or -1 if it fails.
     */
    public static int open(String device){

        OS.dbMes("OS: Opening " + device);

        parameters.clear();
        parameters.add(device);

        currentCall = CallType.OPEN;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } catch(Exception ex){}
            }
        }
    }

    /**
     * Reads data from the specifed device.
     * 
     * @param FID The File ID of the device to be read from.
     * @param size The amount of data in bytes to be read.
     * @return
     */
    public static byte[] read(int FID, int size){
        OS.dbMes("OS: Reading " + FID + " Size: " + size);

        parameters.clear();
        parameters.add(FID);
        parameters.add(size);

        currentCall = CallType.READ;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (byte[]) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } catch(Exception ex){}
            }
        }
    }

    /**
     * Moves the curser within the specified device.
     * 
     * @param FID The File ID of the device to be seeked.
     * @param size The distance to be seeked
     * @return The distance that was seeked. 
     */
    public static int seek(int FID, int size){
        OS.dbMes("OS: Seeking FID: " + FID + " Size: " + size);

        parameters.clear();
        parameters.add(FID);
        parameters.add(size);

        currentCall = CallType.SEEK;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } catch(Exception ex){}
            }
        }
    }

    /**
     * Writes information to the specifed device.
     * 
     * @param FID The File ID of the device being writen to.
     * @param data The data passed to the device.
     * @return The amount of data written to the device.
     */
    public static int write(int FID, byte[] data){
        OS.dbMes("OS: Writing to " + FID + " Data: " + data);

        parameters.clear();
        parameters.add(FID);
        parameters.add(data);

        currentCall = CallType.WRITE;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } catch(Exception ex){}
            }
        }
    }

    /**
     * Closes the device specified by the FID.
     * 
     * @param FID The File ID of the device to be closed.
     */
    public static void close(int FID){
        OS.dbMes("OS: Closing " + FID);

        parameters.clear();
        parameters.add(FID);

        currentCall = CallType.CLOSE;

        switchToKernel();
    }

    /**
     * Gets the current process's PID.
     * 
     * @return The PID of the process.
     */
    public static int getPID(){
        OS.dbMes("OS: Get PID");

        parameters.clear();
        
        currentCall = CallType.GETPID;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } 
                catch(InterruptedException ex){}
            }
        }
    }

    /**
     * Gets the PID of the named process.
     * 
     * @param name The name of the desired process.
     * @return The PID of the named process, or -1 on failure.
     */
    public static int searchPID(String name){
        OS.dbMes("OS: Get PID of \"" + name + "\"");

        parameters.clear();
        parameters.add(name);

        currentCall = CallType.SEARCHPID;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } 
                catch(Exception ex){

                }
            }
        }
    }

    /**
     * Sends a message to the desired process.
     * 
     * @param message The message to be sent.
     */
    public static void sendMessage(Message message){
        dbMes("OS: Send message: " + new String(message.getData()));

        parameters.clear();
        parameters.add(message);

        currentCall = CallType.SEND_MESSAGE;

        switchToKernel();
    }

    /**
     * Pauses the process until it recives a message.
     * 
     * @return The most recent messge sent to the process.
     */
    public static Message waitForMessage(){
        dbMes("OS: Wait Message");

        parameters.clear();

        currentCall = CallType.WAIT_MESSAGE;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                if(retval != null)
                    return (Message) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } 
                catch(Exception ex){

                }
            }
        }
    }

    /**
     * Gets the mapping for the provided virutal page number, and stores it into the tlp.
     * 
     * @param virtualPageNum The virtual page number of the desired memory or -1 on failure.
     */
    public static void getMapping(int virtualPageNum){
        dbMes("OS: Get Mapping");

        parameters.clear();
        parameters.add(virtualPageNum);
        currentCall = CallType.GET_MAPPING;

        switchToKernel();
    }

    /**
     * Allocates memory.
     * 
     * @param size The amount of bytes in memory to be allocated.
     * @return The first pointer in the allocated memory.
     */
    public static int allocateMemory(int size){
        dbMes("OS: Allocate Memory");

        if(size % UserLandProcess.PAGE_SIZE !=0){
            dbMes("OS: Size " + size + " is not a multiple of " + UserLandProcess.PAGE_SIZE + ".");
            return -1;
        }

        // Change to number of pages added because we don't support removing anything smaller anyway.
        size = size / UserLandProcess.PAGE_SIZE;

        parameters.clear();
        
        parameters.add(size);

        currentCall = CallType.ALLOCATE;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                if(retval != null)
                    return (int) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } 
                catch(Exception ex){

                }
            }
        }
    }

    /**
     * Frees the specified page in memory.
     * 
     * @param pointer The start of the addresses in memory to be freed.
     * @param size The amound of memory in bytes to be freed in memory.
     * 
     * @return True if the freeing was successfull. False otherwize.
     */
    public static boolean freeMemory(int pointer, int size){
        dbMes("OS: Allocate Memory");

        if(pointer % UserLandProcess.PAGE_SIZE !=0){
            dbMes("OS: Size " + pointer + " is not a multiple of " + UserLandProcess.PAGE_SIZE + ".");
            return false;
        }

        if(size % UserLandProcess.PAGE_SIZE !=0){
            dbMes("OS: Size " + size + " is not a multiple of " + UserLandProcess.PAGE_SIZE + ".");
            return false;
        }

        // Change to page number because we don't support removeing anything smaller than a page.
        pointer = pointer / UserLandProcess.PAGE_SIZE;

        // Change to number of pages added because we don't support removing anything smaller anyway.
        size = size / UserLandProcess.PAGE_SIZE;

        parameters.clear();

        parameters.add(pointer);
        parameters.add(size);

        currentCall = CallType.FREE;

        switchToKernel();

        while(true){    // The processes are async, so this will sometimes run before Kernel can update it.
            try{
                if(retval != null)
                    return (boolean) retval;
            } catch (Exception e){
                try{
                    Thread.sleep(5);
                } 
                catch(Exception ex){

                }
            }
        }
    
    }

    // Helper Methods:

    /**
     * Starts the Kernel and stops the current process.
     */
    private static void switchToKernel(){
        dbMes("OS: Switching to kernel");

        // Store the process here in case the kernel thread changes currentlyRunning before we can call stop.
        PCB stopMe = kernel.getCurrentlyRunning();  

        kernel.start();

        if(stopMe != null)
            stopMe.stop();

        //kernel.stopCurrentProcesss();
    }
    
    // Debugging Help:

    /**
     * EXTRA METHOD!!! Prints a message to the terminal to help bebugging.
     * 
     * @param message The message printed to the terminal.
     */
    public static void dbMes(String message){
        System.out.println("    ||"+message);
    }
}
