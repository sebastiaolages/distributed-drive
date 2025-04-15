package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DDSessionImpl extends UnicastRemoteObject implements DDSessionRI {

    private final String username;

    public DDSessionImpl(String username) throws RemoteException {
        super();
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
