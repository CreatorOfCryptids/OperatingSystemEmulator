public class Pong extends UserLandProcess{

    public void main() {
        int pingPID = OS.searchPID("Ping");
        int pongPID = OS.getPID();

        System.out.println("My name is PONG and my pid is: " + pongPID);
        System.out.println("Ping's PID is: " + pingPID);

        OS.sendMessage(new Message(pongPID, pingPID, 0, new byte[]{80, 111, 110, 103}));

        cooperate();

        while(true){
            Message pingMessage = OS.waitForMessage();
            System.out.println("PONG: Ping sent \"" + new String(pingMessage.getData()) + "\" From PID: " + pingMessage.getSender() + " Signal: " + pingMessage.getSignal());

            cooperate();

            OS.sendMessage(new Message(pongPID, pingPID, pingMessage.getSignal()+1, new byte[]{80, 111, 110, 103}));

            OS.sleep(200);

            cooperate();
        }
    }
    
}
