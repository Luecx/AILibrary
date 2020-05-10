package newalgebra.network.recurrent;

import core.tensor.Tensor;

import java.io.Serializable;

public class RecurrentMemory implements Serializable {


    private Tensor[] in;            //*ALL* inputs
    private Tensor[] out;           //*ALL* outputs

    public RecurrentMemory(Tensor[] in, Tensor[] out) {
        this.in = in;
        this.out = out;
    }

    public Tensor[] getIn() {
        return in;
    }

    public void setIn(Tensor[] in) {
        this.in = in;
    }

    public Tensor[] getOut() {
        return out;
    }

    public void setOut(Tensor[] out) {
        this.out = out;
    }
}
