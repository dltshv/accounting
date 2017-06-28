package ru.domclick.accounting.exception;

/**
 * Created by dmitry on 27.06.17
 */
public class ServiceException extends RuntimeException {
    public ServiceError error;

    public ServiceException(ServiceError error) {
        super(error.getMsg());
        this.error = error;
    }

    public ServiceException(Integer code, String msg) {
        super(msg);
        this.error = new ServiceError(code, msg);
    }
}
