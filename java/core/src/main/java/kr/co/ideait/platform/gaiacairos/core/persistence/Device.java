package kr.co.ideait.platform.gaiacairos.core.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
public class Device {
    public final static String DEVICE_DESKTOP = "Desktop";
    public final static String DEVICE_MOBILE = "Mobile";

    @Description(name = "")
    String deviceClass;

    @Description(name = "")
    String deviceName;

    @Description(name = "")
    String deviceBrand;

    @Description(name = "")
    String operatingSystemClass;

    @Description(name = "")
    String operatingSystemName;

    @Description(name = "")
    String operatingSystemVersion;

    @Description(name = "")
    String operatingSystemVersionMajor;

    @Description(name = "")
    String operatingSystemNameVersion;

    @Description(name = "")
    String operatingSystemNameVersionMajor;

    @Description(name = "")
    String operatingSystemVersionBuild;

    @Description(name = "")
    String layoutEngineClass;

    @Description(name = "")
    String layoutEngineName;

    @Description(name = "")
    String layoutEngineVersion;

    @Description(name = "")
    String layoutEngineVersionMajor;

    @Description(name = "")
    String layoutEngineNameVersion;

    @Description(name = "")
    String layoutEngineNameVersionMajor;

    @Description(name = "")
    String agentClass;

    @Description(name = "")
    String agentName;

    @Description(name = "")
    String agentVersion;

    @Description(name = "")
    String agentVersionMajor;

    @Description(name = "")
    String agentNameVersion;

    @Description(name = "")
    String agentNameVersionMajor;

    @Description(name = "")
    String agentInformationEmail;

    @Description(name = "")
    String webviewAppName;

    @Description(name = "")
    String webviewAppVersion;

    @Description(name = "")
    String webviewAppVersionMajor;

    @Description(name = "")
    String webviewAppNameVersion;

    @Description(name = "")
    String webviewAppNameVersionMajor;

    @Description(name = "")
    String networkType;

    public static Device build(Map<String, String> map, ObjectMapper objectMapper) {
        Map<String, String> newMap = Maps.newHashMap();

        map.entrySet().forEach(entry -> {
            newMap.put(StringUtils.uncapitalize(entry.getKey()), entry.getValue());
        });

        return objectMapper.convertValue(newMap, Device.class);
    }
}
