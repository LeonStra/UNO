import javax.swing.*;
import java.awt.*;

public class TEST {
    public TEST() {
        JFrame f = new JFrame("Canvas Example");
        f.add(new Smiley());
        f.setLayout(null);
        f.setSize(200, 200);
        f.setVisible(true);
    }

    public static void main(String args[]) {
        new TEST();
    }

    class Smiley extends Canvas {
        public Smiley() {
            setSize(200, 200);
        }

        public void paint(Graphics g) {
            int t = 200;
            g.setColor(Color.red);
            g.fillRect(0,0,t,t);

            g.setColor(Color.YELLOW);
            g.fillOval(0, 0, t, t);

            //Augen
            g.setColor(Color.BLACK);
            g.fillOval((int) (t*0.22), (int)(t*0.27), (int) (t*0.15), (int)(t*0.15));
            g.fillOval((int)(t*0.62), (int)(t*0.27), (int) (t*0.15), (int)(t*0.15));

            // draw Mouth
            g.fillOval((int) (t*0.22), (int) (t*0.5), (int) (t*0.6), (int) (t*0.3));

            // adding smile
            g.setColor(Color.YELLOW);
            g.fillOval((int) (t*0.22), (int) (t*0.5), (int) (t*0.6), (int) (t*0.25));
        }
    }
}
