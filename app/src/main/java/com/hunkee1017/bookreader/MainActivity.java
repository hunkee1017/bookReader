package com.hunkee1017.bookreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;


import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hunkee1017.bookreader.book.database.BookDatabase;
import com.hunkee1017.bookreader.book.entity.Book;
import com.hunkee1017.bookreader.book.entity.BookDao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private static final int LOAD_FILE = 1;

    private static final int LINE_PER_PAGE = 5000;

    private String contents;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);        // 시작 Layout 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("text/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);
        });

        NestedScrollView contentScrolling = (NestedScrollView) findViewById(R.id.content_scrolling);

        if(contentScrolling != null){
            contentScrolling.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if(!contentScrolling.canScrollVertically(1)){
                    nextPageOfContents();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode) {
            case LOAD_FILE:
                loadFile(data);
                break;
            default:
                return;
        }
        if(requestCode != 1 || resultCode != RESULT_OK){
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void loadFile(@Nullable Intent data){
        BookDatabase bookDatabase = BookDatabase.getInstance(getApplicationContext());
        final BookDao bookDao = bookDatabase.bookDao();

        StringBuilder stringBuilder = new StringBuilder(1024 * 50);

        try (InputStream in = getContentResolver().openInputStream(data.getData())){
            Scanner scanner = new Scanner(in, "UTF-16LE");
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                stringBuilder.append(line);
                stringBuilder.append(System.getProperty("line.separator"));
            }

            contents = stringBuilder.toString();

            String bookName = data.getData().getPath();
            book = bookDao.findByBookName(bookName);


            int contentLine = 0;

            if(book == null){
                bookDao.insertBook(new Book.Builder(data.getData().getPath())
                        .line(contentLine)
                        .builder());
                book = bookDao.findByBookName(bookName);
            }else{
                contentLine = book.getLine();
            }
            TextView contentView = (TextView) findViewById(R.id.content);
            contentView.append(contents, contentLine, (contents.length() >= contentLine + LINE_PER_PAGE) ? contentLine + LINE_PER_PAGE : contents.length() - 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextPageOfContents(){
        TextView contentView = (TextView) findViewById(R.id.content);
        int nextLine = book.getLine() + LINE_PER_PAGE;
        contentView.append(contents, nextLine, (contents.length() >= nextLine + LINE_PER_PAGE) ? nextLine + LINE_PER_PAGE : contents.length() - 1);

        updateBookLine(nextLine);
    }

    public void updateBookLine(int nextLine){
        Book.Builder builder = new Book.Builder(book.getBookName())
                .id(book.getId())
                .line(nextLine);

        BookDatabase bookDatabase = BookDatabase.getInstance(getApplicationContext());
        BookDao bookDao = bookDatabase.bookDao();
        bookDao.updateBook(builder.builder());

        book = bookDao.findByBookName(book.getBookName());

    }

    @Override
    protected void onDestroy() {
        contents = null;
        book = null;
        super.onDestroy();
    }
}