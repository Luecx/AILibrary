package luecx.ai.genetic_algorithm.neat.genes;

public class NodeGene extends Gene {

    private double pos_x;
    private double pos_y;

    private int type;   //0 = hidden, -1 = input, 1 = output

    public NodeGene(int innovation_number) {
        super(innovation_number);
        this.pos_x = Math.random();
        this.pos_y = Math.random();
    }

    public NodeGene(int innovation_number, double pos_x, double pos_y) {
        super(innovation_number);
        this.pos_x = pos_x;
        this.pos_y = pos_y;
    }

    public double getPos_x() {
        return pos_x;
    }

    public void setPos_x(double pos_x) {
        this.pos_x = pos_x;
    }

    public double getPos_y() {
        return pos_y;
    }

    public void setPos_y(double pos_y) {
        this.pos_y = pos_y;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "NodeGene{" +
                "pos_x=" + pos_x +
                ", pos_y=" + pos_y +
                ", type=" + type +
                '}';
    }
}
