package newalgebra.gla;

public class Edge {

    private double weight;

    private Node from, to;
    private int fromIndex, toIndex;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }


    public Edge(Node from, Node to, int fromIndex, int toIndex) {
        this.from = from;
        this.to = to;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
