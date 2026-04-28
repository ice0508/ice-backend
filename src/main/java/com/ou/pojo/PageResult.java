package com.ou.pojo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Long total;
    private List<?> records;

    public PageResult(Long total, List<?> records) {
        this.total = total;
        this.records = records;
    }
}
