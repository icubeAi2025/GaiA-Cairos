package kr.co.ideait.platform.gaiacairos.web.config.request;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CachingBodyRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    public CachingBodyRequestWrapper(HttpServletRequest request) {
        super(request);
        cacheInputStream(request);
    }

    private void cacheInputStream(HttpServletRequest request) {
        try {
            cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            cachedBody = new byte[0];
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedServletInputStream(cachedBody);
    }

    private static class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public CachedServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}
