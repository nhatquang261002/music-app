package com.example.musicapp.data;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;
public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        List<String> list = new ArrayList<>();
        String[] array = value.split(",");
        for (String item : array) {
            list.add(item);
        }
        return list;
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        StringBuilder value = new StringBuilder();
        for (String item : list) {
            value.append(item);
            value.append(",");
        }
        return value.toString();
    }
}
