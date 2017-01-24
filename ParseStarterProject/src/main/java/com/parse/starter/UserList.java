package com.parse.starter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
}
