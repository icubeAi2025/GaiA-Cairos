package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;

@Slf4j
public abstract class MapDto extends HashMap<String, Object> {

    List<String> members;

    public MapDto() {
        //[CT] Exception thrown in class kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto at new kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto() will leave the constructor. The object under construction remains partially initialized and may be vulnerable to Finalizer attacks.
        List<String> names = new ArrayList<>();
         try {
             Field[] fields = this.getClass().getDeclaredFields();
             for (Field field : fields) {
                 names.add(field.getName());
             }
         } catch(SecurityException e) {
             log.error("Security Exception : {}",e.getMessage());
         }
         this.members = names;
    }

    @Override
    public Object put(String key, Object value) {
        key = CaseUtils.toCamelCase(key, false, '_');
        members.stream().filter(key::equals).forEach(k -> {
            super.put(k, value);
        });
        return value;
    }

}
