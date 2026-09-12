package com.wsy.mapper;

import com.wsy.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    List<Order> selectAll();

    Order selectById(@Param("id") Integer id);

    /** Orders are keyed by the book ISBN (tb_order.bookISBN CHAR(13)). */
    List<Order> selectByBookId(@Param("bookId") String bookId);

    List<Order> selectByOperatorId(@Param("operatorId") Integer operatorId);

    int insert(Order order);

    int update(Order order);

    int delete(@Param("id") Integer id);

    List<Map<String, Object>> selectUnacceptedOrders();

    int updateOrderAcceptStatus(@Param("bookISBN") String bookISBN, @Param("orderDate") LocalDate orderDate);

    int deleteUnacceptedOrder(@Param("bookISBN") String bookISBN, @Param("orderDate") LocalDate orderDate);
}
