package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DDSessionRI extends Remote {
    String getUsername() throws RemoteException;

    List<String> listLocalFiles() throws RemoteException;

    void createFile(String filename, String content) throws RemoteException;

    void deleteFile(String filename) throws RemoteException;

    void renameFile(String oldName, String newName) throws RemoteException;

    void shareFile(String filename, String targetUsername) throws RemoteException;

    SubjectRI getSubject() throws RemoteException;

    // Gestão de Pastas
    void createFolder(String folderName) throws RemoteException;

    void deleteFolder(String folderName) throws RemoteException;

    void renameFolder(String oldName, String newName) throws RemoteException;

    List<String> listAllLocalContent() throws RemoteException;

    void shareFolder(String folderName, String targetUsername) throws RemoteException;

}
