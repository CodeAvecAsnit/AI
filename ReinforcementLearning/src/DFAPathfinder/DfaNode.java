package DFAPathfinder;

public class DfaNode{

    private DfaNode aNext;
    private DfaNode bNext;
    private final String NodeName;

    public DfaNode(String nodeName) {
        NodeName = nodeName;
        this.aNext = null;
        this.bNext = null;
    }

    public void setBNext(DfaNode bNext) {
        this.bNext = bNext;
    }

    public void setANext(DfaNode aNext) {
        this.aNext = aNext;
    }

    public DfaNode transform(char input){
        return (input =='a')?aNext:bNext;

    }
}
