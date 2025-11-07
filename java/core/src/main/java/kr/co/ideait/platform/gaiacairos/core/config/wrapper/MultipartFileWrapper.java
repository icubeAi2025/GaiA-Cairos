package kr.co.ideait.platform.gaiacairos.core.config.wrapper;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class MultipartFileWrapper implements MultipartFile {

    private final MultipartFile delegate;
    private final String originalFilename;
    private final String name;
    private final String contentType;

    public MultipartFileWrapper(MultipartFile delegate, String originalFilename, String name, String contentType) {
        this.delegate = delegate;
        this.originalFilename = originalFilename;
        this.name = name;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public long getSize() {
        return delegate.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return delegate.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException {
        delegate.transferTo(dest);
    }
}
