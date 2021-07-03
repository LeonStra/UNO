package bothSides;

import Exceptions.NotSuitableException;
import Exceptions.TurnException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Player extends Remote {
    void leaveGame() throws  RemoteException;
    void drawCard() throws RemoteException;
    void takeDrawCount() throws RemoteException;
    void itsMyTurn() throws RemoteException, TurnException;
    void play(Card card) throws RemoteException, NotSuitableException;
    void wish(COLOR color) throws RemoteException;

    //Getter/Setter
    void setView(View view) throws RemoteException;
    ArrayList<Card> getHand() throws RemoteException;
    Card getTop() throws RemoteException;
}
