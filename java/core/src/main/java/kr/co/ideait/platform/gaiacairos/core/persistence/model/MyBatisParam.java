package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;

@Getter
@Setter
@Slf4j
public class MyBatisParam extends MybatisPageable {
    Limit limit;
    HashMap<String,String> order;

    String dltYn;
    String rgstrId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rgstDt;
    String chgId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime chgDt;
    String dltId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dltDt;

    public void setLimit(Integer limitParam, Integer offsetParam){
        limit = new Limit(limitParam,offsetParam);
    }

    public void setOrder(String column, String orderType){
        if(order == null){
            order = new HashMap<String,String>();
        }
        if (column == null || column.isEmpty()){
            return;
        }
        //snake_case
        if (column.matches("[a-z0-9_]+")) {
            order.put(column, orderType);
        }
        //camelCase
        else {
            String regex1 = "([a-z0-9])([A-Z])";
            String regex2 = "([A-Z])([A-Z][a-z])";

            String computedColumn = column.replaceAll(regex2, "$1_$2")
                    .replaceAll(regex1, "$1_$2")
                    .toLowerCase();
            order.put(computedColumn,orderType);
        }
    }

    public void reset(){
        Class<?> clazz = this.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                // static 또는 final 필드는 제외 (로그 필드 포함)
                int modifiers = field.getModifiers();
                if (java.lang.reflect.Modifier.isStatic(modifiers) ||
                        java.lang.reflect.Modifier.isFinal(modifiers)) {
                    continue;
                }
                Class<?> type = field.getType();
                try {
                    if (type.isPrimitive()) {
                        if (type == boolean.class) field.setBoolean(this, false);
                        else if (type == byte.class) field.setByte(this, (byte)0);
                        else if (type == short.class) field.setShort(this, (short)0);
                        else if (type == int.class) field.setInt(this, 0);
                        else if (type == long.class) field.setLong(this, 0L);
                        else if (type == float.class) field.setFloat(this, 0f);
                        else if (type == double.class) field.setDouble(this, 0d);
                        else if (type == char.class) field.setChar(this, '\u0000');
                    } else {
                        // 객체 타입은 null
                        field.set(this, null);
                    }
                } catch (IllegalAccessException e) {
                    log.error("{} 객체 초기화 실패 : {}",clazz.getName(),e.getMessage());
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
