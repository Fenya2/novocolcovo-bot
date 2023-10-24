package models;

public class Context {
    private final String state;
    private final int stateNum;

    public Context(String state, int stateNum) {
        this.state = state;
        this.stateNum = stateNum;
    }

    public int getStateNum() {
        return stateNum;
    }

    public String getState() {
        return state;
    }
}
