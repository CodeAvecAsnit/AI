package dfapathfinder;

public class DfaNode{

    private DfaNode aNext;
    private DfaNode bNext;
    private final int index;

    public DfaNode(int index) {
        this.index = index;
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

    public int getIndex(){
        return this.index;
    }
}
