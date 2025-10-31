package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("output")
public class MybatisOutput extends HashMap<String, Object> {
    void toCamelCase(){
        Set<Entry<String,Object>> entrySet = this.entrySet();
        for(Entry<String,Object> entry : entrySet){
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.matches("[a-z0-9_]+")) {
                // snake_case → camelCase
                String regex = "_([a-z0-9])";

                Pattern p = Pattern.compile(regex);
                String computedKey = p.matcher(key.toLowerCase())
                        .replaceAll(match -> match.group(1).toUpperCase());
                this.put(computedKey, value);
                this.remove(key);
            }
        }
    }
    void toSnakeCase(){
        Set<Entry<String,Object>> entrySet = this.entrySet();
        for(Entry<String,Object> entry : entrySet){
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!key.matches("[a-z0-9_]+")) {
                String regex1 = "([a-z0-9])([A-Z])";
                String regex2 = "([A-Z])([A-Z][a-z])";

                String computedKey = key.replaceAll(regex2, "$1_$2")
                        .replaceAll(regex1, "$1_$2")
                        .toLowerCase();
                this.put(computedKey,value);
                this.remove(key);
            }
        }
    }

}
