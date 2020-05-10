package newalgebra.network.nodes;

import core.tensor.Tensor;
import neuralnetwork.builder.BuildException;
import neuralnetwork.nodes.Node1To1;
import newalgebra.cells.Dimension;

public class PoolingNode extends Node<PoolingNode> {

    private int pooling_factor;
    private int pooling_mode = 0;

    public static final int MEAN_POOLING = 0;
    public static final int MAX_POOLING = 1;


    public PoolingNode(int pooling_factor) {
        super();
        this.pooling_factor = pooling_factor;
    }

    public PoolingNode setPooling_mode(int pooling_mode) {
        this.pooling_mode = pooling_mode;
        return this;
    }

    public int getPooling_factor() {
        return pooling_factor;
    }

    public int getPooling_mode() {
        return pooling_mode;
    }

    @Override
    public void generateOutputDimension() {

        if(this.getInput().getDimension().dimCount() > 3) throw new RuntimeException("Cannot pool more than 3 dimensions!");

        Dimension inputDim = this.getInput().getDimension();

        Dimension outputDim = new Dimension(
                inputDim.getHeight() / pooling_factor + inputDim.getHeight() % pooling_factor > 0 ? 1:0,
                inputDim.getWidth() / pooling_factor + inputDim.getWidth() % pooling_factor > 0 ? 1:0,
                inputDim.getDepth()
        );


    }

    @Override
    public void calc() {
        if(this.pooling_mode == MAX_POOLING){
            for (int i = 0; i < this.getOutput().getDimension().getDepth(); i++) {
                for (int n = 0; n < this.getOutput().getDimension().getWidth(); n++) {
                    for (int k = 0; k < this.getOutput().getDimension().getHeight(); k++) {
                        double max = 0;
                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {

                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;

                                if (x_i < this.getInput().getDimension().getWidth() && y_i < this.getInput().getDimension().getHeight()) {
                                    if (getInput().getValue().get(y_i,x_i, i) > max) {
                                        max = getInput().getValue().get(y_i,x_i, i);
                                    }
                                }
                            }
                        }
                        getOutput().getValue().set(max, k,n,i);
                    }
                }
            }
        }else{
            for (int i = 0; i < this.getOutput().getDimension().getDepth(); i++) {
                for (int n = 0; n < this.getOutput().getDimension().getWidth(); n++) {
                    for (int k = 0; k < this.getOutput().getDimension().getHeight(); k++) {
                        double t = 0;
                        int m = 0;
                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {

                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;

                                if (x_i < this.getInput().getDimension().getWidth() && y_i < this.getInput().getDimension().getHeight()) {
                                    m++;
                                    t += getInput().getValue().get(i,x_i,y_i);
                                }
                            }
                        }
                        getOutput().getValue().set(t/m, k,n,i);
                    }
                }
            }
        }
    }



    @Override
    public void autoDiff() {
        int offset_x = this.getInput().getDimension().getWidth() - pooling_factor * ( this.getInput().getDimension().getWidth() / pooling_factor);
        int offset_y = this.getInput().getDimension().getHeight() - pooling_factor * (this.getInput().getDimension().getHeight() / pooling_factor);
        if(this.pooling_mode == MEAN_POOLING){
            for (int i = 0; i < this.getOutput().getDimension().getDepth(); i++) {
                for (int n = 0; n < this.getOutput().getDimension().getWidth(); n++) {
                    for (int k = 0; k < this.getOutput().getDimension().getHeight(); k++) {
                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {

                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;
                                int v;

                                if (x_i < this.getInput().getDimension().getWidth() && y_i < this.getInput().getDimension().getHeight()) {
                                    if (x_i >= getInput().getDimension().getWidth()-offset_x) {
                                        if(y_i >= getInput().getDimension().getHeight() -offset_y){
                                            v = (offset_x * offset_y);
                                        }else{
                                            v = (offset_x * pooling_factor);
                                        }
                                    }else{
                                        if(y_i >= getInput().getDimension().getHeight() -offset_y){
                                            v = (offset_y * pooling_factor);
                                        }else{
                                            v = (pooling_factor * pooling_factor);
                                        }
                                    }
                                    getInput().getGradient().add(getOutput().getGradient().get(k,n,i) / v, y_i, x_i, i);
                                }

                            }
                        }
                    }
                }
            }
        }else{
            for (int i = 0; i < this.getOutput().getDimension().getDepth(); i++) {
                for (int n = 0; n < this.getOutput().getDimension().getWidth(); n++) {
                    for (int k = 0; k < this.getOutput().getDimension().getHeight(); k++) {

                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {
                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;

                                if (x_i < this.getInput().getDimension().getWidth() && y_i < this.getInput().getDimension().getHeight()) {
                                    if(getOutput().getValue().get(k,n,i) == getInput().getValue().get(y_i,x_i,i)){
                                        getInput().getGradient().add(getOutput().getGradient().get(k,n,i), y_i, x_i, i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }


}
