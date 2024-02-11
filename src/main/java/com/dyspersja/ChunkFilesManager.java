package com.dyspersja;

import java.util.UUID;

public class ChunkFilesManager {

    private static final int FILE_NAME_SIZE = 512 / 8;
    private static final int FILE_EXTENSION_SIZE = 64 / 8;

    private static final int NEXT_FILE_NAME_SIZE = 128 / 8;
    private static final int FILE_ORDER_SIZE = 32 / 8;

    private static final int CHUNK_HEADER = NEXT_FILE_NAME_SIZE + FILE_ORDER_SIZE;
    private static final int FIRST_CHUNK_HEADER = CHUNK_HEADER + FILE_NAME_SIZE + FILE_EXTENSION_SIZE;

    private static final long CHUNK_FILE_SIZE = 25 * 1024 * 1024;

    public int getNumberOfChunks(long sourceFileSize) {
        sourceFileSize -= (CHUNK_FILE_SIZE - FIRST_CHUNK_HEADER);
        int numberOfChunks = 1;

        while(sourceFileSize > 0) {
            sourceFileSize -= (CHUNK_FILE_SIZE - CHUNK_HEADER);
            numberOfChunks++;
        }
        return numberOfChunks;
    }

    public String[] generateChunkFileNames(int numberOfChunks) {
        String[] chunkFileNames = new String[numberOfChunks];

        for(int i=0;i<chunkFileNames.length;i++) {
            chunkFileNames[i] = UUID.randomUUID()
                    .toString()
                    .replace("-","");
        }
        return chunkFileNames;
    }
}
