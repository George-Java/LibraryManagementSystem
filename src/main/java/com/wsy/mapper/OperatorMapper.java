package com.wsy.mapper;

import com.wsy.model.Operator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperatorMapper {
    Operator login(@Param("userName") String userName, @Param("password") String password);

    int insert(Operator operator);

    int update(Operator operator);

    int deleteById(@Param("id") Integer id);

    Operator selectById(@Param("id") Integer id);

    int updatePassword(@Param("id") Integer id, @Param("newPassword") String newPassword);

    List<Operator> selectAll();

    List<Operator> selectByCondition(@Param("column") String column, @Param("value") String value);
}
