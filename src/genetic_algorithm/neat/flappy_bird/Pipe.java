package luecx.ai.genetic_algorithm.neat.flappy_bird;

import java.awt.*;

public class Pipe {

    private double x;
    private double top;
    private double bot;
    private double width;

    public Pipe(double width, double size){
        this.x = 1.2;
        this.width = width;
        this.bot = Math.random() * (0.8 - size) + 0.1;
        this.top = this.bot + size;
    }

    public void move(double time, double speed){
        this.x -= time * speed;
    }

    public boolean collision(Bird b, double offset){
        if(offset > x - width / 2 && offset < x + width / 2){
            if(b.getY() > top || b.getY() < bot){
                return true;
            }
        }
        return false;
    }

    public boolean isNext(double offset){
        if(offset < x -width / 2) return true;
        return false;
    }

    public boolean isVisible(){
        return (this.x + width / 2 > 0);
    }

    public void draw(Graphics2D g2d, int width, int height){
        g2d.setColor(Color.white);
        g2d.fillRect(
                (int)(width * (x - this.width / 2)),
                (int)(height * 0),
                (int)(width * this.width),
                (int)(height * (1 - top)));
        g2d.fillRect(
                (int)(width * (x - this.width / 2)),
                (int)(height * (1-bot)),
                (int)(width * this.width),
                (int)(height * this.bot));
    }





    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getBot() {
        return bot;
    }

    public void setBot(double bot) {
        this.bot = bot;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}
