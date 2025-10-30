package kr.co.ideait.platform.gaiacairos.core.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants.ComponentModel;

@MapperConfig(componentModel = ComponentModel.SPRING, nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface GlobalMapperConfig {

}
