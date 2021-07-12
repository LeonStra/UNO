package server;

import bothSides.Exceptions.*;
import bothSides.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class ExtPlayerImpl extends PlayerImpl implements ExtPlayer{
    protected boolean showFour;
    protected boolean throwaway;

    public ExtPlayerImpl(LinkedList<Card> drawPile, LinkedList<Card> playPile, LinkedList<PlayerImpl> players, Counter drawCount, LinkedList<ChatMessage> chatHistory) throws RemoteException {
        super(drawPile, playPile, players, drawCount, chatHistory);
        this.throwaway = false;
        this.showFour = false;
    }

    @Override
    public void play(Card card) throws RemoteException, NotSuitableException {
        boolean wait = false;
        if (throwaway){
            hand.remove(card);
            if (players.getFirst().getHand().size() == hand.size()){
                throwaway = false;
                view.setNews("Mehr darf nicht abgelegt werden");
                next();
            }
        } else if (showFour){
            showFour = false;
            next();
        }else if (playPile.getFirst().suitable(card) && myTurn || playPile.getFirst().equals(card)){
            System.out.println(playPile.getFirst().getColor());
            //Reinwerfen
            if (!myTurn){
                ((ExtPlayerImpl)players.getLast()).breakTurn();
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
                    System.out.println(players.size());
                    for (PlayerImpl p : players){
                        System.out.println(p.getName());
                        if (players.indexOf(p) != 0) {
                            p.hand = players.get(players.indexOf(p) - 1).hand;
                        }
                    }
                    players.getFirst().hand = h;
                    break;
                case ONE:
                    if (players.getFirst().getHand().size() < hand.size()) {
                        view.setNews("Lege"+ players.getFirst().getHand().size() +"Karten ab");
                        throwaway = true;
                        wait = true;
                    } else{
                        giveCards(players.getFirst().getHand().size()-hand.size());
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
                        ((ExtPlayerImpl) p).showFour = true;
                        p.setNews("Zeige eine Karte");
                    });
                    wait = true;
                    break;
                case FIVE:
                    playPile.removeAll(playPile);
                    playPile.add(new Card(TYPE.UNKNOWN,COLOR.MULTICOLORED));
                    players.getFirst().setNews("Lege was du willst");
                    break;
                case SIX:

                    break;
                case SEVEN:
                    //Zusammenlegen
                    LinkedList<Card> cards = new LinkedList<>();
                    for (PlayerImpl p : players){
                        cards.addAll(p.getHand());
                        p.hand.removeAll(p.getHand());
                    }
                    Collections.shuffle(cards);

                    //Neu verteilen
                    LinkedList<PlayerImpl> playersCopy = (LinkedList<PlayerImpl>) players.clone();
                    playersCopy.addFirst(playersCopy.getLast());
                    playersCopy.removeLast();
                    while (cards.size() > 0){
                        for (PlayerImpl p : playersCopy){
                            if (cards.size() <= 0){break;}
                            p.hand.add(cards.getFirst());
                            cards.removeFirst();
                        }
                    }
                    break;
                case EIGHT:
                    view.setNews("BUMMMMS");
                    view.bumms();
                    wait = true;
                    break;
                case NINE:
                    view.toggleFastButton(true);
                    wait = true;
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
            alreadyPlayed = true;
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
        if (myTurn && !card.getcType().equals(TYPE.UNKNOWN) && !card.getColor().equals(COLOR.UNKNOWN) && getNameList().contains(playerName) && !name.equals(playerName) && alreadyPlayed && playPile.getFirst().getcType().equals(TYPE.THREE)){
            Card finalCard = card.getcType().equals(TYPE.WILDFOUR) || card.getcType().equals(TYPE.WILD)?new Card(card.getcType(),COLOR.MULTICOLORED):card;
            players.forEach(p -> {
                if (p.getName().equals(playerName) && p.hand.contains(finalCard)) {
                    p.hand.remove(finalCard);
                    this.hand.add(finalCard);
                    this.setNews("Karte bekommen");
                }
            });
            next();
        }else {
            view.wishCard();
            view.setNews("Falsche Eingabe");
        }
    }

    @Override
    public void bummsReturn(int i) throws RemoteException {
        if (myTurn && playPile.getFirst().getcType().equals(TYPE.EIGHT) && alreadyPlayed){
            int count = playPile.stream().mapToInt(c -> c.getcType().equals(TYPE.EIGHT) ? 1 : 0).sum();
            System.out.println(count);
            giveCards(count == i?0:count);
            view.setNews("Es waren: " + count + " 8er");
            next();
        }
    }

    @Override
    public LinkedList<Card> getPlayPile(){
        if (myTurn && alreadyPlayed && getTop().getcType().equals(TYPE.TWO)){
            return playPile;
        }
        return null;
    }
}
