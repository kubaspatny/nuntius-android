package com.kubaspatny.startupanimation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
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


public class MessagesActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.messages_list) ListView listView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;
    @InjectView(R.id.empty_state) LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.inject(this);

        listView.setEmptyView(emptyView);

        swipeContainer.setOnRefreshListener(this);

//        swipeContainer.setColorScheme(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);

        swipeContainer.setColorScheme(
                R.color.nuntius_main,
                R.color.nuntius_main_shade,
                R.color.nuntius_main_shade_lighter,
                R.color.nuntius_main_shade_lighter2);

        swipeContainer.setEnabled(true);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (listView == null || listView.getChildCount() == 0) ? 0 : listView.getChildAt(0).getTop();
                swipeContainer.setEnabled(topRowVerticalPosition >= 0);
            }
        });

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

    // onRefresh for the pull to refresh
    @Override
    public void onRefresh() {
        new LoadMessagesAsyncTask().execute();
    }

    private class LoadMessagesAsyncTask extends AsyncTask<Void, Void, ArrayAdapter<String>> {

        public final String DEBUG_TAG = "LoadMessagesAsyncTask";

        @Override
        protected void onPreExecute() {
            Toast.makeText(MessagesActivity.this, "Downloading messages.", Toast.LENGTH_SHORT).show();
            //swipeContainer.setRefreshing(false);
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

            swipeContainer.setRefreshing(false);

            if(adapter != null){

                listView.setAdapter(adapter);
                Toast.makeText(MessagesActivity.this, "Messages downloaded.", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(MessagesActivity.this, "Error while downloading messages.", Toast.LENGTH_SHORT).show();

            }


        }
    }



}
