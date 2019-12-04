package luecx.ai.genetic_algorithm.neat.neat;


import luecx.ai.genetic_algorithm.neat.genes.ConnectionGene;
import luecx.ai.genetic_algorithm.neat.genes.NodeGene;
import luecx.ai.genetic_algorithm.neat.species.Specie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Neat {


    private HashMap<Integer, NodeGene> node_genes = new HashMap();
    private HashMap<Integer, ConnectionGene> connection_genes = new HashMap();
    private HashMap<Integer, ConnectionGene> connection_genes_hashed = new HashMap();

    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Specie> species = new ArrayList<>();

    private int max_genomes;
    private int input_size;
    private int output_size;


    private double NODE_MUTATION_RATE = 0.04;
    private double CONNECTION_MUTATION_RATE = 0.2;
    private double CONNECTION_ENABLE_DISABLE_RATE = 0.05;
    private double CONNECTION_ENABLE_DISABLE_RESET_RATE = 0.25;

    private double WEIGHT_MUTATION_RATE = 0.1;
    private double WEIGHT_MUTATION_STRENGTH = 3;
    private double WEIGHT_TOTALLY_RANDOM = 0.02;

    private double SURVIVAL_PERCENTAGE = 0.3;

    private double C1 = 1;
    private double C2 = 1;
    private double C3 = 0.3;
    private double CP = 8;


    public Neat(int input, int output, int clients){
        this.input_size = input;
        this.output_size = output;
        this.max_genomes = clients;
        this.reset();
    }

    public void reset(){
        node_genes.clear();
        connection_genes.clear();
        connection_genes_hashed.clear();
        clients.clear();


        for(int i = 0; i < input_size; i++){
            get_node_gene(-1, 0.1, (double)i / input_size + 1d / (2 * input_size)).setType(-1);
        }
        for(int i = 0; i < output_size; i++){
            get_node_gene(-1, 0.9, (double)i / output_size + 1d / (2 * output_size)).setType(1);
        }
        for(int i = 0; i < max_genomes; i++){
            clients.add(new Client(this));
        }

        for(Client c:clients){
            c.generateCalculator();
        }

    }

    public NodeGene get_node_gene(int id, double... args){
        if(node_genes.containsKey(id)){
            return node_genes.get(id);
        }
        NodeGene gene;
        if(args.length == 2){
            gene = new NodeGene(next_innovation_number(), args[0], args[1]);
        }else{
            gene = new NodeGene(next_innovation_number());
            if(args.length > 2){
                gene.setType((int) args[2]);
            }
        }
        node_genes.put(gene.getInnovation_number(), gene);
        return gene;
    }

    public ConnectionGene get_connection_gene(int id, double... args){

        if(args.length >=  2){
            for(Integer i:connection_genes.keySet()){
                if(connection_genes.get(i).getFrom() == node_genes.get((int)args[0]) &&
                        connection_genes.get(i).getTo() == node_genes.get((int)args[1])){
                    return connection_genes.get(i);
                }
            }
        }

        if(connection_genes.containsKey(id)){
            return connection_genes.get(id);
        }
        ConnectionGene gene = new ConnectionGene(next_innovation_number());

        switch (args.length){

            case 4:
                gene.setEnabled(args[3] > 0.5);
            case 3:
                gene.setWeight(args[2]);
            case 2:
                gene.setFrom(get_node_gene((int) args[0]));
                gene.setTo(get_node_gene((int) args[1]));
                break;
            case 0: return null;
        }
        if(connection_genes_hashed.containsKey(hash(gene))){
            return connection_genes_hashed.get(hash(gene));
        }

        connection_genes.put(gene.getInnovation_number(), gene);
        return gene;
    }

    public void print(){
        printGenes(node_genes, connection_genes);
    }

    public void evolve(){

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double highest_score = -100000000;
        ArrayList<Client> killed = new ArrayList<>();

        for(Specie s:species){
            s.reset();
        }

        for(Client g: clients){
            boolean found = false;

            for(int s = 0; s < species.size(); s++){
                if(species.get(s).place(g)) {
                    found = true;
                    break;
                }
            }
            if(!found){
                species.add(new Specie(g));
            }
        }

        System.err.println("SPECIES---------------------------------------------------------");
        for(Specie s:species){
            System.err.println(s.getFitness() + "   " + s.getClients().size() + "   " + s.getRepresentative());
        }
        System.err.println("SPECIES---------------------------------------------------------");

        for(Specie e:species){
            e.calculate_adjusted_fitness();
            System.out.print(e.getClients().size() + "  ");
        }

        clients.sort(this.comparator);

        for(int i = this.species.size() - 1; i >= 0; i--){
            ArrayList<Client> clients = species.get(i).getClients();
            clients.sort(comparator);

            System.out.print(species.get(i).getClients().size()+ "   ");

            ArrayList<Client> to_be_removed = new ArrayList<>
                    (clients.subList((int)((species.get(i).size()) * SURVIVAL_PERCENTAGE), species.get(i).size()));
            killed.addAll(to_be_removed);
            clients.removeAll(to_be_removed);
            System.out.println(species.get(i).getClients().size()+ "   ");

            if(clients.size() < 1){
                this.species.remove(i);
            }
        }


        for(Client c:killed){
            Specie s = chooseSpeciesBasedOnScore();

            Client a = chooseClientBasedOnScore(s);
            Client b = chooseClientBasedOnScore(s);

            if(a.getScore() > b.getScore()){
                c.crossover(a,b);
            }else{
                c.crossover(b,a);
            }

            s.getClients().add(c);
        }

        for(Client c:clients){
            c.mutate();
            c.generateCalculator();
        }


        System.out.println("SPECIES---------------------------------------------------------");
        for(Specie s:species){
            System.out.println(s.getFitness() + "   " + s.getClients().size() + "   " + s.getRepresentative());
        }
        System.out.println("SPECIES---------------------------------------------------------");


    }

    private Specie chooseSpeciesBasedOnScore(){
        double total_score = 0;
        for(Specie c:species){
            total_score += c.getFitness();
        }
        double rand = Math.random() * total_score;
        double counter = 0;
        for(Specie c:species){
            counter += c.getFitness();
            if(counter >= rand) return c;
        }
        return null;
    }

    private Client chooseClientBasedOnScore(Specie s){
        double total_score = 0;

        for(Client c:s.getClients()){
            total_score += c.getScore();
        }
        double rand = Math.random() * total_score;
        double counter = 0;
        for(Client c:s.getClients()){
            counter += c.getScore();
            if(counter >= rand) return c;
        }
        return null;
    }

    public static void main(String[] args){

        Neat n = new Neat(3,3, 100);


        for(Client c:n.clients){
            for(int i = 0; i< 15; i++) c.mutate();
            c.setScore(Math.random());
        }

        for(int i = 0; i < 1; i++)
            n.evolve();

        n.getClient(0).print();

    }

    public static void printGenes(HashMap<Integer, NodeGene> nodes, HashMap<Integer, ConnectionGene> connection_genes) {
        System.out.println("");
        System.out.print("Nodes: [");
        for (Integer i : nodes.keySet()) {
            System.out.print(i + ", ");
        }
        System.out.println("]");
        System.out.print("Connections: [");
        for (Integer i : connection_genes.keySet()) {
            System.out.print("{" + connection_genes.get(i).getInnovation_number() + "  " +
                    connection_genes.get(i).getFrom().getInnovation_number() + "->" +
                    connection_genes.get(i).getTo().getInnovation_number() + "}" + ", ");
        }
        System.out.println("]");
    }

    public static long hash(ConnectionGene g){
        return (long)(g.getFrom().getInnovation_number() * 1000000000 + g.getTo().getInnovation_number());
    }

    public static long hash(int idA, int idB){
        return (long)(idA * 1000000000 + idB);
    }


    private Comparator<Client> comparator = new Comparator<Client>() {
        @Override
        public int compare(Client o1, Client o2) {
            if(o1.getScore() > o2.getScore()) return -1;
            if(o1.getScore() < o2.getScore()) return 1;
            return 0;
        }
    };
    private int innovation_counter = 0;
    private int next_innovation_number() {
        return innovation_counter ++;
    }

    public Client getClient(int index){
        return this.clients.get(index);
    }

    public int getMax_genomes() {
        return max_genomes;
    }

    public int getInput_size() {
        return input_size;
    }

    public int getOutput_size() {
        return output_size;
    }

    public int getInnovation_counter() {
        return innovation_counter;
    }

    public double getC1() {
        return C1;
    }

    public void setC1(double c1) {
        this.C1 = c1;
    }

    public double getC2() {
        return C2;
    }

    public void setC2(double c2) {
        this.C2 = c2;
    }

    public double getC3() {
        return C3;
    }

    public void setC3(double c3) {
        this.C3 = c3;
    }

    public double getCP() {
        return CP;
    }

    public void setCP(double CP) {
        this.CP = CP;
    }

    public double getNODE_MUTATION_RATE() {
        return NODE_MUTATION_RATE;
    }

    public void setNODE_MUTATION_RATE(double NODE_MUTATION_RATE) {
        this.NODE_MUTATION_RATE = NODE_MUTATION_RATE;
    }

    public double getCONNECTION_MUTATION_RATE() {
        return CONNECTION_MUTATION_RATE;
    }

    public void setCONNECTION_MUTATION_RATE(double CONNECTION_MUTATION_RATE) {
        this.CONNECTION_MUTATION_RATE = CONNECTION_MUTATION_RATE;
    }

    public double getWEIGHT_MUTATION_RATE() {
        return WEIGHT_MUTATION_RATE;
    }

    public void setWEIGHT_MUTATION_RATE(double WEIGHT_MUTATION_RATE) {
        this.WEIGHT_MUTATION_RATE = WEIGHT_MUTATION_RATE;
    }

    public double getWEIGHT_MUTATION_STRENGTH() {
        return WEIGHT_MUTATION_STRENGTH;
    }

    public void setWEIGHT_MUTATION_STRENGTH(double WEIGHT_MUTATION_STRENGTH) {
        this.WEIGHT_MUTATION_STRENGTH = WEIGHT_MUTATION_STRENGTH;
    }

    public double getWEIGHT_TOTALLY_RANDOM() {
        return WEIGHT_TOTALLY_RANDOM;
    }

    public void setWEIGHT_TOTALLY_RANDOM(double WEIGHT_TOTALLY_RANDOM) {
        this.WEIGHT_TOTALLY_RANDOM = WEIGHT_TOTALLY_RANDOM;
    }

    public double getCONNECTION_ENABLE_DISABLE_RATE() {
        return CONNECTION_ENABLE_DISABLE_RATE;
    }

    public void setCONNECTION_ENABLE_DISABLE_RATE(double CONNECTION_ENABLE_DISABLE_RATE) {
        this.CONNECTION_ENABLE_DISABLE_RATE = CONNECTION_ENABLE_DISABLE_RATE;
    }

    public double getCONNECTION_ENABLE_DISABLE_RESET_RATE() {
        return CONNECTION_ENABLE_DISABLE_RESET_RATE;
    }

    public void setCONNECTION_ENABLE_DISABLE_RESET_RATE(double CONNECTION_ENABLE_DISABLE_RESET_RATE) {
        this.CONNECTION_ENABLE_DISABLE_RESET_RATE = CONNECTION_ENABLE_DISABLE_RESET_RATE;
    }
}
