package com.dyspersja;

public class ChunkFilesManager {

    private static final int FILE_NAME_SIZE = 512 / 8;
    private static final int FILE_EXTENSION_SIZE = 64 / 8;

    private static final int NEXT_FILE_NAME_SIZE = 128 / 8;
    private static final int FILE_ORDER_SIZE = 32 / 8;

    private static final int CHUNK_HEADER = NEXT_FILE_NAME_SIZE + FILE_ORDER_SIZE;
    private static final int FIRST_CHUNK_HEADER = CHUNK_HEADER + FILE_NAME_SIZE + FILE_EXTENSION_SIZE;

    public int getNumberOfChunks(long chunkFileSize, long sourceFileSize) {
        sourceFileSize -= (chunkFileSize - FIRST_CHUNK_HEADER);
        int numberOfChunks = 1;

        while(sourceFileSize > 0) {
            sourceFileSize -= (chunkFileSize - CHUNK_HEADER);
            numberOfChunks++;
        }
        return numberOfChunks;
    }
}
