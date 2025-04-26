package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DDSessionRI extends Remote {
    String getUsername() throws RemoteException;

    List<String> listLocalFiles() throws RemoteException;

    void createFile(String filename, String content) throws RemoteException;

}
