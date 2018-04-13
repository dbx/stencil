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
}
