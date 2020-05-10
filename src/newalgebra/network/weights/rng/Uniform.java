package newalgebra.network.rng;

import java.util.Random;

public class Uniform extends RNG{

    double min;
    double max;

    Random random;

    public Uniform(double min, double max, long seed) {
        super(seed);
        this.min = min;
        this.max = max;
        this.random = new Random(seed);
    }


    @Override
    public double nextDouble() {
        return this.random.nextDouble() * (max-min) + min;
    }
}
