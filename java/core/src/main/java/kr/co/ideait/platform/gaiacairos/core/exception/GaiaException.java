package kr.co.ideait.platform.gaiacairos.core.exception;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class GaiaException extends Exception {

    ErrorType errorType;

    Throwable throwable;

    public GaiaException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public GaiaException(ErrorType errorType, Throwable throwable) {
        super(throwable == null ? "throwable is null" : throwable.getMessage(), throwable);
        this.errorType = errorType;
        this.throwable = throwable;
    }

    public GaiaException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public GaiaException(ErrorType errorType, String message, Throwable throwable) {
        super(message, throwable);
        this.errorType = errorType;
        this.throwable = throwable;
    }

    public GaiaException(ErrorType errorType, String message, String detailMessage) {
        super(message);
        this.errorType = errorType;
        this.detailMessage = detailMessage;
    }

    public GaiaException(ErrorType errorType, String message, String detailMessage, Throwable throwable) {
        super(message, throwable);
        this.errorType = errorType;
        this.detailMessage = detailMessage;
        this.throwable = throwable;
    }

    public GaiaException(Exception exception) {
        super(ErrorType.getType(exception).getMessage());
        this.throwable = exception;
        this.errorType = ErrorType.getType(exception);
    }

    public static GaiaException of(Exception exception) {
        ErrorType errorType = ErrorType.getType(exception);
        String message = getCommonExceptionMessage(exception, errorType.getMessage());

        StackTraceElement[] stackTraceList = exception.getStackTrace();
        StackTraceElement stackTraceElement = stackTraceList[0];
        StringBuffer errorCause = new StringBuffer();
        errorCause.append("class: ").append(stackTraceElement.getClassName());
        errorCause.append(" method: ").append(stackTraceElement.getMethodName());
        errorCause.append(" line: ").append(stackTraceElement.getLineNumber());
//        errorCause.append("\nmessage: ").append(errorType.getMessage().equals(message) ? exception.getMessage() : message);
        errorCause.append("\nmessage: ").append(exception.getMessage());

        return new GaiaException(errorType, message, errorCause.toString());
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    public HttpStatusCode getStatus() {
        return this.errorType.getStatus();
    }

    public Result getNokResult() {
        return Result.nok(this.errorType, getMessage());
    }

    /**
     * Exception Message는 보안상 노출되어서는 안되는 정보가 포함될 수 있어 특정 Exception에 대해서만 처리합니다.
     */
    private static String getCommonExceptionMessage(Exception exption, String defaultMessage) {
        return switch (exption) {
            // TODO:여러개의 메시지를 전달해야 함.
            case MethodArgumentNotValidException ex -> ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            default -> defaultMessage;
        };
    }
}
