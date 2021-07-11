package bothSides;

import Exceptions.NotSuitableException;
import Exceptions.TurnException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;

public interface Player extends Remote {
    void leaveGame() throws  RemoteException;
    void drawCard() throws RemoteException;
    void takeDrawCount() throws RemoteException;
    void play(Card card) throws RemoteException, NotSuitableException, TurnException;
    void wish(COLOR color) throws RemoteException;
    void sayUno() throws RemoteException;
    void sendMessage(String message) throws RemoteException;

    //Getter/Setter
    void setView(View view) throws RemoteException;
    void setName(String name) throws RemoteException;
    void setSorting(boolean b) throws RemoteException;
    ArrayList<Card> getHand() throws RemoteException;
    boolean getSorting() throws RemoteException;
    Card getTop() throws RemoteException;
    LinkedList<String> getNameList() throws RemoteException;
}
