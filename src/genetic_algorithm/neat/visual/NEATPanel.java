package luecx.ai.genetic_algorithm.neat.visual;


import luecx.ai.genetic_algorithm.neat.genes.ConnectionGene;
import luecx.ai.genetic_algorithm.neat.genes.Genome;
import luecx.ai.genetic_algorithm.neat.genes.NodeGene;

import javax.swing.*;
import java.awt.*;

public class NEATPanel extends JPanel {

    private Genome genome;

    public NEATPanel() {
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0,0,10000,10000);
        g.setColor(Color.black);
        g.fillRect(0,0,10000,10000);

        for(Integer i: getGenome().getNode_genes().keySet()){
            paintNode(getGenome().getNode_genes().get(i), (Graphics2D) g);
        }

        for(Integer i: getGenome().getConnection_genes().keySet()){
            paintConnection(getGenome().getConnection_genes().get(i), (Graphics2D) g);
        }


    }

    private void paintNode(NodeGene n, Graphics2D g){
        g.setColor(Color.gray);
        g.setStroke(new BasicStroke(3));
        g.drawOval((int)(this.getWidth() * n.getPos_x()) - 10,
                (int)(this.getHeight() * n.getPos_y()) - 10,20,20);
    }

    private void paintConnection(ConnectionGene c, Graphics2D g){
        g.setColor(c.isEnabled() ? Color.green:Color.red);
        g.setStroke(new BasicStroke(3));
        g.drawString(new String(c.getWeight() + "       ").substring(0,7),
                (int)((c.getTo().getPos_x() + c.getFrom().getPos_x())* 0.5 * this.getWidth()),
                (int)((c.getTo().getPos_y() + c.getFrom().getPos_y())* 0.5 * this.getHeight()) +15);
        g.drawLine(
                (int)(this.getWidth() * c.getFrom().getPos_x()),
                (int)(this.getHeight() * c.getFrom().getPos_y()),
                (int)(this.getWidth() * c.getTo().getPos_x()),
                (int)(this.getHeight() * c.getTo().getPos_y()));
    }
}
