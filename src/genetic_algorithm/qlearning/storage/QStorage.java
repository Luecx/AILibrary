package genetic_algorithm.qlearning.storage;

import genetic_algorithm.qlearning.statetypes.QState;

public abstract class QStorage<T extends QState> {

    public abstract void update(T state, int decision, T nextState, double reward);

    public abstract double max(T state);

    public abstract int action(T state);

    public abstract double get(T state, int action);

}
