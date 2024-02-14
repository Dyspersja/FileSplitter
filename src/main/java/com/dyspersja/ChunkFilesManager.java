package com.dyspersja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

        byte[] sourceFileExtensionBytes = sourceFileExtension != null
                ? sourceFileExtension.getBytes(StandardCharsets.UTF_8)
                : new byte[0];
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

    public void createChunkFiles(
            String[] chunkFileNames,
            byte[][] chunkFileHeaders,
            File sourceFile
    ) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileChannel inputChannel = fis.getChannel()) {
            for (int i = 0; i < chunkFileNames.length; i++) {

                ByteBuffer fileBuffer = ByteBuffer.allocate((int) CHUNK_FILE_SIZE);

                fileBuffer.put(chunkFileHeaders[i]);
                inputChannel.read(fileBuffer);
                fileBuffer.flip();

                try (FileOutputStream fos = new FileOutputStream(chunkFileNames[i]);
                     FileChannel outputChannel = fos.getChannel()) {
                    outputChannel.write(fileBuffer);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while creating chunk files");
        }
    }

    public File getNextFileFromChunkFile(File chunkFile) {
        try (FileInputStream fis = new FileInputStream(chunkFile)) {

            byte[] nextChunkFileNameBytes = new byte[NEXT_FILE_NAME_SIZE];
            int bytesRead = fis.read(nextChunkFileNameBytes);

            if(bytesRead != NEXT_FILE_NAME_SIZE) throw new RuntimeException("Couldn't read file " + chunkFile.getName());

            String nextChunkFileName = byteArrayToHexString(nextChunkFileNameBytes);

            return new File(chunkFile.getParentFile(), nextChunkFileName);
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while searching for chunk file " + chunkFile.getName());
        }
    }

    public int getChunkFileOrder(File chunkFile) {
        try (FileInputStream fis = new FileInputStream(chunkFile)) {

            long bytesSkipped = fis.skip(NEXT_FILE_NAME_SIZE);

            if(bytesSkipped != NEXT_FILE_NAME_SIZE) throw new RuntimeException("Couldn't skip bytes in file " + chunkFile.getName());

            byte[] orderBytes = new byte[FILE_ORDER_SIZE];
            int bytesRead = fis.read(orderBytes);

            if(bytesRead != FILE_ORDER_SIZE) throw new RuntimeException("Couldn't read file " + chunkFile.getName());

            return byteArrayToInt(orderBytes);
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while getting order for chunk file " + chunkFile.getName());
        }
    }

    private byte[] hexStringToByteArray(String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return byteArray;
    }

    private String byteArrayToHexString(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();

        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().trim();
    }

    private int byteArrayToInt(byte[] byteArray) {
        return ((byteArray[0] & 0xFF) << 24) |
                ((byteArray[1] & 0xFF) << 16) |
                ((byteArray[2] & 0xFF) << 8) |
                (byteArray[3] & 0xFF);
    }
}
