package luecx.ai.genetic_algorithm.neat.neat;


import luecx.ai.genetic_algorithm.neat.calculation.Calculator;
import luecx.ai.genetic_algorithm.neat.genes.Genome;
import luecx.ai.genetic_algorithm.neat.species.Specie;

public class Client extends Genome {

    private double score;
    private Specie specie;

    private Calculator calculator;

    public Client(Neat neat) {
        super(neat);
    }

    public void generateCalculator(){
        this.calculator = new Calculator(this.getNode_genes(), this.getConnection_genes());
    }

    public double[] calculate(double... in){
        if(this.calculator != null){
            return this.calculator.calculate(in);
        }
        return null;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Specie getSpecie() {
        return specie;
    }

    public void setSpecie(Specie specie) {
        this.specie = specie;
    }
}
