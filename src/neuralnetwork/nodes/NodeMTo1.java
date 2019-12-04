package neuralnetwork.nodes;

import java.util.ArrayList;

public abstract class NodeMTo1 extends Node {
    public NodeMTo1() {
        super(false, true);
    }
    public NodeMTo1(int depth, int width, int height) {
        super(depth, width, height, false, true);
    }

    public Node get_next_node(){
        return next_nodes.get(0);
    }
    public ArrayList<Node> getPreviousNodes(){
        return previous_nodes;
    }
}
