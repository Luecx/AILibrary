package newalgebra.cells;

import core.tensor.Tensor;
import core.tensor.Tensor2D;
import core.tensor.Tensor3D;
import core.tensor.Tensor4D;

import java.io.Serializable;
import java.util.Objects;

public class Dimension implements Serializable {

    private int depth = 0, width = 0, height = 1, trength = 0;

    public Dimension(Dimension other) {
        this.depth = other.depth;
        this.width = other.width;
        this.height = other.height;
        this.trength = other.trength;
    }

    public Dimension(int height) {
        this.height = height;
    }

    public Dimension(int height, int width) {
        this.width = width;
        this.height = height;
    }

    public Dimension( int height, int width, int depth) {
        this.depth = depth;
        this.width = width;
        this.height = height;
    }

    public Dimension( int height, int width, int depth, int trength){
        this(height, width, depth);
        this.trength = trength;
    }

    public int size(){
        return Math.max(depth,1) * Math.max(width,1) * height * Math.max(1,trength);
    }

    public int getDepth() {
        return Math.max(1,depth);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getWidth() {
        return Math.max(1,width);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return Math.max(1,height);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTrength() {
        return Math.max(1,trength);
    }

    public void setTrength(int trength) {
        this.trength = trength;
    }

    public Tensor emptyTensor(){
        if(trength != 0) return new Tensor4D(height, width, depth, trength);
        if(depth != 0)return new Tensor3D(height, width, depth);
        if(width != 0) return new Tensor2D(height, width);
        return new Tensor(height);
    }


    public int dimCount(){
        if(trength != 0) return 4;
        if(depth != 0) return 3;
        if(width != 0) return 2;
        if(height != 0) return 1;
        return 0;
    }

    public static Dimension fromTensor(Tensor tensor){
        Dimension d =
                new Dimension(
                        tensor.rank() >= 1 ? tensor.getDimension(0) : 0,
                        tensor.rank() >= 2 ? tensor.getDimension(1) : 0,
                        tensor.rank() >= 3 ? tensor.getDimension(2) : 0,
                        tensor.rank() >= 4 ? tensor.getDimension(3) : 0
                );
        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimension dimension = (Dimension) o;
        return depth == dimension.depth &&
               width == dimension.width &&
               height == dimension.height &&
               trength == dimension.trength;
    }

    @Override
    public int hashCode() {

        return Objects.hash(height, width, depth, trength);
    }

    @Override
    public String toString() {
        return "Dimension{" +
                "height=" + height +
                ", width=" + width +
                ", depth=" + depth +
                ", trength=" + trength +
                '}';
    }

    public String toStringShort(){
        String s = "["+height;
        if(width != 0) s += ", " + width;
        if(depth != 0) s += ", " + depth;
        if(trength != 0) s += ", " + trength;

        return s+"]";
    }
}
