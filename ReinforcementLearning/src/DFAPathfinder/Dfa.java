package DFAPathfinder;

public class Dfa {
    private DfaNode q1;
    private DfaNode q2;
    private DfaNode q3;
    private DfaNode q4;
    private DfaNode q5;
    private DfaNode q6;
    private DfaNode q7;
    private DfaNode q8;

    private DfaNode initialState;
    private DfaNode acceptingState;
    private DfaNode deadState;

    public Dfa() {
        this.q1 = new DfaNode("q1");
        this.q2 = new DfaNode("q2");
        this.q3 = new DfaNode("q3");
        this.q4 = new DfaNode("q4");
        this.q5 = new DfaNode("q5");
        this.q6 = new DfaNode("q6");
        this.q7 = new DfaNode("q7");
        this.q8 = new DfaNode("q8");

        this.initialState = q1;
        this.acceptingState = q7;
        this.deadState = q8;

        q1.setANext(q8);
        q1.setBNext(q2);

        q2.setANext(q8);
        q2.setBNext(q3);

        q3.setANext(q4);
        q3.setBNext(q3);

        q4.setANext(q5);
        q4.setBNext(q4);

        q5.setANext(q6);
        q5.setBNext(q4);

        q6.setANext(q7);
        q6.setBNext(q4);

        q7.setANext(q7);
        q7.setBNext(q4);

        q8.setANext(q8);
        q8.setBNext(q8);
    }

    public boolean isGoal(DfaNode node) {
        return node.equals(acceptingState);
    }


    public boolean isDead(DfaNode node) {
        return node.equals(deadState);
    }

    public void reset() {
        this.initialState = q1;
    }

    public DfaNode setPointer() {
        DfaNode node = initialState;
        return node;
    }
}
