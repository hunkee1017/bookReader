package com.hunkee1017.bookreader.book.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hunkee1017.bookreader.book.entity.Book;
import com.hunkee1017.bookreader.book.entity.BookDao;

@Database(entities = {Book.class}, version = 1)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
    private static BookDatabase bookDatabase;

    public static BookDatabase getInstance(Context context){
        if(bookDatabase == null){
            bookDatabase = Room.databaseBuilder(context.getApplicationContext(), BookDatabase.class, "book-database").allowMainThreadQueries().build();
        }
        return bookDatabase;
    }
}
