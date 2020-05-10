package newalgebra.network.nodes;

import core.tensor.Tensor;
import core.tensor.Tensor2D;
import core.tensor.Tensor4D;
import neuralnetwork.builder.BuildException;
import neuralnetwork.functions.Function;
import neuralnetwork.functions.ReLU;
import neuralnetwork.nodes.Node1To1;
import newalgebra.cells.Dimension;
import newalgebra.cells.Variable;

public class ConvolutionNode extends Node {

    private int channel_amount;
    private int filter_size;
    private int filter_Stride;
    private int padding;

    private Variable filter;
    private Variable bias;

    private Tensor2D y_i_range;
    private Tensor2D x_i_range;
    private Tensor2D filter_xy;

    public ConvolutionNode(int channel_amount, int filter_size, int filter_Stride, int padding) {
        this.channel_amount = channel_amount;
        this.filter_size = filter_size;
        this.filter_Stride = filter_Stride;
        this.padding = padding;

        this.filter = new Variable();
        this.bias = new Variable();
    }

    double weights_min = Double.NaN;
    double weight_max = Double.NaN;
    double bias_min = Double.NaN;
    double bias_max = Double.NaN;


    public ConvolutionNode setWeightRange(double lower, double upper) {
        weight_max = upper;
        weights_min = lower;
        return this;
    }

