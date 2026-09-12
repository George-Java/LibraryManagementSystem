package com.wsy.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.wsy.model.BookInfo;

@Mapper
public interface BookInfoMapper {
    List<BookInfo> selectAll();

    /** Lookup by the ISBN primary key (tb_bookInfo.bookISBN). */
    BookInfo selectById(@Param("bookISBN") String bookISBN);

    BookInfo selectByBookISBN(@Param("bookISBN") String bookISBN);

    List<BookInfo> selectByType(@Param("typeId") String typeId);

    List<BookInfo> searchByTitle(@Param("title") String title);

    List<BookInfo> searchByAuthor(@Param("author") String author);

    List<BookInfo> searchByISBN(@Param("isbn") String isbn);

    List<BookInfo> searchByPublisher(@Param("publisher") String publisher);

    int insert(BookInfo bookInfo);

    int update(BookInfo bookInfo);

    /** Delete by the ISBN primary key (tb_bookInfo.bookISBN). */
    int delete(@Param("bookISBN") String bookISBN);

    int deleteByBarcode(@Param("bookISBN") String bookISBN);

    Map<String, Object> selectBookDetailByISBN(@Param("bookISBN") String bookISBN);

    List<String> selectAllPublishers();

    List<Map<String, Object>> selectAllAvailableBooks();
}
