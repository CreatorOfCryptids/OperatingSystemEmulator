public class Ping extends UserLandProcess{

    public void main() {
        
        int pingPID = OS.getPID();

        System.out.println("I am PING and my PID is " + pingPID);

        int pongPID = OS.searchPID("Pong");

        System.out.println("Pong's PID is: " + pongPID);

        cooperate();

        while(true){
            Message pongMessage = OS.waitForMessage();
            System.out.println("PING: Pong sent \"" + new String(pongMessage.getData()) + "\" From PID: " + pongMessage.getSender() + " Signal: " + pongMessage.getSignal());

            cooperate();

            OS.sendMessage(new Message(pingPID, pongPID, pongMessage.getSignal()+1, new byte[]{80, 105, 110, 103}));

            OS.sleep(200);

            cooperate();
        }
    }
    
}
