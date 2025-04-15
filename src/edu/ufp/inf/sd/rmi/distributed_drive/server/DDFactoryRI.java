package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DDFactoryRI extends Remote {
    DDSessionRI login(String username, String password) throws RemoteException;
    boolean register(String username, String password) throws RemoteException;
}
