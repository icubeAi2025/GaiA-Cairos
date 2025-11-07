package kr.co.ideait.platform.gaiacairos.core.persistence;

public class DaoUtils {

    public static String getMyBatisId(Object object, String id) {
        return getMyBatisId(object.getClass(), id);
    }

    public static String getMyBatisId(Class<?> clazz, String id) {
        return clazz.getPackage().getName() + "." + id;
    }
}
