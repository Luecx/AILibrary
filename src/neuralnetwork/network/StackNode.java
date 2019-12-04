package neuralnetwork.network;

import neuralnetwork.builder.BuildException;
import neuralnetwork.nodes.Node;
import neuralnetwork.nodes.NodeMTo1;

public class StackNode extends NodeMTo1 {
    @Override
    protected void abs_calcOutputDim() throws BuildException {
        int w = getPreviousNodes().get(0).getOutputWidth();
        int h = getPreviousNodes().get(0).getOutputHeight();
        int d = 0;
        for (Node n : getPreviousNodes()) {
            if (w != n.getOutputWidth() || h != n.getOutputHeight()) {
                throw new BuildException(this, "non-constant with or height of input");
            }
            d += n.getOutputDepth();
        }
        setOutputDepth(d);
        setOutputWidth(w);
        setOutputHeight(h);
    }

    @Override
    public void abs_genArrays() {
    }

    @Override
    public void abs_feedForward() {
        int c = 0;
        for (int i = 0; i < getPreviousNodes().size(); i++) {
            for (int k = 0; k < getPreviousNodes().get(i).getOutputSize(); k++) {
                output_value.getData()[c] = getPreviousNodes().get(i).getOutputValue().getData()[k];
                output_derivative.getData()[c] = getPreviousNodes().get(i).getOutputDerivative().getData()[k];
                c++;
            }
        }
    }

    @Override
    public void abs_feedBackward() {
        int c = 0;
        for (int i = 0; i < getPreviousNodes().size(); i++) {
            for (int k = 0; k < getPreviousNodes().get(i).getOutputSize(); k++) {
                getPreviousNodes().get(i).getOutputLoss().getData()[k] = output_loss.getData()[c];
                c++;
            }
        }
    }

    @Override
    public void abs_updateWeights(double eta) {
    }
}
