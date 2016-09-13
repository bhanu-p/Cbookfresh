package com.example.delevere.cbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatPage extends AppCompatActivity {
    ListView lv;
    ImageButton send;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chattoolbar);
        setSupportActionBar(toolbar);
        String key_name = getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(key_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),Home2Activicy.class));
            }
        });
        lv = (ListView) findViewById(R.id.list);
        send = (ImageButton) findViewById(R.id.sendButton);

        //adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);

        adapter = new ArrayAdapter<String>(
                this,R.layout.text,R.id.list_content, listItems);

        lv.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputText = (EditText)findViewById(R.id.messageInput);
                String input = inputText.getText().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat tim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String tym = tim.format(c.getTime());

                if (!input.equals("")) {

                    listItems.add(inputText.getText().toString());
                    inputText.setText("");
                    adapter.notifyDataSetChanged();

                }

            }
        });
    }
}
