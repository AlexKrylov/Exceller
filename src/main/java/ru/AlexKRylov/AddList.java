package ru.AlexKRylov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AddList {
    public static ArrayList<String> fileList() {
        ArrayList<String> list = new ArrayList<>();
        String path = System.getProperty("user.dir") + "/files";
        File file = new File(path);
        File[] arr = file.listFiles();
        assert arr != null;
        for (File anArr : arr) {
            list.add(anArr.getPath());
        }
        return list;
    }

    public static ArrayList<String> connectList() {
        ArrayList<String> conList = new ArrayList<>();
        String path = System.getProperty("user.dir") + "/connect.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String str;
            while ((str = reader.readLine()) != null) {
                if (!str.isEmpty()) {
                    conList.add(str);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conList;
    }
}
