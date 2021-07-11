package bothSides;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ExtPlayer extends Player{
    void chooseFromPlayPile(Card card) throws RemoteException;
    void wishCard(Card card, String player) throws RemoteException;
    void setFourList(ArrayList list) throws RemoteException;
}
