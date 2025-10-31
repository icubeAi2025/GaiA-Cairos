package kr.co.ideait.platform.gaiacairos.core.type;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum ActivityType {
    MILESTONE("SM", "Milestone"),
    START_MILESTONE("SM", "Start Milestone"),
    FINISH_MILESTONE("FN", "Finish Milestone"),
    TASK_DEPENDENT("TR", "Task Dependent"),
    RESOURCE_DEPENDENT("RR", "Resource Dependent"),
    LEVEL_OF_EFFORT("NL", "Level of Effort"),
    WBS_SUMMARY("WB", "WBS Summary"),
    NULL("", null);

    private final String gaiaType;
    private final String primaveraType;

    // Map 캐싱 - primavera -> gaia
    private static final Map<String, String> primaveraToGaiaMap = new HashMap<>();
    // Map 캐싱 - gaia -> primavera
    private static final Map<String, String> gaiaToPrimaveraMap = new HashMap<>();


    static {
        for (ActivityType type : ActivityType.values()) {
            if (type.primaveraType != null && !type.primaveraType.isEmpty()) {
                primaveraToGaiaMap.put(type.primaveraType, type.gaiaType);
            }
            if (type.gaiaType != null && !type.gaiaType.isEmpty()) {
                gaiaToPrimaveraMap.put(type.gaiaType, type.primaveraType);
            }
        }
    }

    ActivityType(String gaiaType, String primaveraType) {
        this.gaiaType = gaiaType;
        this.primaveraType = primaveraType;
    }

    public String getGaiaType() { return gaiaType; }
    public String getPrimaveraType() { return primaveraType; }

    public static String getGaiaTypeByPrimaveraType(String primaveraType) {
        return primaveraToGaiaMap.getOrDefault(primaveraType, null);
    }

    public static String getPrimaveraTypeByGaiaType(String gaiaType) {
        return gaiaToPrimaveraMap.getOrDefault(gaiaType, null);
    }

}

