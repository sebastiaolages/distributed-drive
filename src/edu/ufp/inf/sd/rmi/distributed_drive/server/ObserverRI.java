package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {
    void update(String message) throws RemoteException;
}
