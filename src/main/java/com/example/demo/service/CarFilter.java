package com.example.demo.service;

import com.example.demo.builder.QueryFilter;
import lombok.Data;

@Data
public class CarFilter extends QueryFilter {
    private String name;
    private Long year;
}
