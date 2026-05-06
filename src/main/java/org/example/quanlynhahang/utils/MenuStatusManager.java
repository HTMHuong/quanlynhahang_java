package org.example.quanlynhahang.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class MenuStatusManager {
    private static final String FILE_PATH = "locked_items.txt";

    // Hàm đọc danh sách món bị khóa từ file
    public static Set<String> loadLockedItems() {
        Set<String> lockedItems = new HashSet<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return lockedItems;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lockedItems.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lockedItems;
    }

    // Hàm THÊM món vào danh sách khóa (Tương ứng với nút OFF)
    public static void addItem(String maMon) {
        Set<String> items = loadLockedItems();
        items.add(maMon);
        saveLockedItems(items);
    }

    // Hàm XÓA món khỏi danh sách khóa (Tương ứng với nút ON)
    public static void removeItem(String maMon) {
        Set<String> items = loadLockedItems();
        items.remove(maMon);
        saveLockedItems(items);
    }

    // Hàm lưu danh sách xuống file .txt
    private static void saveLockedItems(Set<String> items) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String id : items) {
                writer.println(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}