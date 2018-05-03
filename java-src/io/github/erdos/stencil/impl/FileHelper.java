package io.github.erdos.stencil.impl;

import java.io.File;

public final class FileHelper {

    public static String extension(File f) {
        return extension(f.getName());
    }

    public static String extension(String filename) {
        String[] parts = filename.split("\\.");
        return parts[parts.length - 1].trim().toLowerCase();
    }

    /**
     * Returns file name without extension part
     */
    public static String removeExtension(File f) {
        String fileName = f.getName();
        if (fileName.contains(".")) {
            int loc = fileName.lastIndexOf('.');
            return fileName.substring(0, loc);
        } else
            return fileName;
    }
}
