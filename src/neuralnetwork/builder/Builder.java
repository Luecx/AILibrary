package neuralnetwork.builder;

import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.data.TrainSet;
import neuralnetwork.functions.None;
import neuralnetwork.functions.ReLU;
import neuralnetwork.functions.Sigmoid;
import neuralnetwork.network.*;
import neuralnetwork.network.special.GramNode;
import neuralnetwork.nodes.Node;
import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Builder {


    HashMap<String, Node> nodes = new HashMap<>();
    ArrayList<Node> order = new ArrayList<>();
    ArrayList<InputNode> inputs = new ArrayList<>();
    ArrayList<OutputNode> outputs = new ArrayList<>();

    private Node lastAddedNode = null;

    public Builder(int d, int w, int h) {
        InputNode in = new InputNode(d, w, h);
        this.addNode("input_node_1", in);
        this.lastAddedNode = in;
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
            } else {
                if(lastAddedNode != null){
                    n.addPreviousNode(lastAddedNode);
                }
                if (n instanceof OutputNode) {
                    outputs.add((OutputNode) n);
                }
            }
            nodes.put(n.getIdentifier(), n);
            order.add(n);
            lastAddedNode = n;
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
        Builder builder = new Builder();
        InputNode inputNode;
        ConvolutionNode deconvNode;
        builder.addNode("input", inputNode = new InputNode(1, 2, 2));
        builder.addNode("deconv3", deconvNode = new ConvolutionNode(1, 2, 1, 0));
        Network network = builder.build_network();
        network.print_overview();
        //network.print_timecheck();

        Tensor3D in = new Tensor3D(1,2,2);
        in.randomizeRegular(1,1);
        Tensor3D out = new Tensor3D(1,1,1);
        out.reset(2);

        deconvNode.getFilter().set(Math.random(),0,0,0,0);
        deconvNode.getFilter().set(Math.random(),0,0,1,0);
        deconvNode.getFilter().set(Math.random(),0,0,0,1);
        deconvNode.getFilter().set(Math.random(),0,0,1,1);
        deconvNode.setActivationFunction(new ReLU());

        System.out.println(network.calculate(in)[0]);

        System.out.println(deconvNode.getFilter());

        for(int i = 0; i < 200; i++){
            System.out.println(network.train(in,out,0.03));
        }

        System.out.println(deconvNode.getFilter());

//        network.write("test.net");
//        System.out.println(Network.load("test.net").calculate(in)[0]);
    }
}
