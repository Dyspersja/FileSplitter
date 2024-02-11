package com.dyspersja;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    public byte[][] generateChunkFileHeaders(
            String[] chunkFileNames,
            String sourceFileName,
            String sourceFileExtension
    ) {
        byte[] sourceFileNameBytes = sourceFileName.getBytes(StandardCharsets.UTF_8);
        if (sourceFileNameBytes.length > FILE_NAME_SIZE) throw new IllegalArgumentException("Filename is to long");

        byte[] sourceFileExtensionBytes = sourceFileExtension.getBytes(StandardCharsets.UTF_8);
        if (sourceFileExtensionBytes.length > FILE_EXTENSION_SIZE) throw new IllegalArgumentException("File extension is to long");

        ByteBuffer[] fileBuffers = new ByteBuffer[chunkFileNames.length];
        for (int i = 0; i < fileBuffers.length; i++) {
            fileBuffers[i] = i == 0
                    ? ByteBuffer.allocate(FIRST_CHUNK_HEADER)
                    : ByteBuffer.allocate(CHUNK_HEADER);

            byte[] nextChunkFileName = i != (fileBuffers.length - 1)
                    ? hexStringToByteArray(chunkFileNames[i+1])
                    : hexStringToByteArray(chunkFileNames[0]);

            fileBuffers[i].put(nextChunkFileName);
            fileBuffers[i].putInt(i+1);
        }

        fileBuffers[0].put(new byte[FILE_NAME_SIZE - sourceFileNameBytes.length]);
        fileBuffers[0].put(sourceFileNameBytes);

        fileBuffers[0].put(new byte[FILE_EXTENSION_SIZE - sourceFileExtensionBytes.length]);
        fileBuffers[0].put(sourceFileExtensionBytes);

        byte[][] chunkFileHeaders = new byte[fileBuffers.length][];
        for (int i = 0; i < fileBuffers.length; i++) {
            fileBuffers[i].flip();
            chunkFileHeaders[i] = new byte[fileBuffers[i].remaining()];
            fileBuffers[i].get(chunkFileHeaders[i]);
        }

        return chunkFileHeaders;
    }

    private byte[] hexStringToByteArray(String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return byteArray;
    }
}
