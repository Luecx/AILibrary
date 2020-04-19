package newalgebra.network.optimiser;

import newalgebra.Output;
import newalgebra.Variable;
import newalgebra.network.Weight;

import java.util.List;

public abstract class Optimiser {

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
