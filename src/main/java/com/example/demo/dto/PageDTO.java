package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {

    private int page;
    private int size;
    private long totalElements;
    private List<T> data;

}
