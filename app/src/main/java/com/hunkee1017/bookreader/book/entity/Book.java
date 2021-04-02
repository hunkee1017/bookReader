package com.hunkee1017.bookreader.book.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {
    @PrimaryKey
    public Long id;

    public String bookName;
    public int line;

    public Book(){}

    public Book(Builder builder){
        id = builder.id;
        bookName = builder.bookName;
        line = builder.line;
    }

    public Long getId(){
        return id;
    }

    public String getBookName() {
        return bookName;
    }

    public int getLine() {
        return line;
    }

    public static class Builder {
        private Long id;
        private final String bookName;
        private int line = 0;

        public Builder(String bookName){
            this.bookName = bookName;
        }

        public Builder id(Long id){
            this.id = id;
            return this;
        }

        public Builder line(int line){
            this.line = line;
            return this;
        }

        public Book builder(){
            return new Book(this);
        }
    }




}
