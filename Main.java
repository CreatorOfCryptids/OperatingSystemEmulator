public class Main {
    public static void main(String[] args) throws Exception{
        OS.startUp(new HelloWorld());

        OS.createProcess(new GoodbyeWorld());

        OS.createProcess(new Insomniac(), OS.Priority.REALTIME);

        OS.createProcess(new NiceRealTime(), OS.Priority.REALTIME);

        OS.createProcess(new Background(), OS.Priority.BACKGROUND);
    }
}
