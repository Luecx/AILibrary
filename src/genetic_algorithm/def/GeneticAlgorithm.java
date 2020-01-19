package genetic_algorithm.def;


import core.tensor.Tensor;
import neuralnetwork.network.DenseNode;
import neuralnetwork.nodes.Node;

import java.util.*;
import java.util.List;

public class GeneticAlgorithm {

    public double MUTATION_RATE = 0.1;
    public double MUTATION_STENGTH = 0.1;
    public double AMOUNT_SURVIVORS = 1;

    public <T extends GeneticClient> void evolve(List<T> clients) {
        clients.sort(new Comparator<GeneticClient>() {
            @Override
            public int compare(GeneticClient o1, GeneticClient o2) {
                if (o1.getScore() > o2.getScore()) return -1;
                if (o2.getScore() > o1.getScore()) return 1;
                return 0;
            }
        });
        crossover(
                clients.subList((int)Math.min(AMOUNT_SURVIVORS, clients.size()), clients.size()),
                clients.subList(0,(int)Math.min(AMOUNT_SURVIVORS, clients.size())));
        mutate(clients);



    }

    public static <T extends GeneticClient> void printClients(List<T> clients) {
        int index = 0;
        for (T t : clients) {
            index++;
            System.out.format("index %3s     score %-20s alive: %-10s \n", index, t.getScore(), t.getNetwork());
        }
    }

    protected <T extends GeneticClient> void crossover(List<T> clients, List<T> selection) {
        double parentSum = 0;
        for (GeneticClient c : selection) {
            parentSum += c.getScore();
        }
        for (GeneticClient g : clients) {
            double r = Math.random();
            double c = 0;
            GeneticClient parent = null;
            for (GeneticClient sel : selection) {
                c += (sel.getScore() + 0.0001) / (parentSum + 0.0001);
                if (r < c) {
                    parent = sel;
                    break;
                }
            }

            for(int i = 0; i < g.getNetwork().getNodes().length; i++){
                Node left = g.getNetwork().getNodes()[i];
                Node right = parent.getNetwork().getNodes()[i];

                if(left instanceof DenseNode && right instanceof DenseNode && left.equals(right)){

                    ((DenseNode) left).setWeights(((DenseNode) right).getWeights().copy());
                    ((DenseNode) left).setBias(((DenseNode) right).getBias().copy());
                }

            }

        }
    }

    protected <T extends GeneticClient> void mutate(List<T> clients) {
        for (GeneticClient g : clients) {
            for (Node n : g.getNetwork().getNodes()) {
                if (n instanceof DenseNode) {
                    mutateArray(((DenseNode) n).getWeights(), MUTATION_RATE, MUTATION_STENGTH);
                    mutateArray(((DenseNode) n).getBias(), MUTATION_RATE, MUTATION_STENGTH);
                }
            }
        }
    }


    public static void mutateArray(Tensor vals, double rate, double strength) {
        Random random = new Random();
        for (int i = 0; i < vals.size(); i++) {
            if (Math.random() < rate) {
                vals.getData()[i] += random.nextGaussian() * strength;
            }
        }
    }

}
