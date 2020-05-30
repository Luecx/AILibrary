package monte_carlo.selection;


import monte_carlo.Node;
import monte_carlo.NodeData;

public interface Selector<T extends NodeData> {

    Node selectLeaf(Node root);

    Node selectChild(Node root);

}
