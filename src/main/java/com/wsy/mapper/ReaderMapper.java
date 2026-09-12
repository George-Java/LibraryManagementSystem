package com.wsy.mapper;

import com.wsy.model.Reader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReaderMapper {
    List<Reader> selectAll();

    Reader selectByBarcode(@Param("barcode") String barcode);

    Reader selectByIdentityCard(@Param("identityCard") String identityCard);

    int insert(Reader reader);

    int update(Reader reader);

    int deleteByBarcode(@Param("barcode") String barcode);
}
