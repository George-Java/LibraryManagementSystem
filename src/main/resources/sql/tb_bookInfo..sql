USE library;
CREATE TABLE IF NOT EXISTS tb_bookInfo
(
    bookISBN   CHAR(13) PRIMARY KEY COMMENT '书籍编号',
    category   VARCHAR(13) NOT NULL COMMENT '图书类别', 
    bookname   VARCHAR(40) NOT NULL COMMENT '图书名称',
    writer     VARCHAR(20) COMMENT '作者',
    publisher  VARCHAR(50) NOT NULL COMMENT '出版社',
    translator VARCHAR(30) COMMENT '译者',
    date       DATE        NOT NULL COMMENT '出版日期',
    price      DOUBLE      NOT NULL COMMENT '图书价格',
    CONSTRAINT fk_book_category FOREIGN KEY (category)
        REFERENCES tb_booktype (number)
        ON DELETE RESTRICT                              
);