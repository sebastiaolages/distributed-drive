package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DDSessionImpl extends UnicastRemoteObject implements DDSessionRI {

    private final String username;
    private final SubjectRI subject;
    private final DDFactoryImpl factory;

    public DDSessionImpl(String username, DDFactoryImpl factory) throws RemoteException {
        super();
        this.username = username;
        this.factory = factory;
        this.subject = new SubjectImpl();
        FileManager.setupUserFolders(username);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public SubjectRI getSubject() throws RemoteException {
        return subject;
    }

    @Override
    public List<String> listLocalFiles() throws RemoteException {
        return FileManager.listFilesInFolder(FileManager.getClientLocalFolder(username));
    }

    @Override
    public void createFile(String filename, String content) throws RemoteException {
        try {
            FileManager.createFileInClientAndSyncToServer(username, filename, content);
            subject.notifyObservers("Ficheiro '" + filename + "' foi criado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFile(String filename) throws RemoteException {
        FileManager.deleteFileInClientAndServer(username, filename);
        subject.notifyObservers("Ficheiro '" + filename + "' foi apagado.");
    }

    @Override
    public void renameFile(String oldName, String newName) throws RemoteException {
        FileManager.renameFileInClientAndServer(username, oldName, newName);
        subject.notifyObservers("Ficheiro renomeado de '" + oldName + "' para '" + newName + "'.");
    }

    @Override
    public void shareFile(String filename, String targetUsername) throws RemoteException {
        try {
            FileManager.shareFile(username, filename, targetUsername);

            DDSessionRI targetSession = factory.getActiveSessions().get(targetUsername);
            if (targetSession != null) {
                targetSession.getSubject().notifyObservers("Recebeu um ficheiro partilhado de " + username + ": " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
