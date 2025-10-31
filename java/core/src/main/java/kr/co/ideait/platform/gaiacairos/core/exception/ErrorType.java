package kr.co.ideait.platform.gaiacairos.core.exception;

import org.postgresql.util.PSQLException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

/**
 * [CODE]
 * 0: success
 * 1~999: Common Http Status
 * 1000~9999: Business Error
 */
public enum ErrorType {

    /* @formatter:off */
    /* Common HTTP Status - CLIENT_ERROR */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase()),
    FORBIDDEN(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase()),
    NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase()),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase()),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, HttpStatus.NOT_ACCEPTABLE.value(), HttpStatus.NOT_ACCEPTABLE.getReasonPhrase()),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, HttpStatus.PAYLOAD_TOO_LARGE.value(), HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase()),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase()),

    /* Common HTTP Status - SERVER_ERROR */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase()),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()),

    /* 600 ~ 999 Critical Server Error */
    DATABSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 600, "database error"),

    /* 1000 ~ 9998 Biz Error */
    NO_DATA(HttpStatus.OK, 1000, "no data"),
    LOGIN_ERROR(HttpStatus.OK, 1001, "login error"),
    NOT_SELECTED(HttpStatus.OK, 1002, "must select project and contract"),
    DUPLICATION_DATA(HttpStatus.OK, 1003, "duplication data"),
    INVAILD_INPUT_DATA(HttpStatus.OK, 1004, "Invaild input data"),
    NO_USER_DATA(HttpStatus.OK, 1005, "No User Data"),


    ACCESS_DENIED(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "access denied"),
    INTERFACE(HttpStatus.INTERNAL_SERVER_ERROR, 9998, "interface failure"),

    /* ETC */
    ETC(HttpStatus.INTERNAL_SERVER_ERROR, 9997, "system error");
    /* @formatter:on */

    HttpStatus status;
    int code;
    String message;

    ErrorType(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorType getType(Exception exception) {
        return switch (exception) {
            case HttpRequestMethodNotSupportedException ex -> ErrorType.METHOD_NOT_ALLOWED;
            case HttpMediaTypeNotSupportedException ex -> ErrorType.UNSUPPORTED_MEDIA_TYPE;
            case HttpMediaTypeNotAcceptableException ex -> ErrorType.NOT_ACCEPTABLE;
            case MissingPathVariableException ex -> ErrorType.BAD_REQUEST;
            case MissingServletRequestParameterException ex -> ErrorType.BAD_REQUEST;
            case MissingServletRequestPartException ex -> ErrorType.BAD_REQUEST;
            case ServletRequestBindingException ex -> ErrorType.BAD_REQUEST;
            case MethodArgumentNotValidException ex -> ErrorType.BAD_REQUEST;
            case HandlerMethodValidationException ex -> ErrorType.BAD_REQUEST;
            case ErrorResponseException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case NullPointerException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case IndexOutOfBoundsException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case ConversionNotSupportedException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case TypeMismatchException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case HttpMessageNotReadableException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case HttpMessageNotWritableException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case MethodValidationException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case BindException ex -> ErrorType.INTERNAL_SERVER_ERROR;
            case NoHandlerFoundException ex -> ErrorType.NOT_FOUND;
            case NoResourceFoundException ex -> ErrorType.NOT_FOUND;
            case AsyncRequestTimeoutException ex -> ErrorType.SERVICE_UNAVAILABLE;
            case MaxUploadSizeExceededException ex -> ErrorType.PAYLOAD_TOO_LARGE;
            case DataIntegrityViolationException ex -> ErrorType.DATABSE_ERROR;
            case PSQLException ex -> ErrorType.DATABSE_ERROR;
            case AuthenticationException ex -> ErrorType.UNAUTHORIZED;
            case ExpiredJwtException ex -> ErrorType.UNAUTHORIZED;
            case SignatureException ex -> ErrorType.UNAUTHORIZED;
            case AuthorizationDeniedException ex -> ErrorType.FORBIDDEN;
            case AccessDeniedException ex -> ErrorType.FORBIDDEN;
            case GaiaBizException ex -> ex.getErrorType();
            default -> ErrorType.ETC;
        };
    }

}
