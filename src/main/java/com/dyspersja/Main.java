package com.dyspersja;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        SourceFileManager sourceFileManager = new SourceFileManager();
        String filePath = sourceFileManager.retrieveFilePath(args);
        File file = sourceFileManager.retrieveFileFromFilePath(filePath);
    }
}
