import java.util.Random;

public class RandomDevice implements Device{

    private static final int RAND_COUNT = 10;
    private Random[] rands = new Random[10];
    private int nextIndex = 0;

    @Override
    public int open(String s) {

        if(s != null && s.equals("") == false)
            rands[nextIndex++] = new Random(s.hashCode());    //TODO: Is this how?

    }

    @Override
    public void close(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

    @Override
    public byte[] read(int id, int size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public void seek(int id, int to) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'seek'");
    }

    @Override
    public int write(int id, byte[] data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}
