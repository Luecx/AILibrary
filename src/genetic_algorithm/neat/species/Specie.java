package luecx.ai.genetic_algorithm.neat.species;


import luecx.ai.genetic_algorithm.neat.neat.Client;

import java.util.ArrayList;

public class Specie {


    private Client representative;
    private ArrayList<Client> clients = new ArrayList<>();
    private double fitness = 0;

    public Specie(Client representative) {
        this.representative = representative;
        this.clients.add(representative);
    }

    public boolean place(Client c){
        if(c.distance(representative) < c.getNeat().getCP()){
            clients.add(c);
            return true;
        }
        return false;
    }

    public void reset(){
        //int newRepID = (int)(Math.random() * clients.size());

        //representative = clients.get(newRepID);

        clients.clear();
        this.clients.add(representative);
    }

    public void calculate_adjusted_fitness(){
        double f = 0;
        for(Client c:clients){
            f += c.getScore();
        }
        fitness = f/ clients.size();
    }

    public Client getRepresentative() {
        return representative;
    }

    public void setRepresentative(Client representative) {
        this.representative = representative;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public int size(){
        return clients.size();
    }
}
