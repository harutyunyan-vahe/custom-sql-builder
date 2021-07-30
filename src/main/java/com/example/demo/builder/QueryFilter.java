package com.example.demo.builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class QueryFilter {

    private Integer page;
    private Integer resultsPerPage;
    private Integer resultsCountOverride;
    private String sort;
    private Boolean ascSort;

    public QueryFilter() {
        this.page = 1;
        this.resultsPerPage = Integer.MAX_VALUE;
    }




}
