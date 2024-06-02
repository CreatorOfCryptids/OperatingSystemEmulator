public class Pong extends UserLandProcess{

    public void main() {
        int pingPID = OS.searchPID("Ping").get();
        int pongPID = OS.getPID();

        System.out.println("PONG: My name is PONG and my pid is: " + pongPID);
        System.out.println("PONG: Ping's PID is: " + pingPID);

        System.out.println("PONG: Sending message.");
        OS.sendMessage(new Message(pongPID, pingPID, 0, new byte[]{80, 111, 110, 103}));

        cooperate();

        while(true){

            System.out.println("PONG: Waiting for message.");

            Message pingMessage = OS.waitForMessage();

            System.out.println("PONG: Ping sent \"" + new String(pingMessage.getData()) + "\" From PID: " + pingMessage.getSender() + " Signal: " + pingMessage.getSignal());

            cooperate();

            System.out.println("PONG: Sending message.");

            OS.sendMessage(new Message(pongPID, pingPID, pingMessage.getSignal()+1, new byte[]{80, 111, 110, 103}));

            OS.sleep(200);

            cooperate();
        }
    }
    
}
