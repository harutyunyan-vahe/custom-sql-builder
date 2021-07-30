package com.example.demo.builder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;


@Component
@Slf4j
public abstract class NativeGeneralService<FILTER extends QueryFilter, DTO> {
    private static boolean showSQL() {
        return true;
    }

    private static final String COUNT_QUERY = "SELECT count(*) FROM ( %s ) sub";
    private static final String LIMIT_PART = "LIMIT :limit OFFSET :offset";
    private static final String DEFAULT_ORDER_BY = " id ";

    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public NativeGeneralService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    protected abstract DTO mapToDTO(ResultSet rs, int i) throws SQLException;

    protected abstract SQLPartWithParams getWhereQuery(FILTER filterDto);

    protected abstract String getSql(FILTER filterDto);

    @Transactional
    public List<DTO> list(FILTER filterDto) {

        SQLPartWithParams queryWithWhereAndOrderBy = getQueryWithWhereAndOrderBy(filterDto);
        MapSqlParameterSource paramsMap = queryWithWhereAndOrderBy.getMapSqlParameterSource();

        Integer resultsPerPage = filterDto.getResultsPerPage();
        paramsMap.addValue("limit", resultsPerPage);
        paramsMap.addValue("offset", (filterDto.getPage() - 1) * resultsPerPage);

        String finalSql = queryWithWhereAndOrderBy.getSql() + " " + LIMIT_PART;

        if (showSQL()) {
            log.info("Sql === {}", finalSql);
            log.info("Param map === {}", paramsMap);
        }
        return namedParameterJdbcTemplate.query(finalSql, paramsMap, this::mapToDTO);
    }

    public long count(FILTER filterDto) {
        SQLPartWithParams queryWithWhereAndOrderBy = getQueryWithWhereAndOrderBy(filterDto);
        String countSql = String.format(COUNT_QUERY, queryWithWhereAndOrderBy.getSql());

        return namedParameterJdbcTemplate.query(countSql, queryWithWhereAndOrderBy.getMapSqlParameterSource(),
                (rs, rowNum) -> rs.getInt("count")).get(0);
    }


    private SQLPartWithParams getQueryWithWhereAndOrderBy(FILTER filterDto) {
        SQLPartWithParams whereQuery = getWhereQuery(filterDto);

        String orderByPart = getOrderByPart(filterDto);
        String getReportSql = getSql(filterDto) + " " + whereQuery.getSql() + " " + orderByPart;

        return new SQLPartWithParams(getReportSql, whereQuery.getMapSqlParameterSource());
    }

    protected String getOrderByPart(QueryFilter reportFilterDTO) {
        if (StringUtils.isEmpty(reportFilterDTO.getSort())) {
            return " order by  " + getOrderBy();
        }

        return " order by " + reportFilterDTO.getSort() + (BooleanUtils.isTrue(reportFilterDTO.getAscSort()) ? " ASC " : " DESC ") + " , " + getOrderBy();
    }

    protected String getOrderBy() {
        return DEFAULT_ORDER_BY;
    }


    private static String buildWhereList(List<String> wherePartList) {
        String wherePartSQL = "";
        if (!wherePartList.isEmpty()) {
            wherePartSQL = wherePartList.stream()
                    .collect(Collectors.joining(" AND ", " WHERE ", " "));

        }
        return wherePartSQL;
    }

    protected void addAsLike(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, String value) {
        if (StringUtils.isNotEmpty(value)) {
            String paramName = getParamName(column);
            wherePartList.add(getWhereLikePart(column, paramName));
            mapSqlParameterSource.addValue(paramName, toLike(value));
        }
    }

    protected void add(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, String value) {
        if (StringUtils.isNotEmpty(value)) {
            String paramName = getParamName(column);
            wherePartList.add(getWhereEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, value);
        }
    }

    protected void addWithoutParam(List<String> wherePartList, String where) {
        wherePartList.add(where);
    }

    protected void addAsLong(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, Long longVal) {
        if (longVal != null) {
            String paramName = getParamName(column);
            wherePartList.add(getWhereEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, longVal);
        }
    }

    protected void addAsLong(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, String longVal) {
        if (StringUtils.isNotEmpty(longVal)) {
            long value = Long.parseLong(longVal);
            String paramName = getParamName(column);
            wherePartList.add(getWhereEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, value);
        }
    }


    protected void addAsLocalDate(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, LocalDate date) {
        if (date != null) {
            String paramName = getParamName(column);
            wherePartList.add(getWhereEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, date, Types.DATE);
        }
    }


    protected void addGreaterOrEqualDate(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, Date date) {
        if (date != null) {
            String paramName = getParamName(column);
            wherePartList.add(getGreaterOrEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, date, Types.TIMESTAMP);
        }
    }

    protected void addAsBoolean(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, Boolean bool) {
        if (bool != null) {
            String paramName = getParamName(column);
            wherePartList.add(getWhereEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, bool, Types.BOOLEAN);
        }
    }

    protected void addAsEnum(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource, String column, Enum enumVal) {
        if (enumVal != null) {
            String paramName = getParamName(column);
            wherePartList.add(getWhereEqualPart(column, paramName));
            mapSqlParameterSource.addValue(paramName, enumVal.toString(), Types.VARCHAR);
        }

    }

    private String getGreaterOrEqualPart(String column, String paramName) {
        return column + " >= :" + paramName;
    }

    private String getLessOrEqualPart(String column, String paramName) {
        return column + " >= :" + paramName;
    }

    private String getWhereEqualPart(String column, String paramName) {
        return column + " = :" + paramName;
    }

    private String getWhereLikePart(String column, String paramName) {
        return column + " ILIKE :" + paramName;
    }


    private String getParamName(String where) {
        return where.replace(".", "_")
                .toUpperCase().trim();
    }


    private String toLike(String val) {
        return "%" + preprocessValueForFuzzySearch(val) + "%";
    }

    @Data
    public static class SQLPartWithParams {

        private final String sql;
        private final MapSqlParameterSource mapSqlParameterSource;

        public SQLPartWithParams(List<String> wherePartList, MapSqlParameterSource mapSqlParameterSource) {
            this.sql = NativeGeneralService.buildWhereList(wherePartList);
            this.mapSqlParameterSource = mapSqlParameterSource;
        }

        public SQLPartWithParams(String sql, MapSqlParameterSource mapSqlParameterSource) {
            this.sql = sql;
            this.mapSqlParameterSource = mapSqlParameterSource;
        }


    }

    public static String preprocessValueForFuzzySearch(String value) {
        if (isNotEmpty(value)) {
            value = value.toLowerCase();

            // remove accent
            value = Normalizer.normalize(value, Normalizer.Form.NFD);
            value = value.replaceAll("\\p{M}", "");

            // remove non-alphanumeric characters
            value = value.replaceAll("[^a-zA-Z0-9_]", "");
        }
        return value;
    }
}
