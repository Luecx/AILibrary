package neuralnetwork.network.special;


import core.tensor.Tensor;
import neuralnetwork.network.OutputNode;

/**
 * this uses cross entropy with softmax
 */
public class SoftMaxNode extends OutputNode {

    public SoftMaxNode() {
        super();
    }

    private double error_factor = 1;
    public double getErrorFactor() {
        return error_factor;
    }
    public void setErrorFactor(double error_factor) {
        this.error_factor = error_factor;
    }

    public double calculateLoss(Tensor exp){
        double loss = 0;
        for(int i = 0; i < exp.size(); i++){
            loss -= exp.getData()[i] * Math.log(output_value.getData()[i]);
            if(Double.isNaN(loss) || Double.isNaN(output_loss.getData()[i])){
                System.out.println(input_value);
                System.out.println(get_previous_node().getInputValue());
                System.out.println(get_previous_node().getOutputValue());
                throw new RuntimeException();
            }
            input_loss.getData()[i] = output_value.getData()[i] + (exp.getData()[i] > 0.5 ? -1:0);
        }
        return loss;
    }

    @Override
    protected void abs_calcOutputDim() {
        this.setOutputDepth(get_previous_node().getOutputDepth());
        this.setOutputWidth(get_previous_node().getOutputWidth());
        this.setOutputHeight(get_previous_node().getOutputHeight());
    }

    @Override
    public void abs_genArrays() {

    }

    @Override
    public void abs_feedForward() {
        double tot = 0;


        for(int i = 0; i < getOutputSize(); i++){
            output_value.getData()[i] = Math.exp(input_value.getData()[i]);
            if(!Double.isNaN(output_value.getData()[i]) || output_value.getData()[i] < 1E-5){
                output_value.getData()[i] = 1E-5;
            }

            tot += output_value.getData()[i];
        }
        for(int i = 0; i < getOutputSize(); i++){
            output_value.getData()[i] /= tot;
        }
    }

    @Override
    public void abs_feedBackward() {
//        for(int i = 0; i < getOutputSize(); i++){
//            input_loss.getData()[i] = output_loss.getData()[i];
//        }
    }

    @Override
    public void abs_updateWeights(double eta) {

    }

}
