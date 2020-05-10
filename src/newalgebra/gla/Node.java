package newalgebra.gla;

import newalgebra.cells.Cell;

import java.util.ArrayList;

public class Node {

    Cell cell;

    double xsize = 1;
    double ysize = 1;

    double posX;        //between 0 and 1
    double posY;

    private int rank;
    private int place;



    private ArrayList<Edge> edges = new ArrayList<>();

    public Node(Cell cell) {
        this.cell = cell;
    }

    public double borderXPosition(int index, int total){
        return posX - xsize/2 + xsize*(index+1)/(total+1);
    }

    public double topBorderY(){
        return posY - ysize/2;
    }

    public double bottomBorderY(){
        return posY + ysize/2;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Edge addNext(Node node){
        Edge e = new Edge(this,node);
        edges.add(e);
        return e;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }


    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public double getXsize() {
        return xsize;
    }

    public void setXsize(double xsize) {
        this.xsize = xsize;
    }

    public double getYsize() {
        return ysize;
    }

    public void setYsize(double ysize) {
        this.ysize = ysize;
    }
}
