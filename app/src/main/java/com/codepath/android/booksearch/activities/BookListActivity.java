package com.codepath.android.booksearch.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.adapters.BookAdapter;
import com.codepath.android.booksearch.models.Book;
import com.codepath.android.booksearch.net.BookClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookListActivity extends ActionBarActivity {
    private ListView lvBooks;
    private BookAdapter bookAdapter;
    private EditText query;
    private BookClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        lvBooks = (ListView) findViewById(R.id.lvBooks);
        ArrayList<Book> aBooks = new ArrayList<Book>();
        bookAdapter = new BookAdapter(this, aBooks);
        query = (EditText) findViewById(R.id.query);
        lvBooks.setAdapter(bookAdapter);
        // Fetch the data remotely
        fetchBooks();
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private void fetchBooks() {
        client = new BookClient();

        if(!query.getText().toString().isEmpty()) {

            client.getBooks(query.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray docs = null;
                        if (response != null) {
                            // Get the docs json array
                            docs = response.getJSONArray("docs");
                            // Parse json array into array of model objects
                            final ArrayList<Book> books = Book.fromJson(docs);
                            // Load model objects into the adapter
                            for (Book book : books) {
                                bookAdapter.add(book); // add book through the adapter
                            }
                            bookAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        // Invalid JSON format, show appropriate error.
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Query cannot be empty.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
