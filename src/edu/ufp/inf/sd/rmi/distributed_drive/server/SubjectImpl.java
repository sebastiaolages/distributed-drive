package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SubjectImpl extends UnicastRemoteObject implements SubjectRI {

    private final List<ObserverRI> observers;

    public SubjectImpl() throws RemoteException {
        super();
        observers = new ArrayList<>();
    }

    @Override
    public void attach(ObserverRI observer) throws RemoteException {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(ObserverRI observer) throws RemoteException {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) throws RemoteException {
        for (ObserverRI o : observers) {
            o.update(message);
        }
    }
}
