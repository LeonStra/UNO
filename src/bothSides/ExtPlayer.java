package bothSides;

import bothSides.Exceptions.NotSuitableException;

import java.rmi.RemoteException;
import java.util.LinkedList;

public interface ExtPlayer extends Player{
    void chooseFromPlayPile(Card card) throws RemoteException;
    void wishCard(Card card, String player) throws RemoteException;
    void bummsReturn(int i) throws RemoteException;
    void buzzed() throws RemoteException;
    void throwIn() throws RemoteException, NotSuitableException;

    LinkedList<Card> getPlayPile() throws RemoteException;
}
