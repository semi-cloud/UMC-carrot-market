package com.umc.carrotmarket.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Repository
public class RegionDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getUserRegion(int userIdx){
        try{
            return this.jdbcTemplate.queryForObject("select name as region from Region where userIdx = ?",
                    (rs, rowNum) -> rs.getString("region"), userIdx);
        }catch(DataAccessException e){
            if(e instanceof EmptyResultDataAccessException){
                return null;
            }
        }
        return null;
    }
}
