package neuralnetwork.network;

import core.tensor.Tensor;
import neuralnetwork.builder.BuildException;
import neuralnetwork.nodes.Node1To1;

public class PoolingNode extends Node1To1 {

    private int pooling_factor;
    private int pooling_mode = 0;
    private Tensor field_sizes;

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
    protected void abs_calcOutputDim() throws BuildException {
        this.setOutputDepth(this.getInputDepth());
        this.setOutputWidth(this.getInputWidth() / pooling_factor + (this.getInputWidth() % pooling_factor > 0 ? 1 : 0));
        this.setOutputHeight(this.getInputHeight() / pooling_factor + (this.getInputHeight() % pooling_factor > 0 ? 1 : 0));
    }

    @Override
    public void abs_genArrays() {
        field_sizes = new Tensor(this.getInputWidth(), this.getInputHeight());

    }

    @Override
    public void abs_feedForward() {


        if(this.pooling_mode == MAX_POOLING){
            for (int i = 0; i < this.getOutputDepth(); i++) {
                for (int n = 0; n < this.getOutputWidth(); n++) {
                    for (int k = 0; k < this.getOutputHeight(); k++) {
                        double max = 0;
                        double d = 0;
                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {

                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;

                                if (x_i < this.getInputWidth() && y_i < this.getInputHeight()) {
                                    if (input_value.get(i,x_i,y_i) > max) {
                                        max = input_value.get(i,x_i,y_i);
                                        d = input_derivative.get(i,x_i,y_i);
                                    }
                                }
                            }
                        }
                        output_value.set(max, i,n,k);
                        output_derivative.set(d,i,n,k);
                    }
                }
            }
        }else{
            for (int i = 0; i < this.getOutputDepth(); i++) {
                for (int n = 0; n < this.getOutputWidth(); n++) {
                    for (int k = 0; k < this.getOutputHeight(); k++) {
                        double t = 0;
                        double d = 0;
                        int m = 0;
                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {

                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;

                                if (x_i < this.getInputWidth() && y_i < this.getInputHeight()) {
                                    m++;
                                    t += input_value.get(i,x_i,y_i);
                                    d += input_derivative.get(i,x_i,y_i);
                                }
                            }
                        }
                        output_value.set(t/m,i,n,k);
                        output_derivative.set(d/m,i,n,k);
                    }
                }
            }
        }


    }

    @Override
    public void abs_feedBackward() {
        int offset_x = this.getInputWidth() - pooling_factor * (this.getInputWidth() / pooling_factor);
        int offset_y = this.getInputHeight() - pooling_factor * (this.getInputHeight() / pooling_factor);
        if(this.pooling_mode == MEAN_POOLING){
            for (int i = 0; i < this.getOutputDepth(); i++) {
                for (int n = 0; n < this.getOutputWidth(); n++) {
                    for (int k = 0; k < this.getOutputHeight(); k++) {
                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {

                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;
                                int v;

                                if (x_i < this.getInputWidth() && y_i < this.getInputHeight()) {
                                    if (x_i >= getInputWidth()-offset_x) {
                                        if(y_i >= getInputHeight() -offset_y){
                                            v = (offset_x * offset_y);
                                        }else{
                                            v = (offset_x * pooling_factor);
                                        }
                                    }else{
                                        if(y_i >= getInputHeight() -offset_y){
                                            v = (offset_y * pooling_factor);
                                        }else{
                                            v = (pooling_factor * pooling_factor);
                                        }
                                    }
                                    input_loss.set(output_loss.get(i,n,k) / (v),i,x_i, y_i);
                                }

                            }
                        }
                    }
                }
            }
        }else{
            for (int i = 0; i < this.getOutputDepth(); i++) {
                for (int n = 0; n < this.getOutputWidth(); n++) {
                    for (int k = 0; k < this.getOutputHeight(); k++) {

                        for (int x = 0; x < pooling_factor; x++) {
                            for (int y = 0; y < pooling_factor; y++) {
                                int x_i = n * pooling_factor + x;
                                int y_i = k * pooling_factor + y;

                                if (x_i < this.getInputWidth() && y_i < this.getInputHeight()) {
                                    if(output_value.get(i,n,k) == input_value.get(i,x_i, y_i)){
                                        input_loss.set(output_loss.get(i,n,k),i,x_i,y_i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void abs_updateWeights(double eta) {

    }
}
