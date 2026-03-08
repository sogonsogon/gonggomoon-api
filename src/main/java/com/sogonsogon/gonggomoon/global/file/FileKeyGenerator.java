package com.sogonsogon.gonggomoon.global.file;

import java.util.UUID;

public class FileKeyGenerator {

    public static String generate(String originalFileName) {
        String extension = getExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();

        if (extension.isEmpty()) {
            return uuid;
        }

        return uuid + "." + extension;
    }

    private static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
