package neuralnetwork.nodes;

import java.util.ArrayList;

public abstract class Node1ToM extends Node {
    public Node1ToM() {
        super(true, false);
    }
    public Node1ToM(int depth, int width, int height) {
        super(depth, width, height, true, false);
    }
    public Node get_previous_node(){
        return previous_nodes.get(0);
    }
    public ArrayList<Node> getNextNodes(){
        return next_nodes;
    }
}
