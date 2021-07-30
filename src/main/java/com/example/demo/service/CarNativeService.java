package com.example.demo.service;

import com.example.demo.builder.NativeGeneralService;
import com.example.demo.builder.QueryFilter;
import com.example.demo.dto.CarDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarNativeService extends NativeGeneralService<CarFilter, CarDTO> {

    private static final String SQL = "select * from Car c ";

    public CarNativeService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    protected CarDTO mapToDTO(ResultSet rs, int i) throws SQLException {
        CarDTO carDTO = new CarDTO();
        carDTO.setName(rs.getString("name"));
        carDTO.setYear(rs.getLong("year"));


        return carDTO;

    }

    @Override
    protected SQLPartWithParams getWhereQuery(CarFilter filterDto) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();

        List<String> wherePartList = new ArrayList<>();
        addAsLike(wherePartList, mapSqlParameterSource, "c.name", filterDto.getName());
        addAsLong(wherePartList, mapSqlParameterSource, "c.year", filterDto.getYear());

        return new SQLPartWithParams(wherePartList, mapSqlParameterSource);

    }

    @Override
    protected String getSql(CarFilter filterDto) {
        return SQL;
    }
}
