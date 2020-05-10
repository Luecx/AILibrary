package newalgebra.network.weights;

import newalgebra.cells.Dimension;
import newalgebra.cells.Variable;

import java.io.Serializable;

public class Weight extends Variable implements Serializable {

    public Weight(Dimension dimension) {
        super(dimension);
    }

    public Weight() {
    }
}
