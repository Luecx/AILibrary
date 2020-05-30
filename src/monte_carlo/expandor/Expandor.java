package monte_carlo.expandor;


import monte_carlo.Node;
import monte_carlo.NodeData;

import java.util.List;

public interface Expandor<T extends NodeData> {


    List<Node<T>> expand(Node<T> root);

}
