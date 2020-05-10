package newalgebra.network.optimiser;

import newalgebra.cells.Output;

import java.io.Serializable;
import java.util.List;

public abstract class Optimiser implements Serializable {

    public abstract void prepare(List<Output> valuesToOptimise);

    public abstract void update();

    public static int countParams(List<Output> valuesToOptimise){
        int count = 0;
        for(Output o:valuesToOptimise){
            count += o.getValue().size();
        }
        return count;
    }
}
