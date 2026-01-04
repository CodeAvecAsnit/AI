package dfapathfinder;

import Component.Environment;

public class Dfa implements Environment {
    private DfaNode q1;
    private DfaNode q2;
    private DfaNode q3;
    private DfaNode q4;
    private DfaNode q5;
    private DfaNode q6;
    private DfaNode q7;

    public static int MIN_ACCEPT_LENGTH;

    static{
        MIN_ACCEPT_LENGTH=5;
    }

    private DfaNode initialState;
    private DfaNode acceptingState;
    private DfaNode deadState;

    public Dfa() {
        this.q1 = new DfaNode(1);
        this.q2 = new DfaNode(2);
        this.q3 = new DfaNode(3);
        this.q4 = new DfaNode(4);
        this.q5 = new DfaNode(5);
        this.q6 = new DfaNode(6);
        this.q7 = new DfaNode(7);

        this.initialState = q1;
        this.acceptingState = q6;
        this.deadState = q7;

        q1.setANext(q7);
        q1.setBNext(q2);

        q2.setANext(q7);
        q2.setBNext(q3);

        q3.setANext(q4);
        q3.setBNext(q3);

        q4.setANext(q5);
        q4.setBNext(q4);

        q5.setANext(q6);
        q5.setBNext(q4);

        q6.setANext(q6);
        q6.setBNext(q4);

        q7.setANext(q7);
        q7.setBNext(q7);
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
    public boolean isValidString(String s) {
        DfaNode current = q1;
        for (char c : s.toCharArray()) {
            current = current.transform(c);
            if (isDead(current)) {
                return false;
            }
        }
        return isGoal(current);
    }
}
