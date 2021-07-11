package bothSides;

import java.rmi.RemoteException;

public interface ExtPlayer extends Player{
    void chooseFromPlayPile(Card card) throws RemoteException;
    void wishCard(Card card, String player) throws RemoteException;
    void bummsReturn(int i) throws RemoteException;
}
