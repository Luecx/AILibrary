package newalgebra.network.weights.rng;

import core.tensor.Tensor;

import java.io.Serializable;

public abstract class RNG implements Serializable {

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
