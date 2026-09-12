package com.wsy.mapper;

import com.wsy.model.Borrow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BorrowMapper {
    List<Borrow> selectAll();

    Borrow selectById(@Param("id") Integer id);

    int insert(Borrow borrow);

    int update(Borrow borrow);

    int delete(@Param("id") Integer id);

    List<java.util.Map<String, Object>> selectBorrowDetails(@Param("readerNumber") String readerNumber);

    List<java.util.Map<String, Object>> selectAllBorrowRecords();
}
