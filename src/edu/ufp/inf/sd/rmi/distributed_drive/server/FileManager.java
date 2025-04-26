package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileManager {
    public static final String PROJECT_PATH = new File("").getAbsolutePath().split("out")[0];
    public static final String CLIENTS_BASE_PATH = PROJECT_PATH + "clients/";
    public static final String SERVER_BASE_PATH = PROJECT_PATH + "server/";



    public static void setupUserFolders(String username) {
        String folderName = username + "_LOCAL";
        new File(CLIENTS_BASE_PATH + folderName).mkdirs();
        new File(SERVER_BASE_PATH + folderName).mkdirs();
    }

    public static File getClientLocalFolder(String username) {
        return new File(CLIENTS_BASE_PATH + username + "_LOCAL");
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
}
