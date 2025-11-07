package kr.co.ideait.platform.gaiacairos.web.config;

import com.fasterxml.jackson.databind.JavaType;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.web.config.request.filter.XssFilter;
import kr.co.ideait.platform.gaiacairos.core.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Configuration
public class MessageConverterConfig extends MappingJackson2HttpMessageConverter {
    private final HttpServletRequest request;

    @Autowired
    public MessageConverterConfig(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException {
        JavaType javaType = this.getJavaType(type, contextClass);

        String strBody = StringHelper.getBody(inputMessage.getBody());
//        return this.getObjectMapper().readValue(StringHelper.stripXSS(strBody), javaType);
//        return this.getObjectMapper().readValue(StringHelper.getSafeParamDataForJson(strBody), javaType);
        return this.getObjectMapper().readValue(strBody, javaType);
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException {
        String message = getObjectMapper().writeValueAsString(object);

        OutputStream output = outputMessage.getBody();

        if (XssFilter.excludeUrl(request)) {
            output.write(message.getBytes(StandardCharsets.UTF_8));
        } else {
            output.write(StringHelper.getSafeParamDataForJson(message).getBytes(StandardCharsets.UTF_8));
        }

        output.flush();
    }
}
