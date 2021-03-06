package server;

import bothSides.Exceptions.*;
import bothSides.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class ExtPlayerImpl extends PlayerImpl implements ExtPlayer{
    protected ArrayList<ExtPlayerImpl> buzzedList;
    private HashMap<ExtPlayerImpl,Integer> fourMap;
    protected boolean showFour;
    protected boolean throwaway;

    public ExtPlayerImpl(Pile drawPile, Pile playPile, LinkedList<PlayerImpl> players, Counter drawCount, LinkedList<ChatMessage> chatHistory, ArrayList<ExtPlayerImpl> buzzedList, HashMap<ExtPlayerImpl,Integer> fourMap) throws RemoteException {
        super(drawPile, playPile, players, drawCount, chatHistory);
        this.buzzedList = buzzedList;
        this.fourMap = fourMap;
        this.throwaway = false;
        this.showFour = false;
    }

    @Override
    public void next() throws RemoteException {
        System.out.println("hi");
        myTurn = false;
        view.changeDrawPass(true);
        try {
            System.out.println("before");
            Thread.sleep(1*1000);
            System.out.println("after");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.next();
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
            fourMap.put(this,card.getcType().getValue());
            if (fourMap.size() == players.size()){
                int max =Collections.max(fourMap.values());
                for (ExtPlayerImpl p : fourMap.keySet()){
                    p.giveCards(max <= fourMap.get(p)?2:0);
                }
                next();
            }
        }else if ((playPile.getFirst().suitable(card) && (myTurn || card.getcType().equals(TYPE.SIX))) || playPile.getFirst().equals(card)){
            //Reinwerfen
            if (playPile.getFirst().equals(card) || card.getcType().equals(TYPE.SIX)){
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
                    for (PlayerImpl p : players){
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
                    System.out.println("hi");
                    fourMap.clear();
                    players.forEach(p -> {
                        ((ExtPlayerImpl) p).setShowFour(true);
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
                    buzzedList.clear();
                    players.forEach(p -> {
                        try {
                            p.view.toggleBuzzer(true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
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
        throwaway = false;
        showFour = false;
        view.changeDrawPass(true);

    }

    @Override
    public void chooseFromPlayPile(Card card) throws RemoteException {
        if (myTurn && alreadyPlayed && playPile.getFirst().getcType().equals(TYPE.TWO) && playPile.contains(card)){
            playPile.remove(card);
            hand.add(TYPE.getMultiColored().contains(card.getcType())?new Card(card.getcType(),COLOR.MULTICOLORED):card);
            next();
        }else{
            view.takeFromPlayPile();
        }
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
            giveCards(count == i?0:count);
            view.setNews("Es waren: " + count + " 8er");
            next();
        }
    }

    @Override
    public void buzzed() throws RemoteException {
        buzzedList.add(this);
        view.toggleBuzzer(false);
        if (buzzedList.size() == players.size()){
            players.forEach(p -> p.setNews(getName() + " ist letzter"));
            giveCards(1);
            players.getLast().next();
        }
    }

    @Override
    public void throwIn() throws RemoteException, NotSuitableException {
        if (hand.contains(playPile.getFirst())){
            play(playPile.getFirst());
        }
    }

    @Override
    public LinkedList<Card> getPlayPile(){
        if (myTurn && alreadyPlayed && getTop().getcType().equals(TYPE.TWO)){
            return playPile;
        }
        return null;
    }

    private void setShowFour(boolean b){
        if (hand.contains(playPile.getFirst()) || hand.contains(new Card(TYPE.SIX,playPile.getFirst().getColor()))){
            System.out.println("OUCH"+getName());
            try {
                view.fourThrowIn();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            showFour = true;
        }
    }
}
