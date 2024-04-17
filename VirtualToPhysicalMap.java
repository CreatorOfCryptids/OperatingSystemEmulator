import java.util.Optional;

public class VirtualToPhysicalMap {
    
    public Optional<Integer> physicalPageNum;
    public Optional<Integer> diskPageNum;

    public VirtualToPhysicalMap(){
        physicalPageNum = Optional.empty();
        diskPageNum = Optional.empty();
    }

    /**
     * Determines if either of the entries are not empty
     * 
     * @return True if both are empty, false otherwize.
     */
    public boolean isFree(){
        return (physicalPageNum.isEmpty() && diskPageNum.isEmpty());
    }
}
