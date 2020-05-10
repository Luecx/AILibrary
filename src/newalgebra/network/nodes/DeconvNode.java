package newalgebra.network.nodes;

import core.tensor.Tensor;
import core.tensor.Tensor4D;
import neuralnetwork.builder.BuildException;
import neuralnetwork.functions.Function;
import neuralnetwork.functions.ReLU;
import neuralnetwork.nodes.Node1To1;

public class DeconvNode extends Node1To1 {

    private int channel_amount;
    private int filter_size;
    private int filter_Stride;
    private int padding;

    private Tensor4D filter;
    private Tensor bias;


    public DeconvNode(int channel_amount, int filter_size, int filter_Stride, int padding) {
        this.channel_amount = channel_amount;
        this.filter_size = filter_size;
        this.filter_Stride = filter_Stride;
        this.padding = padding;
    }

    double weights_min = Double.NaN;
    double weight_max = Double.NaN;
    double bias_min = Double.NaN;
    double bias_max = Double.NaN;
    Function activation_function;

    public DeconvNode setActivationFunction(Function f) {
        this.activation_function = f;
        return this;
    }

    public DeconvNode setWeightRange(double lower, double upper) {
        weight_max = upper;
        weights_min = lower;
        return this;
    }

    public DeconvNode setBiasRange(double lower, double upper) {
        bias_max = upper;
        bias_min = lower;
        return this;
    }

    public int getChannel_amount() {
        return channel_amount;
    }

    public int getFilter_size() {
        return filter_size;
    }

    public int getFilter_Stride() {
        return filter_Stride;
    }

    public int getPadding() {
        return padding;
    }

    public Tensor4D getFilter() {
        return filter;
    }

    public Tensor getBias() {
        return bias;
    }

    public Function getActivation_function() {
        return activation_function;
    }

    public void setFilter(Tensor4D filter) {
        this.filter = filter;
    }

    public void setBias(Tensor bias) {
        this.bias = bias;
    }

    public void setActivation_function(Function activation_function) {
        this.activation_function = activation_function;
    }

    @Override
    protected void abs_calcOutputDim() throws BuildException {
        this.setOutputDepth(channel_amount);
        this.setOutputWidth(filter_Stride * (this.get_previous_node().getOutputWidth() - 1) + filter_size - 2 * padding);
        this.setOutputHeight(filter_Stride * (this.get_previous_node().getOutputHeight() - 1) + filter_size - 2 * padding);
    }

    @Override
    public void abs_genArrays() {
        if (this.filter == null) {
            filter = new Tensor4D(this.getInputDepth(), channel_amount, filter_size, filter_size);
            if (Double.isNaN(weights_min) && !Double.isNaN(weight_max)) {
                filter.randomizeRegular(weights_min, weight_max);
            } else {
                filter.randomizeRegular(
                        -1d / Math.sqrt(channel_amount * filter_size * filter_size),
                        1d / Math.sqrt(channel_amount * filter_size * filter_size));
            }
        }
        if (this.bias == null) {
//            bias = new Tensor(this.getOutputSize());
//            if (Double.isNaN(bias_min) && !Double.isNaN(bias_max)) {
//                bias.randomizeRegular(bias_min, bias_max);
//            } else {
//                filter.randomizeRegular(
//                        -1d / Math.sqrt(this.getInputDepth() * filter_size * filter_size),
//                        1d / Math.sqrt(this.getInputDepth() * filter_size * filter_size));
//            }
        }
        if (activation_function == null) {
            activation_function = new ReLU();
        }
    }

    @Override
    public void abs_feedForward() {
        this.output_value.reset(0);
        for (int i = 0; i < filter_size; i++) {
            for (int n = 0; n < filter_size; n++) {
                for (int input_x = 0; input_x < this.getInputWidth(); input_x++) {
                    for (int input_y = 0; input_y < this.getInputHeight(); input_y++) {
                        int x_o = -padding + (input_x * filter_Stride) + i;
                        int y_o = -padding + (input_y * filter_Stride) + n;
                        for (int j = 0; j < getOutputDepth(); j++) {
                            if (x_o >= 0 && y_o >= 0 && x_o < getOutputWidth() && y_o < getOutputHeight()) {
                                double sum = 0;
                                for (int input_d = 0; input_d < this.getInputDepth(); input_d++) {
                                    sum += this.filter.get(input_d, j, i, n) *
                                            getInputValue().get(input_d, input_x, input_y);
//                                    this.outputValue.add(this.filter.get(input_d, j, i, n) *
//                                            getInputValue().get(input_d, input_x, input_y), j, x_o, y_o);
                                }
                                this.output_value.add(sum, j, x_o, y_o);
                            }
                        }
                    }
                }
            }
        }
        this.activation_function.apply(this);
    }

