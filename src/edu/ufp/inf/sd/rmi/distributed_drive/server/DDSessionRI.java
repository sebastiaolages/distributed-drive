package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DDSessionRI extends Remote {
    String getUsername() throws RemoteException;
}
