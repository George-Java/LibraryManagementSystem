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

    Order selectById(Integer id);

    List<Order> selectByBookId(Integer bookId);

    List<Order> selectByOperatorId(Integer operatorId);

    int insert(Order order);

    int update(Order order);

    int delete(Integer id);

    List<Map<String, Object>> selectUnacceptedOrders();

    int updateOrderAcceptStatus(@Param("bookISBN") String bookISBN, @Param("orderDate") LocalDate orderDate);

    int deleteUnacceptedOrder(@Param("bookISBN") String bookISBN, @Param("orderDate") LocalDate orderDate);
}