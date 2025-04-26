package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String FILE_PATH = "C:\\Users\\sebas\\Desktop\\faculdade\\sd\\projeto_finalSD2025\\users.txt";

    private final Map<String, String> users = new HashMap<>();

    public UserManager() {
        loadUsersFromFile();
    }

    private void loadUsersFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, password);
        boolean saved = saveUserToFile(username, password);

        // Criar as pastas assim que o registo for bem-sucedido
        if (saved) {
            FileManager.setupUserFolders(username);
        }

        return saved;
    }


    private boolean saveUserToFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
