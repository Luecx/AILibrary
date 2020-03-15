package algebra.nodes;

import java.util.Objects;

public class Dimension {

    private int depth = 1, width = 1, height = 1;

    public Dimension(Dimension other) {
        this.depth = other.depth;
        this.width = other.width;
        this.height = other.height;
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

    public int size(){
        return depth * width * height;
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
                '}';
    }
}
