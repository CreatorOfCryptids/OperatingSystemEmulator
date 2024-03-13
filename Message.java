public class Message {

    private int senderPID;  // The PID of the sending process.
    private int targetPID;  // The PID of the target process.
    private int signal;     // The type of message being sent.
    private byte[] data;    // The data stored in the message.

    /**
     * Constructor.
     * 
     * @param sender The PID of the sending process.
     * @param target The PID of the reciveing process.
     * @param signal The type of message being sent.
     * @param data The data stored in the data.
     */
    Message(int sender, int target, int signal, byte[] data){
        this.senderPID = sender;
        this.targetPID = target;

        this.signal = signal;

        this.data = data;
    }

    /**
     * Copy Constructor.
     * 
     * @param mes The message to be coppied.
     */
    Message(int senderPID, Message mes){
        this.senderPID = senderPID;
        this.targetPID = mes.getTarget();

        this.signal = mes.getSignal();

        this.data = mes.getData();
    }

    /**
     * The getSender() accessor.
     * 
     * @return The PID of the sending process.
     */
    public int getSender(){
        return this.senderPID;
    }

    /**
     * The getTarget() accessor.
     * 
     * @return The PID of the target process.
     */
    public int getTarget(){
        return this.targetPID;
    }

    /**
     * The getSignal() accessor.
     * 
     * @return The type of message being sent.
     */
    public int getSignal(){
        return this.signal;
    }

    /**
     * The getData() accessor.
     * 
     * @return The data stored in the messge. 
     */
    public byte[] getData(){
        return this.data;
    }

    public String toString(){
        return "Sender: " + senderPID + " Target: " + targetPID + " Signal: " +  signal + "Data: " + new String(data);
    }
}
