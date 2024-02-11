package com.dyspersja;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        SourceFileManager sourceFileManager = new SourceFileManager();
        String sourceFilePath = sourceFileManager.retrieveFilePath(args);
        File sourceFile = sourceFileManager.retrieveFileFromFilePath(sourceFilePath);

        ChunkFilesManager chunkFilesManager = new ChunkFilesManager();
        int numberOfChunks = chunkFilesManager.getNumberOfChunks(sourceFile.length());
        String[] chunkFileNames = chunkFilesManager.generateChunkFileNames(numberOfChunks);
        byte[][] chunkFileHeaders = chunkFilesManager.generateChunkFileHeaders(
                chunkFileNames,
                sourceFileManager.retrieveFileName(sourceFile.getName()),
                sourceFileManager.retrieveFileExtension(sourceFile.getName())
        );
    }
}
