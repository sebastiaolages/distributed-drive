package edu.ufp.inf.sd.rmi.distributed_drive.server;

import java.io.*;
import java.util.*;

public class SharedRegistry {

    private static final String FILE_PATH = "shared_registry.txt";
    private static final Map<String, Set<String>> sharedMap = new HashMap<>();

    static {
        loadFromFile();
    }

    public static void registerShare(String owner, String filename, String targetUser) {
        String key = owner + ":" + filename;
        sharedMap.putIfAbsent(key, new HashSet<>());
        sharedMap.get(key).add(targetUser);
        saveToFile();
    }

    public static void removeShare(String owner, String filename) {
        String key = owner + ":" + filename;
        sharedMap.remove(key);
        saveToFile();
    }

    public static void updateShareKey(String owner, String oldName, String newName) {
        String oldKey = owner + ":" + oldName;
        String newKey = owner + ":" + newName;
        if (sharedMap.containsKey(oldKey)) {
            Set<String> users = sharedMap.remove(oldKey);
            sharedMap.put(newKey, users);
            saveToFile();
        }
    }

    public static List<String> getUsersWithSharedCopy(String owner, String filename) {
        String key = owner + ":" + filename;
        Set<String> users = sharedMap.getOrDefault(key, new HashSet<>());
        return new ArrayList<>(users);
    }

    private static void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String key = parts[0] + ":" + parts[1];
                    sharedMap.putIfAbsent(key, new HashSet<>());
                    sharedMap.get(key).add(parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, Set<String>> entry : sharedMap.entrySet()) {
                String[] parts = entry.getKey().split(":");
                for (String user : entry.getValue()) {
                    writer.write(parts[0] + ":" + parts[1] + ":" + user);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
