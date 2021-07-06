package bothSides;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface View extends Remote {
    void refresh() throws RemoteException;

    //Dialogs
    void drawOrCounter(int drawCount) throws RemoteException;
    void selectColor() throws RemoteException;

    //Changes
    void setNews(String txt) throws RemoteException;
}
