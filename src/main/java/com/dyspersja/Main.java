package com.dyspersja;

import java.io.File;

public class Main {

    private static final long CHUNK_FILE_SIZE = 25 * 1024 * 1024;

    public static void main(String[] args) {
        SourceFileManager sourceFileManager = new SourceFileManager();

        String sourceFilePath = sourceFileManager.retrieveFilePath(args);
        File sourceFile = sourceFileManager.retrieveFileFromFilePath(sourceFilePath);

        ChunkFilesManager chunkFilesManager = new ChunkFilesManager();
        System.out.println(chunkFilesManager.getNumberOfChunks(CHUNK_FILE_SIZE, sourceFile.length()));
    }
}
