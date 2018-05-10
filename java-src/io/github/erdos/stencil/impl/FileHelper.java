package io.github.erdos.stencil.impl;

import java.io.File;
import java.io.IOException;

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

    /**
     * Creates a directory. Recursively creates parent directories too.
     *
     * @param directory not null dir to create
     * @throws IOException              on IO error
     * @throws IllegalArgumentException when input is null or already exists
     */
    public static void forceMkdir(final File directory) throws IOException {
        if (directory == null)
            throw new IllegalArgumentException("Missing directory for forceMkdir");
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new IOException("File exists and not a directory: " + directory);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    throw new IOException("Unable to create directory " + directory);
                }
            }
        }
    }
}
