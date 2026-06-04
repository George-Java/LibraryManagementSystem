package com.wsy.mapper;
import com.wsy.model.Reader;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface ReaderMapper {
    List<Reader> selectAll();
    Reader selectByBarcode(String barcode);
    Reader selectByIdentityCard(String identityCard);
    int insert(Reader reader);
    int update(Reader reader);
    int deleteByBarcode(String barcode);
}