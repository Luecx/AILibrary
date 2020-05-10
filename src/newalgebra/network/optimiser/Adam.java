package newalgebra.network.optimiser;

import newalgebra.cells.Output;

import java.io.Serializable;
import java.util.List;

public class Adam extends Optimiser implements Serializable {

    private double alpha=0.001, beta_1=0.9, beta_2=0.999, epsilon = 1E-8;

    public Adam(double alpha, double beta_1, double beta_2, double epsilon) {
        this.alpha = alpha;
        this.beta_1 = beta_1;
        this.beta_2 = beta_2;
        this.epsilon = epsilon;
    }

    public Adam() {
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta_1() {
        return beta_1;
    }

    public void setBeta_1(double beta_1) {
        this.beta_1 = beta_1;
    }

    public double getBeta_2() {
        return beta_2;
    }

    public void setBeta_2(double beta_2) {
        this.beta_2 = beta_2;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }



    private List<Output> valuesToOptimise;
    private double[] firstMoment;
    private double[] secondMoment;
    private int t;


    @Override
    public void prepare(List<Output> valuesToOptimise) {
        this.valuesToOptimise = valuesToOptimise;
        int params = countParams(valuesToOptimise);
        this.firstMoment = new double[params];
        this.secondMoment = new double[params];
        this.t = 0;
    }

    @Override
    public void update() {
        if(valuesToOptimise == null) throw new RuntimeException();
        int index = 0;
        this.t++;
        for(Output o:valuesToOptimise){
            for(int i = 0; i < o.getValue().size(); i++){

                double g = o.getGradient().getData()[i];
                firstMoment[index] = beta_1 * firstMoment[index] + (1-beta_1) * g;
                secondMoment[index] = beta_2 * secondMoment[index] + (1-beta_2) * g*g;

                double fmCorrected = firstMoment[index] / (1 - Math.pow(beta_1, t));
                double smCorrected = secondMoment[index] / (1 - Math.pow(beta_2, t));

                double dP = -alpha * fmCorrected/(Math.sqrt(smCorrected) + epsilon);

                o.getValue().getData()[i] += dP;

                index++;
            }
        }
    }
}
