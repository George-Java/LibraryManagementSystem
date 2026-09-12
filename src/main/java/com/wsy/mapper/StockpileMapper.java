package com.wsy.mapper;

import com.wsy.model.Stockpile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockpileMapper {
    List<Stockpile> selectAll();

    Stockpile selectByBookId(@Param("bookISBN") String bookISBN);

    Stockpile selectByBarcode(@Param("barcode") String barcode);

    int insert(Stockpile stockpile);

    int update(Stockpile stockpile);

    int delete(@Param("bookISBN") String bookISBN);

    int deleteByBookId(@Param("bookISBN") String bookISBN);

    Stockpile selectByBookISBN(@Param("bookISBN") String bookISBN);

    int updateStockByBookISBN(@Param("bookISBN") String bookISBN, @Param("quantity") Integer quantity);
}
