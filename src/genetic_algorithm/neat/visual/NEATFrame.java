package luecx.ai.genetic_algorithm.neat.visual;


import luecx.ai.genetic_algorithm.neat.genes.Genome;
import luecx.ai.genetic_algorithm.neat.neat.Client;
import luecx.ai.genetic_algorithm.neat.neat.Neat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class NEATFrame extends JFrame {

    private NEATPanel panel;
    private Client genome;

    public NEATFrame(Genome client) {
        this();
        this.genome = genome;
        this.repaint();
    }

    public void setNetwork(Client genome){
        panel.setGenome(genome);
        this.genome = genome;
    }

    public NEATFrame() throws HeadlessException {
        this.setDefaultCloseOperation(3);

        this.setTitle("NEAT");
        this.setMinimumSize(new Dimension(1000,700));
        this.setPreferredSize(new Dimension(1000,700));

        this.setLayout(new BorderLayout());


        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(1000,100));
        menu.setLayout(new GridLayout(1,5));

        JButton buttonB = new JButton("Point mutate-B");
        buttonB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutate_weights();
                repaint();
            }
        });
        menu.add(buttonB);

        JButton buttonC = new JButton("Link mutate");
        buttonC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutate_connection(); repaint();
            }
        });
        menu.add(buttonC);

        JButton buttonD = new JButton("Node mutate");
        buttonD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutate_node(); repaint();
            }
        });
        menu.add(buttonD);

        JButton buttonE = new JButton("on/off");
        buttonE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutate_enable_disable_connections(); repaint();
            }
        });
        menu.add(buttonE);

        JButton buttonF = new JButton("Mutate");
        buttonF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.mutate();
                repaint();
                //System.out.println(Arrays.toString(network.calculate(1,1,1))); repaint();
            }
        });
        menu.add(buttonF);

        JButton buttonG = new JButton("Calculate");
        buttonG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                genome.generateCalculator();
                System.out.println(Arrays.toString(genome.calculate(1,1,1)));
                repaint();
                //System.out.println(Arrays.toString(network.calculate(1,1,1))); repaint();
            }
        });
        menu.add(buttonG);


        this.add(menu, BorderLayout.NORTH);

        this.panel = new NEATPanel();
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public static void main(String[] args){

        Neat neat = new Neat(3,3, 10);

        System.out.println(neat.getClient(0).getNode_genes());

        NEATFrame frame = new NEATFrame();
        frame.setNetwork(neat.getClient(0));
        frame.repaint();
    }
}
