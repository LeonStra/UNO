package bothSides;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface View extends Remote {
    void refresh() throws RemoteException;
    void changeDrawPass(boolean draw) throws RemoteException;
    void refreshChat(LinkedList<ChatMessage> messages) throws RemoteException;

    //Dialogs
    void drawOrCounter(int drawCount) throws RemoteException;
    void selectColor() throws RemoteException;

    //Changes
    void setNews(String txt) throws RemoteException;
}
