package com.hunkee1017.bookreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        TextView contentView = (TextView) findViewById(R.id.content);
        StringBuilder str = new StringBuilder(1024 * 50);

        try (InputStream in = getContentResolver().openInputStream(data.getData())){
            Scanner scanner = new Scanner(in, "UTF-16LE");
            Book book = bookDao.findByBookName(data.getData().getPath());
            int bookLine = 0;
            Book.Builder builder = new Book.Builder(data.getData().getPath()).line(bookLine);

            if(book == null){
                bookDao.insertBook(builder.builder());
            }else{
                bookLine = book.getLine();
                bookDao.updateBook(builder.id(book.getId()).line(bookLine + 5000).builder());
            }
            int i = 0;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                i++;
                if(i > bookLine) {
                    contentView.append(line);
                    contentView.append(System.getProperty("line.separator"));
                }
                if(i > bookLine + 5000){
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}