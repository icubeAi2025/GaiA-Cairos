package kr.co.ideait.platform.gaiacairos.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

//[NM] Class kr.co.ideait.platform.gaiacairos.core.exception.C3RSAXException is not derived from an Exception, even though it is named as such
//Bug type NM_CLASS_NOT_EXCEPTION (click for details)
//In class kr.co.ideait.platform.gaiacairos.core.exception.C3RSAXException
//At C3RSAXException.java:[lines 9-56]
public class C3RSAXErrorHandler implements ErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(C3RSAXErrorHandler.class);

    // This method is called in the event of a recoverable error
    @Override
    public void error(SAXParseException e) throws SAXException {
        throw new SAXException("다음과 같이 XML형식이 잘못되었습니다. " + convertMessage("SAX Error", e));
    }

    //	cvc-complex-type.2.4.a: Invalid content was found starting with element '공사정보1'. One of '{공사정보}' is expected.
    // This method is called in the event of a non-recoverable error
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw new SAXException(convertMessage("SAX Fatal Error", e));
    }

    // This method is called in the event of a warning
    @Override
    public void warning(SAXParseException e) throws SAXException {
        throw new SAXException(convertMessage("SAX Warning", e));
    }

    // Dump a log record to a logger
    private String convertMessage(String message, SAXParseException e) {
        // Get details
        int line = e.getLineNumber();
        String errorMessage = e.getMessage();
        if(errorMessage != null){
            String causeMessage = substring(errorMessage, errorMessage.lastIndexOf("{")+1, errorMessage.length());
            // Append details to message
            message = "[ " + causeMessage + " line=" + line + " ]";
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("************************** C3RSAXErrorHandler ************************** \n[" + message + "]");
            }
        }

        // Log the message
        return message;
    }

    public static String substring(String src, int start, int end) {
        if (src == null || src.isEmpty() || start > src.length() || start > end || start < 0) {
            return "";
        }

        if (end > src.length()) {
            end = src.length();
        }

        return src.substring(start, end);
    }
}
