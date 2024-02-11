package com.dyspersja;

import java.io.File;

public class SourceFileManager {
    
    public String retrieveFilePath(String[] args) {
        if (args.length == 0) throw new IllegalArgumentException("No filePath in command-line arguments provided.");
        return args[0];
    }

    public File retrieveFileFromFilePath(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) throw new IllegalArgumentException("FilePath provided in command-line arguments is incorrect.");
        return file;
    }

    public String retrieveFileName(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex);
        }
        return filename;
    }

    public String retrieveFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(dotIndex + 1);
        }
        return null;
    }
}
