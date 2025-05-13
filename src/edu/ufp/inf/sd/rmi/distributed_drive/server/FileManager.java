package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    public static final String PROJECT_PATH = new File("").getAbsolutePath().split("out")[0];
    public static final String CLIENTS_BASE_PATH = PROJECT_PATH + "clients/";
    public static final String SERVER_BASE_PATH = PROJECT_PATH + "server/";

    // Setup de diretórios do user
    public static void setupUserFolders(String username) {
        new File(CLIENTS_BASE_PATH + username + "_LOCAL").mkdirs();
        new File(CLIENTS_BASE_PATH + username + "_SHARED").mkdirs();
        new File(SERVER_BASE_PATH + username + "_LOCAL").mkdirs();
    }

    // Getters de diretórios
    public static File getClientLocalFolder(String username) {
        return new File(CLIENTS_BASE_PATH + username + "_LOCAL");
    }

    public static File getClientSharedFolder(String username) {
        return new File(CLIENTS_BASE_PATH + username + "_SHARED");
    }

    public static File getServerLocalFolder(String username) {
        return new File(SERVER_BASE_PATH + username + "_LOCAL");
    }

    // Operações com arquivos
    public static List<String> listFilesInFolder(File folder) {
        String[] files = folder.list();
        return (files != null) ? Arrays.asList(files) : new ArrayList<>();
    }

    public static void createFileInClientAndSyncToServer(String username, String filename, String content) throws IOException {
        File clientFile = new File(getClientLocalFolder(username), filename);
        File serverFile = new File(getServerLocalFolder(username), filename);

        try (BufferedWriter clientWriter = new BufferedWriter(new FileWriter(clientFile))) {
            clientWriter.write(content);
        }

        try (BufferedWriter serverWriter = new BufferedWriter(new FileWriter(serverFile))) {
            serverWriter.write(content);
        }
    }

    public static void deleteFileInClientAndServer(String username, String filename) {
        File clientFile = new File(getClientLocalFolder(username), filename);
        File serverFile = new File(getServerLocalFolder(username), filename);
        File sharedFile = new File(getClientSharedFolder(username), filename);

        if (clientFile.exists()) clientFile.delete();
        if (serverFile.exists()) serverFile.delete();
        if (sharedFile.exists()) sharedFile.delete();
    }

    public static void renameFileInClientAndServer(String username, String oldName, String newName) {
        File clientOldFile = new File(getClientLocalFolder(username), oldName);
        File clientNewFile = new File(getClientLocalFolder(username), newName);

        File serverOldFile = new File(getServerLocalFolder(username), oldName);
        File serverNewFile = new File(getServerLocalFolder(username), newName);

        File sharedOldFile = new File(getClientSharedFolder(username), oldName);
        File sharedNewFile = new File(getClientSharedFolder(username), newName);

        if (clientOldFile.exists()) clientOldFile.renameTo(clientNewFile);
        if (serverOldFile.exists()) serverOldFile.renameTo(serverNewFile);
        if (sharedOldFile.exists()) sharedOldFile.renameTo(sharedNewFile);
    }

    public static void shareFile(String originUsername, String filename, String targetUsername) throws IOException {
        File originFile = new File(getClientLocalFolder(originUsername), filename);
        File targetShared = new File(getClientSharedFolder(targetUsername), filename);
        File targetServerLocal = new File(getServerLocalFolder(targetUsername), filename);

        if (!originFile.exists()) throw new FileNotFoundException("Ficheiro a partilhar não existe.");

        String content = new String(Files.readAllBytes(originFile.toPath()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetShared))) {
            writer.write(content);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetServerLocal))) {
            writer.write(content);
        }
    }

    public static void shareFolder(String originUsername, String folderName, String targetUsername) throws IOException {
        File originFolder = new File(getClientLocalFolder(originUsername), folderName);
        File targetSharedFolder = new File(getClientSharedFolder(targetUsername), folderName);
        File targetServerFolder = new File(getServerLocalFolder(targetUsername), folderName);

        if (!originFolder.exists() || !originFolder.isDirectory()) {
            throw new IOException("A pasta a partilhar não existe ou não é uma pasta.");
        }

        copyFolder(originFolder, targetSharedFolder);
        copyFolder(originFolder, targetServerFolder);
    }

    private static void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }
            String[] children = source.list();
            if (children != null) {
                for (String file : children) {
                    copyFolder(new File(source, file), new File(destination, file));
                }
            }
        } else {
            Files.copy(source.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void createFolder(String username, String folderName) {
        File localFolder = new File(getClientLocalFolder(username), folderName);
        File serverFolder = new File(getServerLocalFolder(username), folderName);

        localFolder.mkdirs();
        serverFolder.mkdirs();
    }

    public static void deleteFolder(String username, String folderName) {
        File localFolder = new File(getClientLocalFolder(username), folderName);
        File serverFolder = new File(getServerLocalFolder(username), folderName);
        File sharedFolder = new File(getClientSharedFolder(username), folderName);

        deleteRecursive(localFolder);
        deleteRecursive(serverFolder);
        deleteRecursive(sharedFolder);
    }

    public static void renameFolder(String username, String oldName, String newName) {
        File localOld = new File(getClientLocalFolder(username), oldName);
        File localNew = new File(getClientLocalFolder(username), newName);

        File serverOld = new File(getServerLocalFolder(username), oldName);
        File serverNew = new File(getServerLocalFolder(username), newName);

        File sharedOld = new File(getClientSharedFolder(username), oldName);
        File sharedNew = new File(getClientSharedFolder(username), newName);

        localOld.renameTo(localNew);
        serverOld.renameTo(serverNew);
        sharedOld.renameTo(sharedNew);
    }

    public static List<String> listAllRecursive(File dir, String prefix) {
        List<String> all = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                String path = prefix + f.getName();
                all.add(path + (f.isDirectory() ? "/" : ""));
                if (f.isDirectory()) {
                    all.addAll(listAllRecursive(f, path + "/"));
                }
            }
        }
        return all;
    }

    private static void deleteRecursive(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            for (File child : fileOrDir.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDir.delete();
    }
}
