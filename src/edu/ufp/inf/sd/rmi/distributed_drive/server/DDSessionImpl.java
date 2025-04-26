package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DDSessionImpl extends UnicastRemoteObject implements DDSessionRI {

    private final String username;

    public DDSessionImpl(String username) throws RemoteException {
        super();
        this.username = username;
        FileManager.setupUserFolders(username);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public List<String> listLocalFiles() throws RemoteException {
        return FileManager.listFilesInFolder(FileManager.getClientLocalFolder(username));
    }


    @Override
    public void createFile(String filename, String content) throws RemoteException {
        try {
            FileManager.createFileInClientAndSyncToServer(username, filename, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
