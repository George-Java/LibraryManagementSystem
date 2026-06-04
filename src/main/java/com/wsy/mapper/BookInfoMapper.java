package com.wsy.mapper;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import com.wsy.model.BookInfo;
@Mapper
public interface BookInfoMapper {
    List<BookInfo> selectAll();
    BookInfo selectById(Integer id);
    BookInfo selectByBookISBN(String bookISBN);
    List<BookInfo> selectByType(String typeId);
    List<BookInfo> searchByTitle(String title);
    List<BookInfo> searchByAuthor(String author);
    List<BookInfo> searchByISBN(String isbn);
    List<BookInfo> searchByPublisher(String publisher);
    int insert(BookInfo bookInfo);
    int update(BookInfo bookInfo);
    int delete(Integer id);
    int deleteByBarcode(String bookISBN);
    Map<String, Object> selectBookDetailByISBN(String bookISBN);
    List<String> selectAllPublishers();
    List<Map<String, Object>> selectAllAvailableBooks();
}