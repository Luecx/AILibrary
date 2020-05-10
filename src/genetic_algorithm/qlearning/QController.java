package genetic_algorithm.qlearning;

import genetic_algorithm.qlearning.statetypes.QState;

public abstract class QController<T extends QState> {

    public abstract T getState();

    public abstract int actionCount(QState state);

    public abstract double perform(int action);

}
