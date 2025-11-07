package kr.co.ideait.platform.gaiacairos.core.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spring.application")
public class ApplicationProp {
    PlatformType type = PlatformType.ETC;

    public PlatformType getSystemType() {
        return type;
    }

    public String getName() {
        return type.name();
    }

    public void setName(String type) {
        this.type = PlatformType.from(type);
    }
}
