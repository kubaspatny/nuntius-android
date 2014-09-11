package com.kubaspatny.startupanimation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kubaspatny.startupanimation.JSONUtil.Message;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MessagesActivity extends Activity {

    @InjectView(R.id.messages_list) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.inject(this);

        LinearLayout emptyView = (LinearLayout) findViewById(R.id.empty_state);
        listView.setEmptyView(emptyView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new LoadMessagesAsyncTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadMessagesAsyncTask extends AsyncTask<Void, Void, ArrayAdapter<String>> {

        public final String DEBUG_TAG = "LoadMessagesAsyncTask";

        @Override
        protected void onPreExecute() {
            Toast.makeText(MessagesActivity.this, "Downloading messages.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Void... voids) {

            try {
                URL url = new URL("http://resttime-kubaspatny.rhcloud.com/rest/msg/latest");
                String result = NetworkUtils.getHTTP(url);
                Gson gson = new Gson();
                Message[] messages = gson.fromJson(result, Message[].class);
                List<String> text_list = new ArrayList<String>();
                for(Message m : messages){
                    text_list.add(m.getmMessageBody());
                }

                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(MessagesActivity.this, android.R.layout.simple_list_item_1, text_list);

                return itemsAdapter;

            } catch(MalformedURLException e){
                Log.e(DEBUG_TAG, e.getLocalizedMessage());
            } catch(Exception e){
                Log.e(DEBUG_TAG, e.getLocalizedMessage());
            }

            return null;

        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> adapter) {

            if(adapter != null){

                listView.setAdapter(adapter);
                Toast.makeText(MessagesActivity.this, "Messages downloaded.", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(MessagesActivity.this, "Error while downloading messages.", Toast.LENGTH_SHORT).show();

            }


        }
    }



}
