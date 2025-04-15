package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class DDServiceImpl extends UnicastRemoteObject implements DDServiceRI {

    private final UserManager userManager;

    public DDServiceImpl() throws RemoteException {
        super();
        this.userManager = new UserManager();
    }

    @Override
    public boolean register(String username, String password) throws RemoteException {
        return userManager.registerUser(username, password);
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        return userManager.loginUser(username, password);
    }
}
