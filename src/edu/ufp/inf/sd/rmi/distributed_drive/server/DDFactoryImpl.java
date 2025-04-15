package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class DDFactoryImpl extends UnicastRemoteObject implements DDFactoryRI {

    private final UserManager userManager;
    private final Map<String, DDSessionRI> activeSessions;

    public DDFactoryImpl() throws RemoteException {
        super();
        this.userManager = new UserManager();
        this.activeSessions = new HashMap<>();
    }

    @Override
    public boolean register(String username, String password) throws RemoteException {
        return userManager.registerUser(username, password);
    }

    @Override
    public DDSessionRI login(String username, String password) throws RemoteException {
        if (userManager.loginUser(username, password)) {
            if (!activeSessions.containsKey(username)) {
                DDSessionRI session = new DDSessionImpl(username);
                activeSessions.put(username, session);
            }
            return activeSessions.get(username);
        }
        return null;
    }
}
