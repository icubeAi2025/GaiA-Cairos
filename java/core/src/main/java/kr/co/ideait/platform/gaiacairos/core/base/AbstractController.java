package kr.co.ideait.platform.gaiacairos.core.base;

import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractController extends AbstractBase {

    @Autowired
    protected SystemLogComponent systemLogComponent;
}
