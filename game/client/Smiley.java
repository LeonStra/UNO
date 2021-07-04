package client;

import java.awt.*;

class Smiley extends Canvas {
    private int size;

    public Smiley(int size) {
        this.size = size;
        setSize(200, 200);
    }

    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(0,0,size,size);

        g.setColor(Color.YELLOW);
        g.fillOval(0, 0, size, size);

        //Augen
        g.setColor(Color.BLACK);
        g.fillOval((int) (size*0.22), (int)(size*0.27), (int) (size*0.15), (int)(size*0.15));
        g.fillOval((int)(size*0.62), (int)(size*0.27), (int) (size*0.15), (int)(size*0.15));

        // draw Mouth
        g.fillOval((int) (size*0.22), (int) (size*0.5), (int) (size*0.6), (int) (size*0.3));

        // adding smile
        g.setColor(Color.YELLOW);
        g.fillOval((int) (size*0.22), (int) (size*0.5), (int) (size*0.6), (int) (size*0.25));
    }
}