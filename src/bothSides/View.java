package bothSides;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface View extends Remote {
    void refresh() throws RemoteException;
    void changeDrawPass(boolean draw) throws RemoteException;
    void refreshChat(LinkedList<ChatMessage> messages) throws RemoteException;

    //Dialogs
    void closeDialogs() throws RemoteException;
    void drawOrCounter(int drawCount) throws RemoteException;
    void selectColor() throws RemoteException;
    void takeFromPlayPile() throws RemoteException;
    void wishCard() throws RemoteException;
    void bumms() throws RemoteException;
    void toggleFastButton(boolean b) throws RemoteException;

    //Changes
    void setNews(String txt) throws RemoteException;
}
