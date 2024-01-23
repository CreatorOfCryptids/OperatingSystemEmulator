public class Main {
    public static void main(String[] args) throws Exception{
        OS.startUp(new HelloWorld());
        OS.createProcess(new GoodbyeWorld());
    }
}
