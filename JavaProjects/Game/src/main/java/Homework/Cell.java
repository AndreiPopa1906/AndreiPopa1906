package Homework;

public class Cell {
    public enum State {
        EMPTY, RED_STONE, BLUE_STONE
    }

    private State state;

    public Cell() {
        this.state = State.EMPTY;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
