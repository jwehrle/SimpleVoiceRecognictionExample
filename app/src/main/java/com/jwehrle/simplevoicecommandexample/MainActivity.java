package com.jwehrle.simplevoicecommandexample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int GET_VOICE_INPUT = 2;
    ListView mListView;
    List<String> mList;
    ArrayAdapter<String> mAdapter;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView)findViewById(R.id.main_listview);
        mList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mList);
        mListView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                try {
                    startActivityForResult(voiceIntent, GET_VOICE_INPUT);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Voice recognition is not supported on this device", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GET_VOICE_INPUT:
                    List<String> userWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (userWords.isEmpty()) {
                        break;
                    }
                    if (userWords.get(0).contains("add")) {
                        String addItem = parseContentOfVoiceCommand(userWords.get(0));
                        if (addItem == null) {
                            break;
                        }
                        voiceAdd(addItem);
                        break;
                    }
                    if (userWords.get(0).contains("delete")){
                        String deleteItem = parseContentOfVoiceCommand(userWords.get(0));
                        if (deleteItem == null) {
                            break;
                        }
                        voiceDelete(deleteItem);
                        break;
                    }

            }
        }
    }

    @Nullable
    private String parseContentOfVoiceCommand(String wordsAsOneString) {
        String [] words = TextUtils.split(wordsAsOneString, " ");
        if (words.length == 1) {
            return  null;
        }
        String content = words[1];
        if (words.length > 2) {
            for (int i = 2; i < words.length; i++) {
                content += " " + words[i];
            }
        }
        return content;
    }

    private void voiceAdd(String item) {
        if (item.length() > 1) {
            item = item.substring(0,1).toUpperCase() + item.substring(1);
        } else {
            item = item.substring(0,1).toUpperCase();
        }
        mList.add(item);
        mAdapter.notifyDataSetChanged();
    }

    private void voiceDelete(String item) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).equalsIgnoreCase(item)) {
                mList.remove(i);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
