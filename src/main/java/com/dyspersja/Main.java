package com.dyspersja;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    private static final ApplicationMode MODE = ApplicationMode.MERGE;

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
        SourceFileManager sourceFileManager = new SourceFileManager();
        String chunkFilePath = sourceFileManager.retrieveFilePath(args);

        ChunkFilesManager chunkFilesManager = new ChunkFilesManager();

        List<File> chunkFileList = new ArrayList<>();
        File lastChunkFile = sourceFileManager.retrieveFileFromFilePath(chunkFilePath);

        do {
            File nextChunkFile = chunkFilesManager.getNextFileFromChunkFile(lastChunkFile);
            chunkFileList.add(lastChunkFile);
            lastChunkFile = nextChunkFile;
        } while (!lastChunkFile.equals(chunkFileList.get(0)));

        int chunkFileListOffset = chunkFilesManager.getChunkFileOrder(chunkFileList.get(0));
        Collections.rotate(chunkFileList, chunkFileListOffset - 1);

        String fileName = chunkFilesManager.getFileNameFromChunkFile(chunkFileList.get(0));
        String extension = chunkFilesManager.getExtensionFromChunkFile(chunkFileList.get(0));
    }
}
