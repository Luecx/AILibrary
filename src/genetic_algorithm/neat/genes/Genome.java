package luecx.ai.genetic_algorithm.neat.genes;


import luecx.ai.genetic_algorithm.neat.neat.Neat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Genome {

    private Neat neat;

    private HashMap<Integer, NodeGene> node_genes = new HashMap();
    private HashMap<Integer, ConnectionGene> connection_genes = new HashMap();
    private HashMap<Long, ConnectionGene> hashed_connections = new HashMap<>();


    public Genome(Neat neat){
        this.neat = neat;
        this.reset();
    }

    public void reset(){
        node_genes.clear();
        connection_genes.clear();
        hashed_connections.clear();

        for(int i = 0; i < neat.getInput_size() + neat.getOutput_size(); i++){
            put_node(neat.get_node_gene(i));
        }
    }


    public void mutate(){
        mutate_enable_disable_connections();
        mutate_weights();

        if(Math.random() < neat.getCONNECTION_MUTATION_RATE()) mutate_connection();
        if(Math.random() < neat.getNODE_MUTATION_RATE()) mutate_node();

    }

    public void mutate_weights(){
        for(int id:connection_genes.keySet()){
            if(Math.random() < neat.getWEIGHT_MUTATION_RATE()){
                if(Math.random() < neat.getWEIGHT_TOTALLY_RANDOM()){
                    connection_genes.get(id).setWeight(Math.random() * neat.getWEIGHT_MUTATION_STRENGTH() - neat.getWEIGHT_MUTATION_STRENGTH() / 2);
                }else{
                    connection_genes.get(id).setWeight(
                            connection_genes.get(id).getWeight() *
                            (Math.random() * neat.getWEIGHT_MUTATION_STRENGTH() - neat.getWEIGHT_MUTATION_STRENGTH() / 2));
                }
            }
        }
    }

    public void mutate_enable_disable_connections(){
        for(int id:connection_genes.keySet()){
            if(Math.random() < neat.getCONNECTION_ENABLE_DISABLE_RATE()){
                connection_genes.get(id).setEnabled(!connection_genes.get(id).isEnabled());
            }
        }
    }

    public void mutate_connection(){
        int from_index = 0;
        int to_index = 0;
        int c = 0;

        Set<Integer> ids = node_genes.keySet();
        int max = 0;
        for(int i:ids){
            if(i >= max) max = i + 1;
        }

        while(c < 20){


            from_index = (int)(Math.random() * max);
            to_index = (int)(Math.random() * max);
            c++;

            if(from_index == to_index) continue;
            if(hashed_connections.containsKey(Neat.hash(from_index, to_index))) continue;
            if(node_genes.containsKey(from_index) == false || node_genes.containsKey(to_index) == false) continue;
            if(node_genes.get(from_index).getType() == 1 || node_genes.get(to_index).getType() == -1) continue;
            if(node_genes.get(from_index).getPos_x() >= node_genes.get(to_index).getPos_x()) continue;

            break;
        }


        if(c >= 20) return;

        NodeGene nodeA = node_genes.get(from_index);
        NodeGene nodeB = node_genes.get(to_index);

        put_connection(neat.get_connection_gene(-1, nodeA.getInnovation_number(), nodeB.getInnovation_number(),
                Math.random() * neat.getWEIGHT_MUTATION_STRENGTH() - 0.5 * neat.getWEIGHT_MUTATION_STRENGTH()));

    }

    public void mutate_node(){
        if(connection_genes.size() == 0) return;

        int index = (int)(Math.random() * connection_genes.size());
        ConnectionGene gene= null;
        int c = 0;
        for(Integer i:connection_genes.keySet()){
            if(c == index){
                gene = connection_genes.get(i);
                break;
            }
            c++;
        }

        if(gene == null) return;

        NodeGene center = neat.get_node_gene(
                -1,
                (gene.getFrom().getPos_x() + gene.getTo().getPos_x()) / 2,
                (gene.getFrom().getPos_y() + gene.getTo().getPos_y()) / 2 + Math.random() * 0.3 - 0.15);

        ConnectionGene newLinkA = neat.get_connection_gene(-1, gene.getFrom().getInnovation_number(), center.getInnovation_number());
        newLinkA.setWeight(1);
        ConnectionGene newLinkB = neat.get_connection_gene(-1, center.getInnovation_number(), gene.getTo().getInnovation_number());
        newLinkB.setWeight(gene.getWeight());

        connection_genes.remove(gene.getInnovation_number());

        put_node(center);
        put_connection(newLinkA);
        put_connection(newLinkB);
    }


    public void crossover(Genome parent_fit, Genome parent_lessfit){

        reset();

        Set<Integer> parentA_IDs = parent_fit.connection_genes.keySet();
        Set<Integer> parentB_IDs = parent_lessfit.connection_genes.keySet();

        int maxID = 0;
        for(Integer id:parentA_IDs){
            if(maxID < id) maxID = id;

            if(parentB_IDs.contains(id)) {
                ConnectionGene gene = null;
                if (Math.random() < 0.5) {
                    gene = parent_fit.connection_genes.get(id).copy();
                } else {
                    gene = parent_lessfit.connection_genes.get(id).copy();
                }
                put_connection(gene);
            }
            else{
                put_connection(parent_fit.connection_genes.get(id).copy());
            }
        }

        for(Integer id:parentB_IDs){

            if(id > maxID) continue;
            if(parentA_IDs.contains(id)) continue;
            put_connection(parent_lessfit.connection_genes.get(id).copy());
        }

        for(int i = 0; i < neat.getInput_size(); i++){
            put_node(neat.get_node_gene(i));
        }
        for(int i = neat.getInput_size(); i < neat.getInput_size() + neat.getOutput_size(); i++){
            put_node(neat.get_node_gene(i));
        }
        for(Integer i:connection_genes.keySet()){
            this.put_node(connection_genes.get(i).getFrom());
            this.put_node(connection_genes.get(i).getTo());

            if(Math.random() < neat.getCONNECTION_ENABLE_DISABLE_RESET_RATE() && connection_genes.get(i).isEnabled() == false){
                connection_genes.get(i).setEnabled(true);
            }
        }
    }

    public double distance(Genome other){

        TreeSet<Integer> bigger_ids;
        TreeSet<Integer> smaller_ids;

        Set<Integer> this_set = this.connection_genes.keySet();
        Set<Integer> other_set = other.connection_genes.keySet();

        if((other_set.isEmpty() ? 0 : Collections.max(other_set)) >
                (this_set.isEmpty() ? 0 : Collections.max(this_set))){
            bigger_ids = new TreeSet<>(other_set);
            smaller_ids = new TreeSet<>(this_set);
        }else{
            bigger_ids = new TreeSet<>(this_set);
            smaller_ids = new TreeSet<>(other_set);
        }

        int excess = 0;
        int disjoint = 0;
        int similar = 0;
        double similar_dist=0;

        int max_id_small = 0;


        for(Integer id:smaller_ids){
            if(max_id_small < id) max_id_small = id;

            if(bigger_ids.contains(id)){
                similar++;
                similar_dist += Math.abs(connection_genes.get(id).getWeight() - other.connection_genes.get(id).getWeight());
            }else{
                disjoint++;
            }
            bigger_ids.remove(id);

        }

        for(Integer id:bigger_ids){
            if(id > max_id_small){
                excess ++;
            }else{
                disjoint ++;
            }
        }

        double N = Math.max(this_set.size(), other_set.size()) < 20 ? 1:Math.max(this_set.size(), other_set.size());
        //double N = Math.max(this_set.size(), other_set.size());

        return neat.getC1() * excess / N + neat.getC2() * disjoint / N + neat.getC3() * similar_dist / N;
    }

    public void print(){
        Neat.printGenes(node_genes, connection_genes);
    }

    private void put_node(NodeGene g){
        node_genes.put(g.getInnovation_number(), g);
    }

    private void put_connection(ConnectionGene g){
        connection_genes.put(g.getInnovation_number(), g);
        hashed_connections.put(neat.hash(g), g);
    }



    public Neat getNeat() {
        return neat;
    }

    public HashMap<Integer, NodeGene> getNode_genes() {
        return node_genes;
    }

    public HashMap<Integer, ConnectionGene> getConnection_genes() {
        return connection_genes;
    }
}
