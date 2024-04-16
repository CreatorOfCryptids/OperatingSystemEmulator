import java.util.Optional;

public class VirtualToPhysicalMap {
    
    public Optional<Integer> physicalPageNum;
    public Optional<Integer> diskPageNum;

    public VirtualToPhysicalMap(){
        physicalPageNum = Optional.empty();
        diskPageNum = Optional.empty();
    }

    public boolean isFree(){
        return (physicalPageNum.isEmpty() && diskPageNum.isEmpty());
    }
}
