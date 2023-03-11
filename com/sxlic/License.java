package com.sxlic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class License {
    
    public static final byte[] magic_bytes = new byte[]{83, 88, 108, 1};
    
    public static String generate(SequentialProperties sprop) {
        try {
            
            // Store properties
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            sprop.store(byteArrayOutputStream, "SpectX License");
            byte[] inflated_bytes = byteArrayOutputStream.toByteArray();
            
            // Fake signature
            
            byte[] signature = new byte[256];
            
            // Concatenate signature and properties
            
            byte[] concatenated = new byte[signature.length + inflated_bytes.length];
            System.arraycopy(signature, 0, concatenated, 0, signature.length);
            System.arraycopy(inflated_bytes, 0, concatenated, signature.length, inflated_bytes.length);
            
            // Deflate
            
            Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
            deflater.setInput(concatenated);
            deflater.finish();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(concatenated.length);
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            deflater.end();
            byte[] compressedData = outputStream.toByteArray();
            
            // Concatenate magic number and deflated data
            
            byte[] concatenated_2 = new byte[magic_bytes.length + compressedData.length];
            System.arraycopy(magic_bytes, 0, concatenated_2, 0, magic_bytes.length);
            System.arraycopy(compressedData, 0, concatenated_2, magic_bytes.length, compressedData.length);
            
            // Base64 encode
            
            String base64 = Base64.encode(concatenated_2);
            
            // Build PEM formatted string
            
            StringBuilder pemFormatted = new StringBuilder();
            pemFormatted.append("-----BEGIN SPECTX LICENSE-----\n");
            int lineLength = 64;
            int offset = 0;
            while (offset < base64.length()) {
                int length = Math.min(lineLength, base64.length() - offset);
                String lineData = base64.substring(offset, offset + length);
                pemFormatted.append(lineData);
                pemFormatted.append("\n");
                offset += lineLength;
            }
            pemFormatted.append("-----END SPECTX LICENSE-----");
            
            return pemFormatted.toString();
            
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return null;
    }
    
    public static SequentialProperties read(String license) {
        try {
            byte[] licBytes = license.getBytes();
            byte[] signature = new byte[256];
            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(licBytes);
        
            // Remove BEGIN and END lines
            
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream)inputStream, StandardCharsets.UTF_8));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                if ((string = string.trim()).isEmpty() || string.charAt(0) == '-') continue;
                stringBuilder.append(string);
            }
            byte[] deflated_bytes = Base64.decode(stringBuilder.toString());
           
            // Check for Magic number
            
            for (int i = 0; i < 4; ++i) {
                if (deflated_bytes.length >= i + 1 && deflated_bytes[i] == magic_bytes[i]) continue;
                throw new EOFException("Invalid magic");
            }
            
            // Inflate
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(deflated_bytes, 4, deflated_bytes.length - 4); // Avoid magic number
            
            Inflater inflater = new Inflater(true);
            InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream, inflater);
            // First 256 bytes are the signature
            inflaterInputStream.read(signature, 0, signature.length);
            // Remaining bytes are the properties
            int aux_nPipe;
            byte[] byArray = new byte[1024];
            while ((aux_nPipe = inflaterInputStream.read(byArray)) > -1) {
                byteArrayOutputStream.write(byArray, 0, aux_nPipe);
            }
            inflater.end();
            
            byte[] inflated_bytes = byteArrayOutputStream.toByteArray(); // Does not contain the signature
            byteArrayOutputStream.close();
            
            // Load sequential properties from bytes
            
            SequentialProperties sequentialProperties = new SequentialProperties();
            ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(inflated_bytes);
            sequentialProperties.load(byteArrayInputStream2);
            byteArrayInputStream2.close();
            
            return sequentialProperties;
            
        } catch (Throwable throwable) {
            System.out.println("Error :" + throwable);
        }
        return null;
    }
}