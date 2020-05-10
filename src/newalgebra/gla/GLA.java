package newalgebra.gla;


import core.vector.Vector2d;
import newalgebra.cells.Cell;
import newalgebra.cells.Output;
import newalgebra.cells.Variable;
import newalgebra.network.nodes.recurrent.LSTM;
import visuals.Panel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * resource: https://www.graphviz.org/Documentation/TSE93.pdf
 */
public class GLA {


    int pixelWidth = 3000;
    int pixelHeight = 3000;

    int fontSize = 20;



    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private int placeCounter[];
    private int maxRank;




    private void buildInitialTree(Cell cell, boolean recursive){

        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        HashMap<Cell, Node> map = new HashMap<>();

        List<Cell> comp;
        if(recursive){
            comp = cell.listAllChildsDeep();
            for(int i = comp.size()-1; i >= 0; i--){
                if(!comp.get(i).getComputationOrder().isEmpty()){
                    comp.remove(i);
                }
            }

        }else{
            comp = cell.getComputationOrder();
        }

        for(Cell c:comp){
            Node n = new Node(c);
            map.put(c, n);
            nodes.add(n);
        }

        Collections.reverse(comp);
        for(Cell c:comp){
            Node nd = map.get(c);
            for(int i = 0; i < c.inputCount(); i++){
                Output p = c.getInput(i).getOutput();
                if(p != null){
                    for(Cell k:comp){
                        for(int o = 0; o < k.outputCount(); o++){
                            if(k.getOutput(o) == p){
                                Node prev = map.get(k);
                                edges.add(prev.addNext(nd));
                            }
                        }
                    }
                }
            }
        }

        placeCounter = new int[comp.size()+1];

        maxRank = 0;
        for(Cell c:comp){
            Node n = map.get(c);
            int max = 0;
            for(Edge e:n.getEdges()){
                max = Math.max(max, e.getTo().getRank());
            }
            n.setRank(max+1);
            maxRank = Math.max(maxRank, n.getRank());
            n.setPlace(placeCounter[max+1]);
            placeCounter[max+1] ++;
        }





    }

    public void render(Cell cell, boolean recursive, int resolution){

    }

    public void drawGraph(Graphics2D graphics2D, Cell cell, boolean recursive){
        buildInitialTree(cell,recursive);
        position();
        ordering();
        render(graphics2D);
    }


    private void ordering(){

    }
    private void position(){

        Rectangle2D.Double max = new Rectangle2D.Double();
        for(Node n:nodes){
            Rectangle2D textBounds = Panel.get_centered_string_bounds(n.getCell().getClass().getSimpleName(), new Font("Arial", Font.PLAIN, fontSize));
            max.width = Math.max(textBounds.getWidth(), max.width);
            max.height = Math.max(textBounds.getHeight(), max.height);
        }

        for(Node n:nodes){
            n.setXsize(max.getWidth() / pixelWidth);
            n.setYsize(max.getHeight() / pixelHeight);

            n.setPosX(0.1 + 0.8 * ((double)(n.getPlace()+1) / (placeCounter[n.getRank()]+1)));
            System.out.println();
            n.setPosY(0.1+ 0.8*(n.getRank()+1d) / (maxRank+1));
        }






    }



    private void render(Graphics2D graphics2D){
        graphics2D.clearRect(0,0,pixelWidth,pixelHeight);
        graphics2D.setColor(Color.white);
        graphics2D.fillRect(0,0,pixelWidth, pixelHeight);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.setColor(Color.CYAN);
        Rectangle2D max = new Rectangle2D.Double();



        renderArrows(graphics2D);



        for(Node n:nodes){
            Rectangle rectangle = new Rectangle(
                    (int)((n.getPosX()-n.getXsize()/2) * pixelWidth),
                    (int)((n.getPosY()-n.getYsize()/2) * pixelHeight),
                    (int)(n.getXsize() * pixelWidth),(int)(n.getYsize() * pixelHeight));
            graphics2D.clearRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);


            if(n.getCell() instanceof Variable){
                graphics2D.setColor(Color.orange);
            }else if(n.getCell().getConnectedInputs().size() == 0 || n.getCell().getConnectedOutputs().size() == 0){
                graphics2D.setColor(Color.yellow);
            }else{
                graphics2D.setColor(Color.cyan);
            }

            graphics2D.fillRect((int)rectangle.getX(), (int)rectangle.getY(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
            graphics2D.setColor(Color.black);
            graphics2D.drawRect((int)rectangle.getX(), (int)rectangle.getY(), (int)rectangle.getWidth(), (int)rectangle.getHeight());
            Panel.draw_centered_string(graphics2D, n.getCell().getClass().getSimpleName(), rectangle, new Font("Arial", Font.PLAIN, fontSize));

        }
    }



    private void renderArrows(Graphics2D graphics2D){
        for(Node n:nodes){
            for(Edge e:n.getEdges()){

                graphics2D.setColor(Color.black);


                Panel.draw_arrow(graphics2D,
                                 new Vector2d((n.getPosX() * pixelWidth), (n.getPosY() * pixelHeight)),
                                 new Vector2d(((e.getTo().getPosX()) * pixelWidth), ((e.getTo().getPosY()+e.getTo().getYsize()/2d) * pixelHeight)),
                                 pixelWidth/800,
                                 pixelWidth/200,
                                 pixelWidth/100
                );
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage img = new BufferedImage(3000,3000,1);
        Graphics2D g2d = img.createGraphics();
        LSTM lstm = new LSTM();
        GLA gla = new GLA();
        gla.drawGraph(g2d, lstm, true);
        ImageIO.write(img, "PNG", new File("test.png"));

    }


}
