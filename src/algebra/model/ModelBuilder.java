package algebra.model;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import algebra.nodes.basic.*;
import core.tensor.Tensor;
import neuralnetwork.builder.BuildException;

import java.util.ArrayList;
import java.util.Arrays;

public class ModelBuilder {


    private ArrayList<Variable> variables = new ArrayList<>();

    private ArrayList<Node> calculationOrder = new ArrayList<>();

    public ModelBuilder(){ }

    private Node lastNode = null;

    public void add(Node node, String... prevNodes){

        //get variables and all nodes
        ArrayList<Node> vars = new ArrayList<>();
        ArrayList<Node> roots = new ArrayList<>();
        ArrayList<Node> nodeList = getAllSubNodes(node, new ArrayList<>(),vars,roots);

        if(lastNode == null && vars.size() == 0){
            throw new BuildException(node, "define input node first");
        }

        if(prevNodes.length == 0 && vars.size() == 0 && roots.size() != 1){
            throw new BuildException(node, "cannot connect");
        }

        if(prevNodes.length > 0 && roots.size() != prevNodes.length && roots.size() > 1){
            throw new BuildException(node, "cannot connect");
        }



        // connecting first
        if(prevNodes.length == 0){
            for(Node root:roots){
                if(root instanceof Connector){
                    root.replaceWith(lastNode);
//                    Node actualRoot = (Node) root.getNextNodes().get(0);
//                    actualRoot.removePreviousNode(root);
//                    actualRoot.addPreviousNode(lastNode);
                }else{
                    root.addPreviousNode(lastNode);
                }
            }
        }else{

            //link 1 to 1
            if(roots.size() == prevNodes.length) {
                for (int i = 0; i < roots.size(); i++) {

                    Node root = roots.get(i);
                    if(root instanceof Connector){
                        root.replaceWith(lastNode);
//                        Node actualRoot = (Node) root.getNextNodes().get(0);
//                        actualRoot.removePreviousNode(root);
//                        actualRoot.addPreviousNode(lastNode);
                    }else{
                        if(!root.addPreviousNode(lastNode)){
                            throw new BuildException(node, "cannot connect");
                        }
                    }

                }
            }

            if(roots.size() == 1){
                for (int i = 0; i < prevNodes.length; i++) {
                    if (!roots.get(0).addPreviousNode(getNode(prevNodes[i]))) {
                        throw new BuildException(node, "cannot connect");
                    }
                }
            }


        }

        for(Node n:nodeList){
            if(n.getIdentifier() != null){
                if(nameExists(n.getIdentifier())){
                    throw new BuildException(n, "duplicate name!");
                }
            }else{
                n.setIdentifier(generateName(n));
            }

            if(!(n instanceof Connector))
                calculationOrder.add(n);

            if(n instanceof Variable){
                variables.add((Variable) n);
            }

        }

        lastNode = node;

    }

    public Model build() {

        ArrayList<Node> outputs = new ArrayList<>();
        for(Node node:calculationOrder){
            if(node.getNextNodes().size() == 0){
                outputs.add(node);
            }
            node.build();
        }


        Model model = new Model(variables, outputs, calculationOrder);
        return model;
    }

    private ArrayList<Node> getAllSubNodes(Node node, ArrayList<Node> ar, ArrayList<Node> variables, ArrayList<Node> roots){
        for(Object k:node.getPreviousNodes()){
            getAllSubNodes((Node)k, ar, variables, roots);
        }
        if(node instanceof Variable){
            variables.add(node);
        }else if(node.getPreviousNodes().size() == 0){
            roots.add(node);
        }
        ar.add(node);
        return ar;
    }

    private Node getNode(String name){
        for(Node n:calculationOrder){
            if(n.getIdentifier().equals(name)) return n;
        }
        return null;
    }

    private boolean nameExists(String name){
        for(Node n:calculationOrder){
            if(n.getIdentifier().equals(name)) return true;
        }
        return false;
    }

    private String generateName(Node node){
        int num = 0;

        do{
            num++;
        } while(nameExists(node.getClass().getSimpleName()+"_"+num));

        return node.getClass().getSimpleName()+"_"+num;
    }

    @Override
    public String toString() {
        return lastNode.toString();
    }

    public static void main(String[] args) {

        Variable var = new Variable("x1", new Dimension(1));
        Variable var2 = new Variable("target", new Dimension(1));

        ModelBuilder builder = new ModelBuilder();
        builder.add(new Hadamard(var,var));

        builder.add(
                new Pow(
                        new Add(
                                new Connector(),
                                new Negate(var2)),
                        2));




        System.out.println(builder);

        Model model = builder.build();


        Tensor t_in = new Tensor(new double[]{1},1,1,1);

        Tensor t_target = new Tensor(new double[]{2},1,1,1);


        Tensor t_seed = new Tensor(new double[]{1},1,1,1);


        for(int i = 0; i < 100; i++){
            System.out.println(Arrays.toString(model.calc(t_in, t_target)));
            model.autoDiff(t_seed);
            t_in.self_sub(model.getInputGradients()[0].scale(0.1));
        }



    }
}
