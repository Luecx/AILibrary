package luecx.ai.genetic_algorithm.neat.calculation;


import luecx.ai.genetic_algorithm.neat.genes.ConnectionGene;
import luecx.ai.genetic_algorithm.neat.genes.NodeGene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Calculator {

    private ArrayList<Node> input_nodes = new ArrayList<>();
    private ArrayList<Node> output_nodes = new ArrayList<>();
    private ArrayList<Node> hidden_nodes = new ArrayList<>();


    public Calculator(HashMap<Integer, NodeGene> nodes, HashMap<Integer, ConnectionGene> connections){

        HashMap<Integer, Node> nodeHashMap = new HashMap<>();

        for(int i:nodes.keySet()){
            nodeHashMap.put(i, new Node(nodes.get(i).getPos_x()));
        }


        for(int i:connections.keySet()){
            Connection con = new Connection(
                    nodeHashMap.get(connections.get(i).getFrom().getInnovation_number()),
                    nodeHashMap.get(connections.get(i).getTo().getInnovation_number())
            );
            con.setWeight(connections.get(i).getWeight());
            con.setActivated(connections.get(i).isEnabled());
            nodeHashMap.get(connections.get(i).getTo().getInnovation_number()).getConnections().add(con);
        }

        NodeGene gene;
        for(int i:nodeHashMap.keySet()){

            gene = nodes.get(i);
            if(gene.getType() != 0){
                if(gene.getType() == -1){
                    input_nodes.add(nodeHashMap.get(i));
                    input_nodes.get(input_nodes.size() - 1).setDraw_x(- 1d / (i + 1));
                }else{
                    output_nodes.add(nodeHashMap.get(i));
                    output_nodes.get(output_nodes.size() - 1).setDraw_x(- 1d / (i + 1));
                }
            }else{
                hidden_nodes.add(nodeHashMap.get(i));
            }
        }

        input_nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.compareTo(o1);
            }
        });output_nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.compareTo(o1);
            }
        });hidden_nodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o2.compareTo(o1);
            }
        });

    }

    public double[] calculate(double... in){
        for(int i = 0; i < input_nodes.size(); i++){
            input_nodes.get(i).setOutput(in[i]);
            input_nodes.get(i).setFinished(true);
        }
        for(Node n:hidden_nodes){
            n.setFinished(false);
            n.setOutput(0);
        }

        ArrayList<Node> todo = new ArrayList<>();
        todo.addAll(hidden_nodes);
        while(todo.size() > 0){
            for(int i = 0; i < todo.size(); i++){
                if(todo.get(i).calculate()){
                    todo.remove(i);
                }
            }
        }

        double[] out = new double[output_nodes.size()];
        for(int i = 0; i < out.length; i++){
            output_nodes.get(i).calculate();
            out[i]= output_nodes.get(i).getOutput();
        }
        return out;
    }

}
