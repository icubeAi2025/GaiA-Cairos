package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview;

import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DesignReviewDtoImpl implements DesignReviewDto {

    @Override
    public DsgnPhase toDsgnPhase(Map<String, ?> map) {
        if ( map == null ) {
            return null;
        }

        DsgnPhase dsgnPhase = new DsgnPhase();

        for ( java.util.Map.Entry<String, ?> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            dsgnPhase.put( key, value );
        }

        return dsgnPhase;
    }
}
