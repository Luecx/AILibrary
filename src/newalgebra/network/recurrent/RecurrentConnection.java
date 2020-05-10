package newalgebra.network.recurrent;

import java.io.Serializable;

public class RecurrentConnection implements Serializable {
    private int outputIndex, inputIndex;

    public RecurrentConnection(int outputIndex, int inputIndex) {
        this.outputIndex = outputIndex;
        this.inputIndex = inputIndex;
    }

    public int getOutputIndex() {
        return outputIndex;
    }

    public void setOutputIndex(int outputIndex) {
        this.outputIndex = outputIndex;
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public void setInputIndex(int inputIndex) {
        this.inputIndex = inputIndex;
    }
}
