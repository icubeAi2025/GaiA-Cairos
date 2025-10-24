package kr.co.ideait.platform.gaiacairos.web.config.request;


import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import kr.co.ideait.iframework.ThreadContext;
import kr.co.ideait.platform.gaiacairos.core.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class XssFilterRequestWrapper extends HttpServletRequestWrapper {
    ThreadContext threadContext = new ThreadContext();

    public XssFilterRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String[] getParameterValues(String parameter) {
        String values[] = super.getParameterValues(parameter);

        if (values == null) return null;

        for (int i = 0; i < values.length; i++) {
            if(values[i] != null) {
                values[i] = StringHelper.getSafeParamData(values[i]);
            } else {
                values[i] = null;
            }
        }
        return values;
    }

    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);

        if (value == null) return null;

        return StringHelper.getSafeParamData(value);
    }

    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> newValueMap = (Map<String, String[]>)threadContext.get();

        if (newValueMap == null) {
            newValueMap = Maps.newHashMap();

            Map<String, String[]> valueMap = super.getParameterMap();

            for (Map.Entry entry : valueMap.entrySet ()) {
                String[] values = (String[])entry.getValue();

                for (int i = 0; i < values.length; ++i) {
                    if (values[i] != null) {
                        values[i] = StringHelper.getSafeParamData(values[i]);
                    } else {
                        values[i] = null;
                    }
                }

                newValueMap.put((String)entry.getKey(), values);
            }

            threadContext.set(newValueMap);
        }

        return newValueMap;
    }
}
