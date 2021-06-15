import cards.Card;

import java.util.ArrayList;

public class Controller {
    public Pile drawpile;
    public Pile playPile;
    public Player actualPlayer;
    public ArrayList<Player> players;

    public boolean playCard(Card card, Player player) {
        //Spielbarkeitscheck
        if (!playPile.top().suitable(card)) {
            return false;
        }
        playPile.add(card);
        card.action();
        return true;
    }

    public void join(){}

    public void start(){}


}
