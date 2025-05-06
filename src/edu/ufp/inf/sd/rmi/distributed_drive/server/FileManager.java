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

        if (clientFile.exists()) clientFile.delete();
        if (serverFile.exists()) serverFile.delete();
    }

    public static void renameFileInClientAndServer(String username, String oldName, String newName) {
        File clientOldFile = new File(getClientLocalFolder(username), oldName);
        File clientNewFile = new File(getClientLocalFolder(username), newName);

        File serverOldFile = new File(getServerLocalFolder(username), oldName);
        File serverNewFile = new File(getServerLocalFolder(username), newName);

        if (clientOldFile.exists()) clientOldFile.renameTo(clientNewFile);
        if (serverOldFile.exists()) serverOldFile.renameTo(serverNewFile);
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

    // NOVAS FUNÇÕES – Gestao de Pastas

    public static void createFolder(String username, String folderName) {
        File localFolder = new File(getClientLocalFolder(username), folderName);
        File serverFolder = new File(getServerLocalFolder(username), folderName);

        localFolder.mkdirs();
        serverFolder.mkdirs();
    }

    public static void deleteFolder(String username, String folderName) {
        File localFolder = new File(getClientLocalFolder(username), folderName);
        File serverFolder = new File(getServerLocalFolder(username), folderName);

        deleteRecursive(localFolder);
        deleteRecursive(serverFolder);
    }

    public static void renameFolder(String username, String oldName, String newName) {
        File localOld = new File(getClientLocalFolder(username), oldName);
        File localNew = new File(getClientLocalFolder(username), newName);

        File serverOld = new File(getServerLocalFolder(username), oldName);
        File serverNew = new File(getServerLocalFolder(username), newName);

        localOld.renameTo(localNew);
        serverOld.renameTo(serverNew);
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
