package bothSides;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Player extends Remote {
    void leaveGame() throws  RemoteException;
    void drawCard() throws RemoteException;
    void itsMyTurn() throws InterruptedException, RemoteException;
    void play(Card card) throws InterruptedException, RemoteException;
    ArrayList<Card> getHand() throws InterruptedException, RemoteException;
    Card getTop() throws InterruptedException, RemoteException;
}
