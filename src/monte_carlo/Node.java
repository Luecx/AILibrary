package monte_carlo;


import java.util.List;

public class Node<T extends NodeData> {

    private List<Node<T>> childs;
    private Node parent;

    private T nodeData;
    private int playerIndex;
    private int depth;
    private double total_score;
    private int number_of_simulations;

    public Node(T nodeData, int depth) {
        this.nodeData = nodeData;
        this.depth = depth;
    }

    public double getTotal_score() {
        return total_score;
    }

    public void setTotal_score(double total_score) {
        this.total_score = total_score;
    }

    public int getNumber_of_simulations() {
        return number_of_simulations;
    }

    public void setNumber_of_simulations(int number_of_simulations) {
        this.number_of_simulations = number_of_simulations;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isLeaf(){
        return childs == null || childs.size() == 0;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node<T>> getChilds() {
        return childs;
    }

    public void setChilds(List<Node<T>> childs) {
        this.childs = childs;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public T getNodeData() {
        return nodeData;
    }

    @Override
    public String toString() {
        return "["+total_score+":"+number_of_simulations+"]("+nodeData+")";
    }

    public void setNodeData(T nodeData) {
        this.nodeData = nodeData;
    }
}
