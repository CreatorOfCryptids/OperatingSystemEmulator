public class Reader extends UserLandProcess{
    public void main(){
        
        int ffsFID = OS.open("FILE test.txt");

        while(true){

            byte output = OS.read(ffsFID, 1)[0];

            System.out.printf("Reading from file: %d\n", output);

            OS.sleep(30);
        }
    }
}
