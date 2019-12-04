package luecx.ai.genetic_algorithm.neat.calculation;

public class Connection {

    private Node input_node;
    private Node output_node;

    private double weight;
    private boolean activated = true;

    public Connection(Node input_node, Node output_node) {
        this.input_node = input_node;
        this.output_node = output_node;
    }

    public Node getInput_node() {
        return input_node;
    }

    public void setInput_node(Node input_node) {
        this.input_node = input_node;
    }

    public Node getOutput_node() {
        return output_node;
    }

    public void setOutput_node(Node output_node) {
        this.output_node = output_node;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "input_node=" + input_node +
                ", output_node=" + output_node +
                ", weight=" + weight +
                ", activated=" + activated +
                '}';
    }
}
