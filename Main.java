public class Main {
    public static void main(String[] args) throws Exception{
        // Basic processes:
        OS.startUp(new HelloWorld());
        OS.createProcess(new GoodbyeWorld());

        // Priority management tests:
        OS.createProcess(new Insomniac(), OS.Priority.REALTIME);
        OS.createProcess(new NiceRealTime(), OS.Priority.REALTIME);
        OS.createProcess(new Background(), OS.Priority.BACKGROUND);

        // File I/O tests:
        OS.createProcess(new MereMortal(), OS.Priority.REALTIME);
        OS.createProcess(new MultiFIle(), OS.Priority.INTERACTIVE);
        OS.createProcess(new Reader(), OS.Priority.INTERACTIVE);

        // Messages tests:
        OS.createProcess(new Ping());
        OS.createProcess(new Pong());

        // Virtual Memory Tests:
        OS.createProcess(new MemUser());
        OS.createProcess(new MoralMemUser());
    }
}
