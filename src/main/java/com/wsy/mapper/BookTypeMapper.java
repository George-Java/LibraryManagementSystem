package com.wsy.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.wsy.model.BookType;

@Mapper
public interface BookTypeMapper {
    List<BookType> selectAll();

    BookType selectByNumber(String number);

    BookType selectByTypeName(String typeName);

    List<BookType> selectByKeyword(String keyword);

    int countBooksByType(String number);

    int insert(BookType bookType);

    int update(BookType bookType);

    int updateWithNumberChange(@Param("oldNumber") String oldNumber, @Param("bookType") BookType bookType);

    int updateTypeName(@Param("number") String number, @Param("typeName") String typeName);

    int deleteByNumber(String number);

    int updateBookCategory(@Param("oldNumber") String oldNumber, @Param("newNumber") String newNumber);
}