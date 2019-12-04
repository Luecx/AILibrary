package neuralnetwork.nodes;

public abstract class Node1To1 extends Node {
    public Node1To1() {
        super(true, true);
    }

    public Node1To1(int depth, int width, int height) {
        super(depth, width, height, true, true);
    }

    public Node get_previous_node(){
        return previous_nodes.get(0);
    }
    public Node get_next_node(){
        return next_nodes.get(0);
    }
}
