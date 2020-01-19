package neuralnetwork.builder;

import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.functions.ReLU;
import neuralnetwork.functions.Sigmoid;
import neuralnetwork.loss.Error;
import neuralnetwork.network.*;
import neuralnetwork.nodes.Node;
import parser.parser.Parser;
import parser.parser.ParserTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Network {

    private Node[] calculation_order;
    private InputNode[] input_nodes;
    private OutputNode[] output_nodes;

    public Network(Node[] calculation_order, InputNode[] input_nodes, OutputNode[] output_nodes) {
        this.calculation_order = calculation_order;
        this.input_nodes = input_nodes;
        this.output_nodes = output_nodes;
    }

    public Tensor3D[] calculate(Tensor3D... input) {
        try {
            if (input.length > input_nodes.length) {
                throw new ArgumentException("Too many inputs given");
            }
            for (int i = 0; i < input.length; i++) {
                if (input[i].size() != input_nodes[i].getOutputSize()) {
                    throw new ArgumentException("Input [" + i + "] does not have the correct size");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getOutput();
        }
        feed(input);
        return getOutput();
    }

    public Tensor3D[] calculate(Tensor3D input) {
        try {
            if (input.size() != input_nodes[0].getOutputSize()) {
                throw new ArgumentException("Input [" + 0 + "] does not have the correct size");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getOutput();
        }
        feed(input);
        return getOutput();
    }

    private void feed(Tensor3D... input) {
        for (int i = 0; i < input_nodes.length; i++) {
            if(input[i].size() != input_nodes[i].getOutputSize()) throw new RuntimeException();
            input_nodes[i].setOutputValue(input[i]);
        }
        for (Node i : calculation_order) {
            i.abs_feedForward();
        }
    }

    private void feed(Tensor3D input) {
        input_nodes[0].setOutputValue(input);
        for (Node i : calculation_order) {
            i.abs_feedForward();
        }
    }

    private double backpropagateError(Tensor... exp) {
        double e = 0;
        for (int i = 0; i < output_nodes.length; i++) {
            e += output_nodes[i].calculateLoss(exp[i]);
        }
        for (int i = calculation_order.length - 1; i >= 0; i--) {
            calculation_order[i].abs_feedBackward();
        }
        return e;
    }

    private double backpropagateError(Tensor exp) {
        double e = output_nodes[0].calculateLoss(exp);
        for (int i = calculation_order.length - 1; i >= 0; i--) {
            calculation_order[i].abs_feedBackward();
        }
        return e;
    }

    private void updateWeights(double eta) {
        for (int i = calculation_order.length - 1; i >= 0; i--) {
            calculation_order[i].abs_updateWeights(eta);
        }
    }

    public double train(Tensor3D[] in, Tensor3D[] out, double eta) {
        feed(in);
        double e = backpropagateError(out);
        updateWeights(eta);
        return e;
    }

    public double train(Tensor3D[] in, Tensor3D out, double eta) {
        feed(in);
        double e = backpropagateError(out);
        updateWeights(eta);
        return e;
    }

    public double train(Tensor3D in, Tensor3D[] out, double eta) {
        feed(in);
        double e = backpropagateError(out);
        updateWeights(eta);
        return e;
    }

    public double train(Tensor3D in, Tensor3D out, double eta) {
        feed(in);
        double e = backpropagateError(out);
        updateWeights(eta);
        return e;
    }

    public void print_inputLoss() {
        System.out.print("########################################################" +
                "#########################################################");
        for (Node l : calculation_order) {
            System.out.println("\n" + l.getIdentifier());
            System.out.println(l.getInputLoss());
            System.out.print("-------------------------------------------------------------" +
                    "----------------------------------------------------");
        }
        System.out.println("\r########################################################" +
                "#########################################################");
    }

    public void print_outputLoss() {
        System.out.print("########################################################" +
                "#########################################################");
        for (Node l : calculation_order) {
            System.out.println("\n" + l.getIdentifier());
            System.out.println(l.getOutputLoss());
            System.out.print("-------------------------------------------------------------" +
                    "----------------------------------------------------");
        }
        System.out.println("\r########################################################" +
                "#########################################################");
    }

    public void print_derivative() {
        System.out.print("########################################################" +
                "#########################################################");
        for (Node l : calculation_order) {
            System.out.println("\n" + l.getIdentifier());
            System.out.println(l.getOutputDerivative());
            System.out.print("-------------------------------------------------------------" +
                    "----------------------------------------------------");
        }
        System.out.println("\r########################################################" +
                "#########################################################");
    }

    public void print_outputs() {
        System.out.print("########################################################" +
                "#########################################################");
        for (Node l : calculation_order) {
            System.out.println("\n" + l.getIdentifier());
            System.out.println(l.getOutputValue());
            System.out.print("-------------------------------------------------------------" +
                    "----------------------------------------------------");
        }
        System.out.println("\r########################################################" +
                "#########################################################");
    }

    public void print_overview() {
        System.out.print("###########################################################" +
                "############################################" +
                "#########################################################");
        for (Node l : calculation_order) {
            String prev = "[";
            String next = "[";
            if (l.hasPreviousNode()) {
                for (Node n : l.getPreviousNodes()) {
                    prev += n.getIdentifier() + " ";
                }
                prev = prev.substring(0, prev.length() - 1) + "]";
            } else {
                prev += " - ]";
            }
            if (l.hasNextNode()) {
                for (Node n : l.getNextNodes()) {
                    next += n.getIdentifier() + " ";
                }
                next = next.substring(0, next.length() - 1) + "]";
            } else {
                next += " - ]";
            }
            int params = 0;
            int connections = 0;
            String func = "None";
            if (l instanceof ConvolutionNode) {
                params += ((ConvolutionNode) l).getFilter_size() * ((ConvolutionNode) l).getFilter_size() * l.getInputDepth() *
                        l.getOutputDepth();
                connections += l.getOutputDepth() * l.getOutputHeight() * l.getOutputWidth() * l.getInputDepth() *
                        ((ConvolutionNode) l).getFilter_size() * ((ConvolutionNode) l).getFilter_size();
                func = ((ConvolutionNode) l).getActivation_function().getClass().getSimpleName();
            } else if (l instanceof DeconvNode) {
                params += ((DeconvNode) l).getFilter_size() * ((DeconvNode) l).getFilter_size() * l.getInputDepth() *
                        l.getOutputDepth();
                connections += l.getInputDepth() * l.getInputHeight() * l.getInputWidth() * l.getOutputDepth() *
                        ((DeconvNode) l).getFilter_size() * ((DeconvNode) l).getFilter_size();
                func = ((DeconvNode) l).getActivation_function().getClass().getSimpleName();
            } else if (l instanceof DenseNode) {
                params += l.getOutputSize() * l.getInputSize();
                connections += l.getOutputSize() * l.getInputSize();
                func = ((DenseNode) l).getActivation_function().getClass().getSimpleName();
            } else {
                connections += l.getOutputSize();
            }
            System.out.format("\n%-40s %-15s %-15s %-16s %-30s %-30s %-20s \n",
                    l.getClass().getSimpleName() + "[" + l.getIdentifier() + "]",
                    "[" + l.getOutputDepth() + " " + l.getOutputWidth() + " " + l.getOutputHeight() + "]",
                    "params:" + params,
                    "conns: " + connections,
                    prev,
                    next,
                    func
            );
            System.out.print("----------------------------------------------------------------------------------------" +
                    "------------------------------------------------------------------------");
        }
        System.out.println("\r###########################################################" +
                "############################################" +
                "#########################################################");
    }

    public void print_timecheck() {
        print_timecheck(100);
    }

    public void print_timecheck(int amount) {
        Tensor3D[] in = new Tensor3D[input_nodes.length];
        Tensor3D[] out = new Tensor3D[output_nodes.length];
        for (int i = 0; i < in.length; i++) {
            in[i] = new Tensor3D(input_nodes[i].getOutputDepth(), input_nodes[i].getOutputWidth(), input_nodes[i].getOutputHeight());
            in[i].randomizeRegular(-1, 1);
        }
        for (int i = 0; i < out.length; i++) {
            out[i] = new Tensor3D(output_nodes[i].getOutputDepth(), output_nodes[i].getOutputWidth(), output_nodes[i].getOutputHeight());
            out[i].randomizeRegular(-1, 1);
        }
        System.out.println("########################################################" +
                "#########################################################");
        long k = System.nanoTime();
        long t;
        long[] timingsForward = new long[this.calculation_order.length];
        long[] timingsBackward = new long[this.calculation_order.length];
        long[] timingsWeights = new long[this.calculation_order.length];
        for (int i = 0; i < amount; i++) {

            //############################# forward #################################
            for (int n = 0; n < input_nodes.length; n++) {
                input_nodes[n].setOutputValue(in[n]);
            }
            for(int c = 0; c < calculation_order.length; c++){
                Node n=calculation_order[c];
                t = System.nanoTime();
                n.abs_feedForward();
                timingsForward[c] += System.nanoTime() - t;
            }
            //############################# forward #################################
            //############################# backward ################################
            for (int n = 0; n < output_nodes.length; n++) {
                output_nodes[n].calculateLoss(out[n]);
            }
            for (int n = calculation_order.length - 1; n >= 0; n--) {
                t = System.nanoTime();
                calculation_order[n].abs_feedBackward();
                timingsBackward[n] += System.nanoTime() - t;
            }
            //############################# backward ################################
            //############################# weights ################################
            for (int n = calculation_order.length - 1; n >= 0; n--) {
                t = System.nanoTime();
                calculation_order[n].abs_updateWeights(0);
                timingsWeights[n] += System.nanoTime() - t;
            }
            //############################# weights ################################

            System.out.print("\r"+(i+1) + "/" + amount);
        }
        System.out.println();
        System.out.println("Average: " + (System.nanoTime() - k) / (10e5d * amount) + " ms");
        System.out.println("########################################################" +
                "#########################################################");

        System.out.print("\r###########################################################" +
                "############################################" +
                "#########################################################");
        for(int i = 0; i < calculation_order.length; i++){
            System.out.format("\n%-40s forward[ms]: %-15s backward[ms]: %-15s update[ms]: %-15s \n",
                    calculation_order[i].getClass().getSimpleName() + "[" + calculation_order[i].getIdentifier() + "]",
                    timingsForward[i] / amount / 1E6d,
                    timingsBackward[i] / amount / 1E6d,
                    timingsWeights[i] / amount / 1E6d
            );
            System.out.print("----------------------------------------------------------------------------------------" +
                    "------------------------------------------------------------------------");
        }
        System.out.println("\r###########################################################" +
                "############################################" +
                "#########################################################");
    }

    public Tensor3D[] getOutput() {
        Tensor3D[] ar = new Tensor3D[output_nodes.length];
        for (int i = 0; i < output_nodes.length; i++) {
            ar[i] = new Tensor3D(output_nodes[i].getOutputValue());
        }
        return ar;
    }

    public InputNode getInputNode(int index) {
        return input_nodes[index];
    }

    public OutputNode getOutputNode(int index) {
        return output_nodes[index];
    }

    public Node[] getNodes() {
        return calculation_order;
    }

    public static Node parsing_generateNode(parser.tree.Node node) {
        String name = node.getName();
        Node result = null;
        switch (name) {
            case "InputNode":
                result = new InputNode(
                        Integer.parseInt(node.getAttribute("depth").getValue()),
                        Integer.parseInt(node.getAttribute("width").getValue()),
                        Integer.parseInt(node.getAttribute("height").getValue())
                );
                break;
            case "ConvolutionNode":
                result = new ConvolutionNode(
                        Integer.parseInt(node.getAttribute("channel_amount").getValue()),
                        Integer.parseInt(node.getAttribute("filter_size").getValue()),
                        Integer.parseInt(node.getAttribute("filter_stride").getValue()),
                        Integer.parseInt(node.getAttribute("padding").getValue())
                );
                switch (node.getAttribute("activation_function").getValue()) {
                    case "ReLU":
                        ((ConvolutionNode) result).setActivationFunction(new ReLU());
                        break;
                    case "Sigmoid":
                        ((ConvolutionNode) result).setActivationFunction(new Sigmoid());
                        break;
                }
                break;
            case "DeconvNode":
                result = new DeconvNode(
                        Integer.parseInt(node.getAttribute("channel_amount").getValue()),
                        Integer.parseInt(node.getAttribute("filter_size").getValue()),
                        Integer.parseInt(node.getAttribute("filter_stride").getValue()),
                        Integer.parseInt(node.getAttribute("padding").getValue())
                );
                switch (node.getAttribute("activation_function").getValue()) {
                    case "ReLU":
                        ((DeconvNode) result).setActivationFunction(new ReLU());
                        break;
                    case "Sigmoid":
                        ((DeconvNode) result).setActivationFunction(new Sigmoid());
                        break;
                }
                break;
            case "OutputNode":
                result = new OutputNode();
                break;
            case "DenseNode":
                result = new DenseNode(
                        Integer.parseInt(node.getAttribute("height").getValue())
                );
                switch (node.getAttribute("activation_function").getValue()) {
                    case "ReLU":
                        ((DenseNode) result).setActivationFunction(new ReLU());
                        break;
                    case "Sigmoid":
                        ((DenseNode) result).setActivationFunction(new Sigmoid());
                        break;
                }
                break;
            case "FlattenNode":
                result = new FlattenNode();
                break;
            case "ShapeNode":
                result = new ShapeNode(
                        Integer.parseInt(node.getAttribute("depth").getValue()),
                        Integer.parseInt(node.getAttribute("width").getValue()),
                        Integer.parseInt(node.getAttribute("height").getValue())
                );
                break;
            case "PoolingNode":
                result = new PoolingNode(
                        Integer.parseInt(node.getAttribute("pooling_factor").getValue())
                );
                break;
            case "StackNode":
                result = new StackNode();
                break;
            case "SplitNode":
                result = new SplitNode();
                break;
        }
        result.setIdentifier(node.getAttribute("identity").getValue());
        return result;
    }

    public static Network load(String file) {
        try {
            Parser parser = new Parser();
            parser.load(file);

            HashMap<String, parser.tree.Node> map = new HashMap<>();

            Builder builder = new Builder();
            for (parser.tree.Node n : parser.getContent().getChilds().get(0).getChilds()) {
                Node k = parsing_generateNode(n);
                map.put(k.getIdentifier(), n);
                if (k instanceof InputNode) {
                    builder.addNode(k);
                } else {
                    builder.addNode(k, n.getAttribute("prev").getValue().split(" "));
                }
            }
            Network result = builder.build_network();

            for(Node n:result.calculation_order){
                if(n instanceof ConvolutionNode) {
                    ((ConvolutionNode) n).getFilter().setData(
                            ParserTools.parseDoubleArray(map.get(n.getIdentifier()).getAttribute("filter").getValue()));
                    ((ConvolutionNode) n).getBias().setData(
                            ParserTools.parseDoubleArray(map.get(n.getIdentifier()).getAttribute("bias").getValue()));
                }else if(n instanceof DeconvNode){
                        ((DeconvNode) n).getFilter().setData(
                                ParserTools.parseDoubleArray(map.get(n.getIdentifier()).getAttribute("filter").getValue()));
                        //((DeconvNode) n).getBias().setData(
                        //        ParserTools.parseDoubleArray(map.get(n.getIdentifier()).getAttribute("bias").getValue()));
                }else if(n instanceof DenseNode){
                    ((DenseNode) n).getWeights().setData(
                            ParserTools.parseDoubleArray(map.get(n.getIdentifier()).getAttribute("weights").getValue()));
                    ((DenseNode) n).getBias().setData(
                            ParserTools.parseDoubleArray(map.get(n.getIdentifier()).getAttribute("bias").getValue()));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String file) {
        try {
            Parser parser = new Parser();
            parser.create(file);
            parser.tree.Node root = new parser.tree.Node("Network");
            for (Node node : this.calculation_order) {
                parser.tree.Node result = new parser.tree.Node(node.getClass().getSimpleName());
                result.addAttribute("identity", node.getIdentifier());
                String prev = "";
                for (Node n : node.getPreviousNodes()) {
                    prev += n.getIdentifier() + " ";
                }
                result.addAttribute("prev", prev.trim());
                String next = "";
                for (Node n : node.getNextNodes()) {
                    next += n.getIdentifier() + " ";
                }
                result.addAttribute("next", next.trim());
                switch (node.getClass().getSimpleName()) {
                    case "InputNode":
                        result.addAttribute("depth", "" + ((InputNode) node).getOutputDepth());
                        result.addAttribute("width", "" + ((InputNode) node).getOutputWidth());
                        result.addAttribute("height", "" + ((InputNode) node).getOutputHeight());
                        break;
                    case "ConvolutionNode":
                        result.addAttribute("channel_amount", "" + ((ConvolutionNode) node).getChannel_amount());
                        result.addAttribute("filter_size", "" + ((ConvolutionNode) node).getFilter_size());
                        result.addAttribute("filter_stride", "" + ((ConvolutionNode) node).getFilter_Stride());
                        result.addAttribute("padding", "" + ((ConvolutionNode) node).getPadding());
                        result.addAttribute("activation_function", ((ConvolutionNode) node).getActivation_function().getClass().getSimpleName());
                        result.addAttribute("filter", Arrays.toString(((ConvolutionNode) node).getFilter().getData()));
                        result.addAttribute("bias", Arrays.toString(((ConvolutionNode) node).getBias().getData()));
                        break;
                    case "DeconvNode":
                        result.addAttribute("channel_amount", "" + ((DeconvNode) node).getChannel_amount());
                        result.addAttribute("filter_size", "" + ((DeconvNode) node).getFilter_size());
                        result.addAttribute("filter_stride", "" + ((DeconvNode) node).getFilter_Stride());
                        result.addAttribute("padding", "" + ((DeconvNode) node).getPadding());
                        result.addAttribute("activation_function", ((DeconvNode) node).getActivation_function().getClass().getSimpleName());
                        result.addAttribute("filter", Arrays.toString(((DeconvNode) node).getFilter().getData()));
                        //result.addAttribute("bias", Arrays.toString(((DeconvNode) node).getBias().getData()));
                        break;
                    case "DenseNode":
                        result.addAttribute("height", "" + ((DenseNode) node).getOutputHeight());
                        result.addAttribute("activation_function", ((DenseNode) node).getActivation_function().getClass().getSimpleName());
                        result.addAttribute("weights", Arrays.toString(((DenseNode) node).getWeights().getData()));
                        result.addAttribute("bias", Arrays.toString(((DenseNode) node).getBias().getData()));
                        break;
                    case "PoolingNode":
                        result.addAttribute("pooling_factor", "" + ((PoolingNode) node).getPooling_factor());
                        break;
                    case "ShapeNode":
                        result.addAttribute("depth", "" + node.getOutputDepth());
                        result.addAttribute("width", "" + node.getOutputWidth());
                        result.addAttribute("height", "" + node.getOutputHeight());

                        break;
                }
                root.addChild(result);
            }
            parser.addNode(root);
            parser.write();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Node[] getCalculation_order() {
        return calculation_order;
    }

    public Network setLossFunction(int index, Error error) {
        output_nodes[index].setErrorFunction(error);
        return this;
    }

    public Network setLossFactor(int index, double val) {
        output_nodes[index].setErrorFactor(val);
        return this;
    }

    public Network setLossFunction(Error error) {
        output_nodes[0].setErrorFunction(error);
        return this;
    }

    public Network setLossFactor(double val) {
        output_nodes[0].setErrorFactor(val);
        return this;
    }
}
