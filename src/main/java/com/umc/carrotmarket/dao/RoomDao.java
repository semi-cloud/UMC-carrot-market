package com.umc.carrotmarket.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class RoomDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int getProductRoom(int productIdx){
        try{
            return this.jdbcTemplate.queryForObject("select count(productIdx) as roomCnt from Room where productIdx = ? group by productIdx ",
                    (rs, rowNum) -> rs.getInt("roomCnt"), productIdx
            );
        }catch(DataAccessException e){
            if(e instanceof EmptyResultDataAccessException) {
                return 0;
            }
        }
        return 0;
    }
}
