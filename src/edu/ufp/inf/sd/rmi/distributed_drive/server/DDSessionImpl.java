package edu.ufp.inf.sd.rmi.distributed_drive.server;

import edu.ufp.inf.sd.rabbitmq.rabbitmq.distributed_drive2.producer.RMQPublisher;

import java.io.File;
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
            String msg = "Ficheiro '" + filename + "' foi criado.";
            subject.notifyObservers(msg);
            RMQPublisher.publishUpdate("[" + username + "] " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFile(String filename) throws RemoteException {
        FileManager.deleteFileInClientAndServer(username, filename);
        String msg = "Ficheiro '" + filename + "' foi apagado.";
        subject.notifyObservers(msg);
        RMQPublisher.publishUpdate("[" + username + "] " + msg);

        for (String targetUser : SharedRegistry.getUsersWithSharedCopy(username, filename)) {
            FileManager.deleteFileInClientAndServer(targetUser, filename);

            File sharedFile = new File(FileManager.getClientSharedFolder(targetUser), filename);
            if (sharedFile.exists()) {
                sharedFile.delete();
            }

            DDSessionRI session = factory.getActiveSessions().get(targetUser);
            if (session != null) {
                session.getSubject().notifyObservers("Ficheiro partilhado '" + filename + "' foi apagado por " + username);
                RMQPublisher.publishUpdate("[" + username + "] apagou ficheiro partilhado '" + filename + "' com [" + targetUser + "]");
            }
        }

        SharedRegistry.removeShare(username, filename);
    }

    @Override
    public void renameFile(String oldName, String newName) throws RemoteException {
        FileManager.renameFileInClientAndServer(username, oldName, newName);
        String msg = "Ficheiro renomeado de '" + oldName + "' para '" + newName + "'.";
        subject.notifyObservers(msg);
        RMQPublisher.publishUpdate("[" + username + "] " + msg);

        for (String targetUser : SharedRegistry.getUsersWithSharedCopy(username, oldName)) {
            FileManager.renameFileInClientAndServer(targetUser, oldName, newName);
            File sharedOld = new File(FileManager.getClientSharedFolder(targetUser), oldName);
            File sharedNew = new File(FileManager.getClientSharedFolder(targetUser), newName);
            if (sharedOld.exists()) {
                sharedOld.renameTo(sharedNew);
            }

            DDSessionRI session = factory.getActiveSessions().get(targetUser);
            if (session != null) {
                session.getSubject().notifyObservers("Ficheiro partilhado foi renomeado de '" + oldName + "' para '" + newName + "' por " + username);
                RMQPublisher.publishUpdate("[" + username + "] renomeou ficheiro partilhado de '" + oldName + "' para '" + newName + "' com [" + targetUser + "]");
            }
        }

        SharedRegistry.updateShareKey(username, oldName, newName);
    }

    @Override
    public void shareFile(String filename, String targetUsername) throws RemoteException {
        try {
            FileManager.shareFile(username, filename, targetUsername);
            SharedRegistry.registerShare(username, filename, targetUsername);

            String msg = "Partilhou '" + filename + "' com " + targetUsername;
            subject.notifyObservers(msg);
            RMQPublisher.publishUpdate("[" + username + "] " + msg);

            DDSessionRI targetSession = factory.getActiveSessions().get(targetUsername);
            if (targetSession != null) {
                targetSession.getSubject().notifyObservers("Recebeu um ficheiro partilhado de " + username + ": " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shareFolder(String folderName, String targetUsername) throws RemoteException {
        try {
            FileManager.shareFolder(username, folderName, targetUsername);
            SharedRegistry.registerShare(username, folderName, targetUsername);

            String msg = "Partilhou pasta '" + folderName + "' com " + targetUsername;
            subject.notifyObservers(msg);
            RMQPublisher.publishUpdate("[" + username + "] " + msg);

            DDSessionRI targetSession = factory.getActiveSessions().get(targetUsername);
            if (targetSession != null) {
                targetSession.getSubject().notifyObservers("Recebeu uma pasta partilhada de " + username + ": " + folderName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RemoteException("Erro ao partilhar pasta", e);
        }
    }


    @Override
    public void createFolder(String folderName) throws RemoteException {
        FileManager.createFolder(username, folderName);
        String msg = "Pasta '" + folderName + "' foi criada.";
        subject.notifyObservers(msg);
        RMQPublisher.publishUpdate("[" + username + "] " + msg);
    }

    @Override
    public void deleteFolder(String folderName) throws RemoteException {
        FileManager.deleteFolder(username, folderName);
        String msg = "Pasta '" + folderName + "' foi apagada.";
        subject.notifyObservers(msg);
        RMQPublisher.publishUpdate("[" + username + "] " + msg);

        for (String targetUser : SharedRegistry.getUsersWithSharedCopy(username, folderName)) {
            FileManager.deleteFolder(targetUser, folderName);

            File sharedFolder = new File(FileManager.getClientSharedFolder(targetUser), folderName);
            if (sharedFolder.exists()) {
                deleteRecursively(sharedFolder);
            }

            DDSessionRI session = factory.getActiveSessions().get(targetUser);
            if (session != null) {
                session.getSubject().notifyObservers("Pasta partilhada '" + folderName + "' foi apagada por " + username);
                RMQPublisher.publishUpdate("[" + username + "] apagou pasta partilhada '" + folderName + "' com [" + targetUser + "]");
            }
        }

        SharedRegistry.removeShare(username, folderName);
    }

    @Override
    public void renameFolder(String oldName, String newName) throws RemoteException {
        FileManager.renameFolder(username, oldName, newName);
        String msg = "Pasta renomeada de '" + oldName + "' para '" + newName + "'.";
        subject.notifyObservers(msg);
        RMQPublisher.publishUpdate("[" + username + "] " + msg);

        for (String targetUser : SharedRegistry.getUsersWithSharedCopy(username, oldName)) {
            FileManager.renameFolder(targetUser, oldName, newName);

            File sharedOld = new File(FileManager.getClientSharedFolder(targetUser), oldName);
            File sharedNew = new File(FileManager.getClientSharedFolder(targetUser), newName);
            if (sharedOld.exists()) {
                sharedOld.renameTo(sharedNew);
            }

            DDSessionRI session = factory.getActiveSessions().get(targetUser);
            if (session != null) {
                session.getSubject().notifyObservers("Pasta partilhada foi renomeada de '" + oldName + "' para '" + newName + "' por " + username);
                RMQPublisher.publishUpdate("[" + username + "] renomeou pasta partilhada de '" + oldName + "' para '" + newName + "' com [" + targetUser + "]");
            }
        }

        SharedRegistry.updateShareKey(username, oldName, newName);
    }

    @Override
    public List<String> listAllLocalContent() throws RemoteException {
        return FileManager.listAllRecursive(FileManager.getClientLocalFolder(username), "");
    }

    private void deleteRecursively(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File child : fileOrDir.listFiles()) {
                deleteRecursively(child);
            }
        }
        fileOrDir.delete();
    }
}