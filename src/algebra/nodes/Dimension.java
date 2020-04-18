package algebra.nodes;

import core.tensor.Tensor;
import core.tensor.Tensor2D;
import core.tensor.Tensor3D;
import core.tensor.Tensor4D;

import java.util.Objects;

public class Dimension {

    private int depth = 1, width = 1, height = 1, trength = 1;

    public Dimension(Dimension other) {
        this.depth = other.depth;
        this.width = other.width;
        this.height = other.height;
        this.trength = other.trength;
    }

    public Dimension(int height) {
        this.height = height;
    }

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension(int depth, int width, int height) {
        this.depth = depth;
        this.width = width;
        this.height = height;
    }

    public Dimension(int trength, int depth, int width, int height){
        this(depth, width, height);
        this.trength = trength;
    }

    public int size(){
        return depth * width * height * trength;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTrength() {
        return trength;
    }

    public void setTrength(int trength) {
        this.trength = trength;
    }

    public Tensor emptyTensor(){
        if(trength != 1) return new Tensor4D(height, width, depth, trength);
        if(height != 1)return new Tensor3D(height, width, depth);
        if(width != 1) return new Tensor2D(height, width);
        return new Tensor(height);
    }


    public int dimCount(){
        if(trength != 1) return 4;
        if(height != 1) return 3;
        if(width != 1) return 2;
        if(depth != 1) return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimension dimension = (Dimension) o;
        return depth == dimension.depth &&
                width == dimension.width &&
                height == dimension.height;
    }

    @Override
    public int hashCode() {

        return Objects.hash(depth, width, height);
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "depth=" + depth +
                ", width=" + width +
                ", height=" + height +
                ", trength=" + trength +
                '}';
    }
}
