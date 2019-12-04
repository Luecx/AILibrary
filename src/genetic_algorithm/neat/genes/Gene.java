package luecx.ai.genetic_algorithm.neat.genes;

public abstract class Gene{


    private int innovation_number;

    public Gene(int innovation_number) {
        this.innovation_number = innovation_number;
    }

    public int getInnovation_number() {
        return innovation_number;
    }

    public void setInnovation_number(int innovation_number) {
        this.innovation_number = innovation_number;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gene gene = (Gene) o;
        return innovation_number == gene.innovation_number;
    }

    @Override
    public final int hashCode() {
        return innovation_number;
    }
}
