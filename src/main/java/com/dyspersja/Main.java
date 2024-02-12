package com.dyspersja;

import java.io.File;

public class Main {

    private static final ApplicationMode MODE = ApplicationMode.SPLIT;

    public static void main(String[] args) {
        switch (MODE) {
            case SPLIT -> splitSourceFile(args);
            case MERGE -> mergeChunkFiles(args);
        }
    }

    private static void splitSourceFile(String[] args) {
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

        chunkFilesManager.createChunkFiles(
                chunkFileNames,
                chunkFileHeaders,
                sourceFile
        );
    }

    private static void mergeChunkFiles(String[] args) {
        throw new RuntimeException("Not Implemented");
    }
}
