package server;

import Exceptions.*;
import bothSides.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class ExtPlayerImpl extends PlayerImpl implements ExtPlayer{
    protected LinkedList<ExtPlayerImpl> players;
    protected boolean showFour;
    protected boolean throwaway;

    public ExtPlayerImpl(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<PlayerImpl> players, Counter drawCount, LinkedList<ChatMessage> chatHistory) throws RemoteException {
        super(drawPile, playPile, players, drawCount, chatHistory);
        this.players = new LinkedList<>();
        players.forEach(p -> this.players.add((ExtPlayerImpl)p));
    }

    @Override
    public void play(Card card) throws RemoteException, NotSuitableException {
        boolean wait = false;

        if (throwaway){
            hand.remove(card);
            if (players.getFirst().getHand().size() == hand.size()){
                throwaway = false;
                next();
            }
        } else if (showFour){
            /*globales Objekt*/
            showFour = false;
            next();
        }else if (playPile.getFirst().suitable(card) && myTurn || playPile.getFirst().equals(card)){
            System.out.println(playPile.getFirst().getColor());
            if (!myTurn){
                players.getLast().breakTurn();
                while (players.getLast() != this){
                    players.addLast(players.getFirst());
                    players.removeFirst();
                }
                myTurn = true;
                players.forEach((p) ->{ p.refreshView();});
            }
            hand.remove(card);
            playPile.addFirst(card);
            switch (card.getcType()){
                case ZERO:
                    Hand h = players.getLast().hand;
                    for (ExtPlayerImpl p : players){
                        if (players.indexOf(p) != 0) {
                            p.hand = players.get(players.indexOf(p) - 1).hand;
                        }
                    }
                    players.getFirst().hand = h;
                    break;
                case ONE:
                    if (players.getFirst().getHand().size() > hand.size()-1) {
                        view.setNews("Lege Kartem ab");
                        throwaway = true;
                        wait = true;
                    } else{
                        giveCards(players.getFirst().getHand().size()-hand.size()-1);
                    }
                    break;
                case TWO:
                    view.takeFromPlayPile();
                    wait = true;
                    break;
                case THREE:
                    view.wishCard();
                    wait = true;
                    break;
                case FOUR:
                    players.forEach(p -> {
                        p.showFour = true;
                        p.setNews("Zeige eine Karte");
                    });
                    wait = true;
                    break;
                case FIVE:
                    playPile.add(new Card(TYPE.UNKNOWN,COLOR.MULTICOLORED));
                    break;
                case SIX:

                    break;
                case SEVEN:
                    LinkedList<Card> list = new LinkedList<>();
                    for (ExtPlayerImpl p : players){
                        list.addAll(p.getHand());
                        p.hand.removeAll(p.getHand());
                    }
                    Collections.shuffle(list);
                    while (list.size() > 0){
                        for (ExtPlayerImpl p : players){
                            if (list.size() <= 0){break;}
                            p.hand.add(list.getFirst());
                            list.removeFirst();
                        }
                    }
                    break;
                case EIGHT:

                    break;
                case NINE:

                    break;
                case DRAWTWO:
                    drawCount.addCounter(2);
                    increased = true;
                    break;
                case REVERSE:
                    if (players.size() == 2){
                        players.addLast(players.getFirst());
                        players.removeFirst();
                    }else {
                        Collections.reverse(players);
                        players.removeFirst();
                        players.addLast(this);
                    }
                    break;
                case SKIP:
                    players.addLast(players.getFirst());
                    players.removeFirst();
                    players.forEach((p)->p.setNews("Aussetzen"));
                    break;
                case WILD:
                    view.selectColor();
                    wait = true;
                    break;
                case WILDFOUR:
                    drawCount.addCounter(4);
                    increased = true;
                    wait = true;
                    view.selectColor();
                    break;
            }
            if (!wait) {
                next();
            }
            refreshView();
        } else {throw new NotSuitableException();}
    }
    protected void breakTurn() throws RemoteException {
        view.closeDialogs();
        if (!saidUno && hand.size() == 1){
            giveCards(2);
        }
        cardDrawn = false;
        increased = false;
        myTurn = false;
        saidUno = false;
        view.changeDrawPass(true);

    }

    @Override
    public void chooseFromPlayPile(Card card) throws RemoteException {
        playPile.remove(card);
        hand.add(card);
        next();
    }

    @Override
    public void wishCard(Card card, String playerName) throws RemoteException {
        players.forEach(p -> {
            if(p.getName() == playerName && p.hand.contains(card)){
                p.hand.remove(card);
                this.hand.add(card);
            }
        });
    }

    //Getter/Setter
    public void setFourList(ArrayList fourList){

    }
}
