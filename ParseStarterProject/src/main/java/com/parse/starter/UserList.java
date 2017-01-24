package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class UserList extends AppCompatActivity {

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle("User List");

        if (ParseUser.getCurrentUser().get("following") == null) {
            List<String> emptyList = new ArrayList<>();
            ParseUser.getCurrentUser().put("following", emptyList);
        }

        final ListView lvUsers = (ListView) findViewById(R.id.lvUsers);

        lvUsers.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        arrayAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_checked,
                users);

        lvUsers.setAdapter(arrayAdapter);

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;

                if (checkedTextView.isChecked()) {
                    Log.i("Info", "Row is checked");

                    ParseUser.getCurrentUser().getList("following").add(users.get(position));
                    ParseUser.getCurrentUser().saveInBackground();
                }
                else {
                    Log.i("Info", "Row is not checked");

                    ParseUser.getCurrentUser().getList("following").remove(users.get(position));
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        });

        users.clear();
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseUser user: objects) {
                            users.add(user.getUsername());
                        }
                        arrayAdapter.notifyDataSetChanged();

                        for (String username: users) {
                            if (ParseUser.getCurrentUser().getList("following").contains(username)) {
                                lvUsers.setItemChecked(users.indexOf(username), true);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.tweet_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.tweet) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Send a Tweet");

            final EditText etTweet = new EditText(this);
            builder.setView(etTweet);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("Info", etTweet.getText().toString());
                    ParseObject tweet = new ParseObject("Tweet");
                    tweet.put("username", ParseUser.getCurrentUser().getUsername());
                    tweet.put("tweet", etTweet.getText().toString());
                    tweet.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Tweet Sent Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                            else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Tweet Failed - Please try again later",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
        else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            Intent intent = new Intent(
                    getApplicationContext(),
                    MainActivity.class
            );
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.feed) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    Feed.class
            );
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
