package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import neuralnetwork.builder.BuildException;

public class ExpPow extends Node<ExpPow> {

    public ExpPow(Node base, Node power){
        super(NodeCount.TWO, NodeCount.UNLIMITED);
        this.addPreviousNode(base);
        this.addPreviousNode(power);
    }

    @Override
    protected Dimension selfCalcOutputDim() throws BuildException {
        return null;
    }

    @Override
    protected void selfInit() {
    }

    @Override
    public void calc() {
        for (int n = 0; n < outputValue.size(); n++) {
            double fpg = Math.pow(getInputValue(0).getData()[n], getInputValue(1).getData()[n]);
            outputValue.getData()[n] = fpg;
            getOutputDerivative(0).getData()[n] = fpg * getInputValue(1).getData()[n] / getInputValue(0).getData()[n];
            getOutputDerivative(1).getData()[n] = fpg * Math.log(getInputValue(0).getData()[n]);
        }
    }

    @Override
    public void autoDiff() {
        for(int inp = 0; inp < getPreviousNodes().size(); inp++){
            for(int i = 0; i < this.outputGradient.size(); i++){
                getInputGradient(inp).getData()[i] += outputGradient.getData()[i] * outputDerivative[inp].getData()[i];
            }
        }
    }

    @Override
    public ExpPow copy() {
        ExpPow add = new ExpPow(getPreviousNode(), getPreviousNodes().get(1));

        return add;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getPreviousNode());
        builder.append("^");
        builder.append(getPreviousNodes().get(1));
        return builder.toString();
    }

}
