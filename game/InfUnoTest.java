//Zum Testen des Programms

import main.*;
import view.BoardFrame;

public class InfUnoTest {

    public static void main(String[] args) {
        System.out.println("Start"+1);
        Controller controller = new Controller();

        //Spieler1
        Player player1 = new Player(controller);
        controller.join(player1);
    }
}
