package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    public static final String PROJECT_PATH = new File("").getAbsolutePath().split("out")[0];
    public static final String CLIENTS_BASE_PATH = PROJECT_PATH + "clients/";
    public static final String SERVER_BASE_PATH = PROJECT_PATH + "server/";

    public static void setupUserFolders(String username) {
        new File(CLIENTS_BASE_PATH + username + "_LOCAL").mkdirs();
        new File(CLIENTS_BASE_PATH + username + "_SHARED").mkdirs();
        new File(SERVER_BASE_PATH + username + "_LOCAL").mkdirs();
    }

    public static File getClientLocalFolder(String username) {
        return new File(CLIENTS_BASE_PATH + username + "_LOCAL");
    }

    public static File getClientSharedFolder(String username) {
        return new File(CLIENTS_BASE_PATH + username + "_SHARED");
    }

    public static File getServerLocalFolder(String username) {
        return new File(SERVER_BASE_PATH + username + "_LOCAL");
    }

    public static List<String> listFilesInFolder(File folder) {
        String[] files = folder.list();
        return (files != null) ? Arrays.asList(files) : new java.util.ArrayList<>();
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

        if (clientFile.exists()) {
            clientFile.delete();
        }

        if (serverFile.exists()) {
            serverFile.delete();
        }
    }

    public static void renameFileInClientAndServer(String username, String oldName, String newName) {
        File clientOldFile = new File(getClientLocalFolder(username), oldName);
        File clientNewFile = new File(getClientLocalFolder(username), newName);

        File serverOldFile = new File(getServerLocalFolder(username), oldName);
        File serverNewFile = new File(getServerLocalFolder(username), newName);

        if (clientOldFile.exists()) {
            clientOldFile.renameTo(clientNewFile);
        }
        if (serverOldFile.exists()) {
            serverOldFile.renameTo(serverNewFile);
        }
    }

    public static void shareFile(String originUsername, String filename, String targetUsername) throws IOException {
        File originFile = new File(getClientLocalFolder(originUsername), filename);
        File targetShared = new File(getClientSharedFolder(targetUsername), filename);
        File targetServerLocal = new File(getServerLocalFolder(targetUsername), filename);

        if (!originFile.exists()) {
            throw new FileNotFoundException("Ficheiro a partilhar não existe.");
        }

        String content = new String(Files.readAllBytes(originFile.toPath()));

        // Escreve no shared do cliente destino
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetShared))) {
            writer.write(content);
        }

        // Escreve no local do servidor destino
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetServerLocal))) {
            writer.write(content);
        }
    }
}
