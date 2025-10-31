package kr.co.ideait.platform.gaiacairos.comp.document.helper;

import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DocumentHelper {
    @Autowired
    DocumentService documentService;

    @Autowired
    private FileService fileService;

}
