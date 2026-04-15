package com.ou.pojo;

import lombok.Data;

@Data
public class Result {
    private Long status;
    private Object message;

    public Result() {
    }

    public Result(Long status, Object message) {
        this.status = status;
        this.message = message;
    }
}
