package newalgebra.cells;

import core.tensor.Tensor;

import java.io.Serializable;

public class Input implements Serializable {

    private Output output;

    public Input(Output output) {
        setConnectedOutput(output);
    }

    public Input() {
    }

    public Output getOutput() {
        return output;
    }

    public Tensor getValue() {
        return this.output.getValue();
    }

    public Tensor getGradient(){
        return this.output.getGradient();
    }

    public Dimension getDimension() {
        return this.output.getDimension();
    }

    public boolean hasConnectedOutput() {
        return output != null;
    }

    public void removeConnectedOutput(){
        output.removeConnectedInput(this);
        output = null;
    }

    void setConnectedOutput(Output output) {

        if(hasConnectedOutput()){
            removeConnectedOutput();
        }

        this.output = output;
    }

}
