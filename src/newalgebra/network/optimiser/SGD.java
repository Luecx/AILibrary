package newalgebra.network.optimiser;

import newalgebra.Output;

import java.util.List;

public class SGD extends Optimiser{

    private double learningRate;

    public SGD(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    private List<Output> valuesToOptimise;

    @Override
    public void prepare(List<Output> valuesToOptimise) {
        this.valuesToOptimise = valuesToOptimise;
    }

    @Override
    public void update() {
        for(Output o:valuesToOptimise){
            for(int i = 0; i < o.getValue().size(); i++){
                o.getValue().getData()[i] += -learningRate * o.getGradient().getData()[i];
                //System.out.println(o.getGradient().getData()[i]);
            }
        }
    }
}
