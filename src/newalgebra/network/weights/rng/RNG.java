package newalgebra.network.rng;

import core.tensor.Tensor;
import core.tensor.Tensor2D;

public abstract class RNG {

    private long seed;

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public RNG(long seed){
        this.setSeed(seed);
    }

    public void apply(Tensor tensor){
        for(int i = 0; i < tensor.size(); i++){
            tensor.getData()[i] = nextDouble();
        }
    }

    public abstract double nextDouble();


}
