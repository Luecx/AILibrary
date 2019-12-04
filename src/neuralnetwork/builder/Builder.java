package neuralnetwork.builder;

import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.data.TrainSet;
import neuralnetwork.network.*;
import neuralnetwork.network.special.GramNode;
import neuralnetwork.nodes.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Builder {


    HashMap<String, Node> nodes = new HashMap<>();
    ArrayList<Node> order = new ArrayList<>();
    ArrayList<InputNode> inputs = new ArrayList<>();
    ArrayList<OutputNode> outputs = new ArrayList<>();

    public Builder(int d, int w, int h) {
        InputNode in = new InputNode(d, w, h);
        this.addNode("input_node_1", in);
    }

    public Builder() {
    }

    public Network build_network() {
        finish_layers();
        generate_output_dimensions();
        for (Node n : order) {
            n.build();
        }
        Network k = new Network(
                order.toArray(new Node[0]),
                inputs.toArray(new InputNode[0]),
                outputs.toArray(new OutputNode[0]));

        return k;
    }

    public void generate_output_dimensions() {
        for (Node n : order) {
            n.calcOutputDim();
        }
    }

    public void finish_layers() {
        int output_index = 1;

        Set<String> identifier = nodes.keySet();
        ArrayList<Node> nodes_to_add = new ArrayList<>();

        try {
            for (Node n : order) {
                if (!n.hasPreviousNode() && !(n instanceof InputNode)) {
                    throw new BuildException(n, "No previous nodes");
                }
                if (!n.hasNextNode() && !(n instanceof OutputNode)) {
                    OutputNode node = new OutputNode();
                    String name = "output_layer_" + output_index;
                    while (identifier.contains(name)) {
                        output_index++;
                        name = "output_layer_" + output_index;
                    }
                    output_index++;

                    node.setIdentifier(name);
                    node.addPreviousNode(n);

                    nodes_to_add.add(node);

                    outputs.add(node);
                    nodes.put(name, node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        order.addAll(nodes_to_add);
    }

    public void addNode(Node n, String... arguments) {
        try {
            if (n.getIdentifier() == null) {
                throw new ArgumentException("Layer has no identifier");
            }
            addNode(n.getIdentifier(), n, arguments);
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
    }

    public void addNode(String name, Node n, String... arguments) {
        try {
            if (n.hasMaxPrevNodes() && arguments.length > 1 ||
                    (n instanceof InputNode && arguments.length > 0)) {
                throw new ArgumentException("To many arguments given");
            }
            for (String s : arguments) {
                if (nodes.keySet().contains(s) == false) {
                    throw new ArgumentException("Node name: [" + s + "] unknown");
                }
            }
            n.setIdentifier(name);
            for (String s : arguments) {
                n.addPreviousNode(nodes.get(s));
            }
            if (n instanceof InputNode) {
                inputs.add((InputNode) n);
            } else if (n instanceof OutputNode) {
                outputs.add((OutputNode) n);
            }
            nodes.put(n.getIdentifier(), n);
            order.add(n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print_overview() {
        this.generate_output_dimensions();
        System.out.println("########################################################" +
                "#########################################################");
        for (Node l : order) {
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

            System.out.format("%-30s %-20s %-40s %-50s \n",
                    l.getClass().getSimpleName() + "[" + l.getIdentifier() + "]",
                    "[" + l.getOutputDepth() + " " + l.getOutputWidth() + " " + l.getOutputHeight() + "]",
                    prev,
                    next);
            System.out.println("-------------------------------------------------------------" +
                    "----------------------------------------------------");
        }
        System.out.println("########################################################" +
                "#########################################################");
    }

    public static void main(String[] args) throws InterruptedException {

//        Builder builder = new Builder();
//        InputNode inputNode = new InputNode(1,28,28);
//
//        builder.addNode("input_node_1", inputNode);
//        builder.addNode("conv_1_1", new ConvolutionNode(4, 5, 1, 2), "input_node_1");
//        builder.addNode("conv_1_2", new ConvolutionNode(4, 3, 1, 1), "conv_1_1");
//        builder.addNode("conv_1_3", new ConvolutionNode(4, 3, 1, 1), "conv_1_2");
//        builder.addNode("flatten", new FlattenNode(), "conv_1_3");
//        builder.addNode("dense_1", new DenseNode(10), "flatten");
//        builder.addNode("output_content", new OutputNode(),"dense_1");
//
//        Network net = builder.build_network();
        TrainSet set = TrainSet.fromMnist("res/train-images.idx3-ubyte", "res/train-labels.idx1-ubyte", 100,5000);
//
//
//        for(int n = 0; n < 100; n++){
//            double e = 0;
//            for(int i = 0; i < set.size(); i++){
//                e += net.train(set.getInput(i), set.getOutput(i),0.001);
//            }
//            System.out.println(e / set.size());
//        }
//        net.write("res/mnist_conv_small.net");

        Network net = Network.load("res/mnist_conv_small.net");

        double hits = 0;
        for(int i = 0; i < set.size(); i++){
            Tensor3D out = net.calculate(set.getInput(i))[0];
            double trueIndex = 0;
            double index = 0;
            double val = 0;
            for(int x = 0; x < 10; x++){
                if(out.get(0,0,x) > val){
                    val = out.get(0,0,x);
                    index = x;
                }
                if(set.getOutput(i).get(0,0,x) > 0.5){
                    trueIndex = x;
                }
            }
            if(trueIndex == index){
                hits++;
            }
            System.out.println(100 * hits / (i+1) + "%");
        }


    }
}
