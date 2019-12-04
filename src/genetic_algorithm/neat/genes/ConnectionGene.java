package luecx.ai.genetic_algorithm.neat.genes;

public class ConnectionGene extends Gene {

    private NodeGene from;
    private NodeGene to;

    private double weight;
    private boolean enabled = true;


    public ConnectionGene(int innovation_number) {
        super(innovation_number);
    }

    public NodeGene getFrom() {
        return from;
    }

    public void setFrom(NodeGene from) {
        this.from = from;
    }

    public NodeGene getTo() {
        return to;
    }

    public void setTo(NodeGene to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ConnectionGene copy(){
        ConnectionGene g = new ConnectionGene(getInnovation_number());
        g.enabled = enabled;
        g.weight = weight;
        g.from = from;
        g.to = to;
        return g;
    }
}
