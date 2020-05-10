package genetic_algorithm.qlearning.statetypes;

public class QIndex extends QState {

    private int index;

    public QIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
