package com.hunkee1017.bookreader.book.entity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface BookDao {

    @Insert
    void insertBook(Book... books);

    @Update()
    void updateBook(Book... books);

    @Query("SELECT * FROM books WHERE bookName = :bookName")
    Book findByBookName(String bookName);


}