    public ConvolutionNode setBiasRange(double lower, double upper) {
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

    public Variable getFilter() {
        return filter;
    }

    public Variable getBias() {
        return bias;
    }


    @Override
    public void calc() {
        super.calc();
    }

    @Override
    public void autoDiff() {
        super.autoDiff();
    }

    @Override
    public void generateInternalVariableDimension() {

//        this.filter

    }

    @Override
    public void generateOutputDimension() {
        Dimension outputDim = new Dimension(
                (this.getInputHeight() + this.padding * 2 - filter_size) / filter_Stride + 1,
                (this.getInputWidth() + this.padding * 2 - filter_size) / filter_Stride + 1,
                channel_amount
        );


        double g = ((double) this.getInputWidth()+ (double) this.padding * 2 - (double) filter_size) / (double) filter_Stride + 1;
        double g1 = ((double) this.getInputHeight() + (double) this.padding * 2 - (double) filter_size) / (double) filter_Stride + 1;
        if (g != (int) g || g1 != (int) g1)
            throw new RuntimeException("Format does not work! Use a different padding-value!");

        this.getOutput().setDimension(outputDim);
    }

    @Override
    public void initArrays() {
        super.initArrays();
    }



//    @Override
//    public void abs_genArrays() {
//        if (this.filter == null) {
//            filter = new Tensor4D(channel_amount, this.getInputDepth(), filter_size, filter_size);
//            if (!Double.isNaN(weights_min) && !Double.isNaN(weight_max)) {
//                filter.randomizeRegular(weights_min, weight_max);
//            } else {
//                filter.randomizeRegular(
//                        -1d / Math.sqrt(this.getInputDepth() * filter_size * filter_size),
//                        1d / Math.sqrt(this.getInputDepth() * filter_size * filter_size));
//            }
//        }
//        if (this.bias == null) {
//            bias = new Tensor(this.getChannel_amount());
//            if (!Double.isNaN(bias_min) && !Double.isNaN(bias_max)) {
//                bias.randomizeRegular(bias_min, bias_max);
//            } else {
//                filter.randomizeRegular(
//                        -1d / Math.sqrt(this.getInputDepth() * filter_size * filter_size),
//                        1d / Math.sqrt(this.getInputDepth() * filter_size * filter_size));
//            }
//        }
//        if (activation_function == null) {
//            activation_function = new ReLU();
//        }
//        this.x_i_range = new Tensor2D(this.getOutputWidth(), 2);
//        this.y_i_range = new Tensor2D(this.getOutputHeight(), 2);
//        this.filter_xy = new Tensor2D(Math.max(this.getInputWidth(), this.getInputHeight()),
//                Math.max(this.getOutputWidth(), this.getOutputHeight()));
//        for (int j = 0; j < this.filter_xy.getDimension(0); j++) {
//            for (int i = 0; i < this.filter_xy.getDimension(1); i++) {
//                this.filter_xy.set(j + padding - i * filter_Stride, j, i);
//            }
//        }
//        for (int j = 0; j < this.getOutputWidth(); j++) {
//            this.x_i_range.set(Math.max(0, -padding + (j * filter_Stride) + 0), j, 0);
//            this.x_i_range.set(Math.min(this.getInputWidth(), -padding + (j * filter_Stride) + filter_size), j, 1);
//        }
//        for (int j = 0; j < this.getOutputHeight(); j++) {
//            this.y_i_range.set(Math.max(0, -padding + (j * filter_Stride) + 0), j, 0);
//            this.y_i_range.set(Math.min(this.getInputHeight(), -padding + (j * filter_Stride) + filter_size), j, 1);
//        }
//    }
//
//    @Override
//    public void abs_feedForward() {
//        for (int i = 0; i < this.getOutputDepth(); i++) {
//            for (int j = 0; j < this.getOutputWidth(); j++) {
//                for (int n = 0; n < this.getOutputHeight(); n++) {
//                    this.output_value.set(this.calcSample(i, j, n), i, j, n);
//                }
//            }
//        }
//        this.activation_function.apply(this);
//    }
//
//    public double calcSample(int actIndex, int x, int y) {
//        double total = bias.getData()[actIndex];
//        for (int j = 0; j < getInputDepth(); j++) {
////            for (int i = 0; i < filter_size; i++) {
////                for (int n = 0; n < filter_size; n++) {
////                    int x_i = -padding + (x * filter_Stride) + i;
////                    int y_i = -padding + (y * filter_Stride) + n;
////                    if (x_i >= 0 && y_i >= 0 && x_i < getINPUT_WIDTH() && y_i < getINPUT_HEIGHT()) {
////                        total += this.filter[actIndex][j][i][n] *
////                                getInput_values()[j][x_i][y_i];
////                    }
////
////                }
////            }
//            for (int x_i = (int) x_i_range.get(x, 0); x_i < x_i_range.get(x, 1); x_i++) {
//                for (int y_i = (int) y_i_range.get(y, 0); y_i < y_i_range.get(y, 1); y_i++) {
////                    System.out.println(x_i + "  " + y_i + "  " +
////                            filter_xy.get(x_i, x) + "  " + filter_xy.get(y_i, y) + "  " +
////                            this.filter.get(
////                                    actIndex,
////                                    j,
////                                    (int) filter_xy.get(x_i, x),
////                                    (int) filter_xy.get(y_i, y))
////                    );
////                    System.out.println(getInputValue().get(j,x_i, y_i));
//                    total += this.filter.get(
//                            actIndex,
//                            j,
//                            (int) filter_xy.get(x_i, x),
//                            (int) filter_xy.get(y_i, y))
//                            *
//                            getInputValue().get(j, x_i, y_i);
//                }
//            }
//        }
//        return total;
//    }
//
//    @Override
//    public void abs_feedBackward() {
//        for (int i = 0; i < getInputSize(); i++) {
//            this.input_loss.getData()[i] = 0;
//        }
//        for (int output_d = 0; output_d < this.getOutputDepth(); output_d++) {
//            for (int output_w = 0; output_w < this.getOutputWidth(); output_w++) {
//                for (int output_h = 0; output_h < this.getOutputHeight(); output_h++) {
//                    for (int j = 0; j < getInputDepth(); j++) {
//                        for (int x_i = (int) x_i_range.get(output_w, 0);
//                             x_i < x_i_range.get(output_w, 1); x_i++) {
//                            for (int y_i = (int) y_i_range.get(output_h, 0);
//                                 y_i < y_i_range.get(output_h, 1); y_i++) {
//                                this.input_loss.getData()[this.input_loss.index(j, x_i, y_i)] +=
//                                        this.filter.get(
//                                                output_d,
//                                                j,
//                                                (int) filter_xy.get(x_i, output_w),
//                                                (int) filter_xy.get(y_i, output_h)) *
//                                                this.output_loss.get(output_d, output_w, output_h) *
//                                                this.input_derivative.get(
//                                                        j, x_i, y_i);
//                            }
//                        }
////
////                        for (int i = 0; i < filter_size; i++) {
////                            for (int n = 0; n < filter_size; n++) {
////                                int x_i = -padding + (output_w * filter_Stride) + i;
////                                int y_i = -padding + (output_h * filter_Stride) + n;
////                                if (x_i >= 0 && y_i >= 0 && x_i < getINPUT_WIDTH() && y_i < getINPUT_HEIGHT()) {
////                                    this.getPrev_layer().output_error_values[j][x_i][y_i] +=
////
////                                            this.filter[output_d][j][i][n] * output_error_values[output_d][output_w][output_h]
////                                                    * getPrev_layer().output_derivative_values[j][i][n];
////                                }
////
////                            }
////                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void abs_updateWeights(double eta) {
//        for (int output_d = 0; output_d < this.getOutputDepth(); output_d++) {
//            for (int output_w = 0; output_w < this.getOutputWidth(); output_w++) {
//                for (int output_h = 0; output_h < this.getOutputHeight(); output_h++) {
//
//                    bias.getData()[output_d] -= output_loss.get(output_d, output_w, output_h) * eta;
//                        for (int x_i = (int) x_i_range.get(output_w, 0); x_i < x_i_range.get(output_w, 1); x_i++) {
//                            for (int y_i = (int) y_i_range.get(output_h, 0); y_i < y_i_range.get(output_h, 1); y_i++) {
//                                for (int j = 0; j < getInputDepth(); j++) {
//                                this.filter.getData()[this.filter.index(output_d, j,
//                                        (int) filter_xy.get(x_i, output_w),
//                                        (int) filter_xy.get(y_i, output_h))] +=
//                                        -getOutputLoss().get(output_d, output_w, output_h) * getInputValue().get(j, x_i, y_i) * eta;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
