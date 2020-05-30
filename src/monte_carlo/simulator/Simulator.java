package monte_carlo.simulator;


import monte_carlo.Node;
import monte_carlo.NodeData;

public interface Simulator<T extends NodeData> {

    double simulate(Node<T> leaf);

}
