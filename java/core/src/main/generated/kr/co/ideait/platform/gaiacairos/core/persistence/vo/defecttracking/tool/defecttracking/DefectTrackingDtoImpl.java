package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking;

import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DefectTrackingDtoImpl implements DefectTrackingDto {

    @Override
    public DfccyPhase toDfccyPhase(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        DfccyPhase dfccyPhase = new DfccyPhase();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            dfccyPhase.put( key, value );
        }

        return dfccyPhase;
    }
}
