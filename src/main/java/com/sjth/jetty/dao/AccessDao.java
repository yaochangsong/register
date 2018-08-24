package com.sjth.jetty.dao;

import com.sjth.jetty.pojo.Access;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.List;

@Repository
public class AccessDao {
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Access> getAccess() {
        String sql = "SELECT access_key, access_secret FROM device_register_access WHERE dr = :dr";
        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource("dr", 1), new BeanPropertyRowMapper<>(Access.class));
    }
}