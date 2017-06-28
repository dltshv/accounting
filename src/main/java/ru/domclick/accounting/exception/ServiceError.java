package ru.domclick.accounting.exception;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by dmitry on 27.06.17
 */
@JsonRootName("error")
public class ServiceError {

    private Integer code;
    private String msg;

    public ServiceError(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
