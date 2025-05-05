package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SubjectRI extends Remote {
    void attach(ObserverRI observer) throws RemoteException;
    void detach(ObserverRI observer) throws RemoteException;
    void notifyObservers(String message) throws RemoteException;
}
