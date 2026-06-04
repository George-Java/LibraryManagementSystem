package com.wsy.mapper;
import com.wsy.model.Stockpile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface StockpileMapper {
    List<Stockpile> selectAll();
    Stockpile selectByBookId(Integer bookId);
    Stockpile selectByBarcode(String barcode);
    int insert(Stockpile stockpile);
    int update(Stockpile stockpile);
    int delete(Integer id);
    int deleteByBookId(Integer bookId);
    Stockpile selectByBookISBN(String bookISBN);
    int updateStockByBookISBN(@Param("bookISBN") String bookISBN, @Param("quantity") Integer quantity);
}