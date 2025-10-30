package kr.co.ideait.platform.gaiacairos.web.config.request;


import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
public class XssFilterRequestBodyWrapper extends HttpServletRequestWrapper {
    private byte[] b;

    public XssFilterRequestBodyWrapper(HttpServletRequest request) {
        super(request);

//        try {
//            Collection<Part> parts = request.getParts();
//
//            for (Part part : parts) {
//                if (part.getHeader("Content-Disposition").contains("filename") && part.getSize() > 0) {
//                } else {
//                    String body = getBody(part.getInputStream());
//                    b = StringHelper.stripXSS( body ).getBytes(StandardCharsets.UTF_8);
//                }
//            }
//        } catch (ServletException | IOException e) {
//            log.error(e.getMessage(), e);
//            throw new RuntimeException(e);
//        } finally {
//            log.info("reqBody 최종: {}");
//        }

        String contentType = request.getContentType();

        try {
            if (StringUtils.contains(contentType, "application/json")) {
                String body = StringHelper.getBody(request.getInputStream());
                b = StringHelper.stripXSS( body ).getBytes(StandardCharsets.UTF_8);
            } else {
                if (request.getParts() == null) {
                    String body = StringHelper.getBody(request.getInputStream());
                    b = StringHelper.stripXSS( body ).getBytes(StandardCharsets.UTF_8);
                }
            }
        } catch (ServletException | IOException e) {
//            throw new GaiaBizException(e);
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bis = new ByteArrayInputStream(b);

        return new ServletInputStreamImpl(bis);
    }

    public class ServletInputStreamImpl extends ServletInputStream {
        private InputStream is;

        public ServletInputStreamImpl(InputStream bis){
            is = bis;
        }

        @Override
        public int read() throws IOException {
            return is.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return is.read(b);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}