    public void calcSample(int input_d, int input_x, int input_y) {
        for (int j = 0; j < getOutputDepth(); j++) {
            for (int i = 0; i < filter_size; i++) {
                for (int n = 0; n < filter_size; n++) {
                    int x_o = -padding + (input_x * filter_Stride) + i;
                    int y_o = -padding + (input_y * filter_Stride) + n;
                    if (x_o >= 0 && y_o >= 0 && x_o < getOutputWidth() && y_o < getOutputHeight()) {
                        this.output_value.add(this.filter.get(input_d, j, i, n) *
                                getInputValue().get(input_d, input_x, input_y), j, x_o, y_o);
                    }
                }
            }
        }
//            for (int x_i = (int) x_i_range.get(x, 0); x_i < x_i_range.get(x, 1); x_i++) {
//                for (int y_i = (int) y_i_range.get(y, 0); y_i < y_i_range.get(y, 1); y_i++) {
//                    total += this.filter.get(
//                            input_d,
//                            j,
//                            (int) filter_xy.get(x_i, x),
//                            (int) filter_xy.get(y_i, y))
//                            *
//                            getInputValue().get(j, x_i, y_i);
//                }
//            }
//        }
    }

    @Override
    public void abs_feedBackward() {
        this.input_loss.reset(0);
        for (int input_d = 0; input_d < this.getInputDepth(); input_d++) {
            for (int input_w = 0; input_w < this.getInputWidth(); input_w++) {
                for (int input_h = 0; input_h < this.getInputHeight(); input_h++) {
                    double loss_sum = 0;
                    for (int i = 0; i < filter_size; i++) {
                        for (int n = 0; n < filter_size; n++) {
                            int x_o = -padding + (input_w * filter_Stride) + i;
                            int y_o = -padding + (input_h * filter_Stride) + n;
                            for (int j = 0; j < getOutputDepth(); j++) {
                                if (x_o >= 0 && y_o >= 0 && x_o < getOutputWidth() && y_o < getOutputHeight()) {
                                    loss_sum += this.filter.get(input_d, j, i, n) *
                                            output_loss.get(j, x_o, y_o)
                                            * input_derivative.get(input_d, input_w, input_h);
                                }
                            }
                        }
                    }
                    input_loss.set(loss_sum, input_d, input_w, input_h);
                }
            }
        }
    }

    @Override
    public void abs_updateWeights(double eta) {
        for (int i = 0; i < filter_size; i++) {
            for (int n = 0; n < filter_size; n++) {
                for (int input_d = 0; input_d < this.getInputDepth(); input_d++) {
                    for (int output_d = 0; output_d < this.getOutputDepth(); output_d++) {
                        double dw = 0;
                        for (int input_x = 0; input_x < this.getInputWidth(); input_x++) {
                            for (int input_y = 0; input_y < this.getInputHeight(); input_y++) {
                                int x_o = -padding + (input_x * filter_Stride) + i;
                                int y_o = -padding + (input_y * filter_Stride) + n;
                                if (x_o >= 0 && y_o >= 0 && x_o < getOutputWidth() && y_o < getOutputHeight()) {
                                    dw -= this.output_loss.get(output_d, x_o, y_o) * input_value.get(input_d, input_x, input_y)
                                            * eta;
//                                    this.filter.add(
//                                            -this.output_loss.get(output_d, x_o, y_o) * input_value.get(input_d, input_x, input_y)
//                                                    * eta,
//                                            input_d,
//                                            output_d,
//                                            i,
//                                            n
//                                    );
                                }
                            }
                        }
                        this.filter.add(
                                dw,
                                input_d,
                                output_d,
                                i,
                                n
                        );
                    }
                }
            }
        }
    }
}
