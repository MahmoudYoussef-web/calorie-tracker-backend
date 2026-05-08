package com.caloriestracker.system.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;

public class ByteArrayMultipartFile implements MultipartFile {

    private final byte[] bytes;
    private final String originalFilename;
    private final String contentType;

    public ByteArrayMultipartFile(byte[] bytes, String originalFilename, String contentType) {
        this.bytes = bytes;
        this.originalFilename = originalFilename;
        this.contentType = contentType != null ? contentType : "image/jpeg";
    }

    @Override public String  getName()             { return "file"; }
    @Override public String  getOriginalFilename() { return originalFilename; }
    @Override public String  getContentType()      { return contentType; }
    @Override public boolean isEmpty()             { return bytes == null || bytes.length == 0; }
    @Override public long    getSize()             { return bytes != null ? bytes.length : 0; }
    @Override public byte[]  getBytes()            { return bytes; }
    @Override public InputStream getInputStream()  { return new ByteArrayInputStream(bytes); }

    @Override
    public void transferTo(File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(bytes);
        }
    }
}