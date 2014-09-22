package com.kubaspatny.startupanimation.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kubaspatny.startupanimation.JSONUtil.Message;
import com.kubaspatny.startupanimation.activity.DrawerActivity;
import com.kubaspatny.startupanimation.data.NuntiusContentProvider;
import com.kubaspatny.startupanimation.data.NuntiusDataContract;
import com.kubaspatny.startupanimation.network.NetworkUtils;
import com.kubaspatny.startupanimation.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LatestMessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayout emptyView;

    private SimpleCursorAdapter cursorAdapter;


    public static LatestMessagesFragment newInstance() {
        LatestMessagesFragment fragment = new LatestMessagesFragment();
        return fragment;
    }

    public LatestMessagesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_latest_messages, container, false);

        listView = (ListView) result.findViewById(R.id.messages_list);
        swipeContainer = (SwipeRefreshLayout) result.findViewById(R.id.swipe_container);
        emptyView = (LinearLayout) result.findViewById(R.id.empty_state);

        listView.setEmptyView(emptyView);

        swipeContainer.setOnRefreshListener(this);

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

        String[] from = {NuntiusDataContract.MessageEntry.COLUMN_NAME_TEXT, NuntiusDataContract.MessageEntry.COLUMN_NAME_TIMESTAMP};
        int[] to = {R.id.message_row_text, R.id.message_row_timestamp};

        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.message_row, null, from, to, 0);
        listView.setAdapter(cursorAdapter);

        return result;
    }

    // onRefresh for the pull to refresh
    @Override
    public void onRefresh() {
        new LoadMessagesAsyncTask().execute();
    }

    // ----------------- LOADER CALLBACKS ----------------------------------------

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {NuntiusDataContract.MessageEntry._ID, NuntiusDataContract.MessageEntry.COLUMN_NAME_TEXT,
                NuntiusDataContract.MessageEntry.COLUMN_NAME_TIMESTAMP};

        CursorLoader cursorLoader = new CursorLoader(getActivity(), NuntiusContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        cursorAdapter.swapCursor(null);
    }

    // ---------------------------------------------------------------------------

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getSupportLoaderManager().destroyLoader(0);
        //TODO: IS THIS A GOOD PRACTISE OR NOT?
    }

    private class LoadMessagesAsyncTask extends AsyncTask<Void, Void, ArrayAdapter<String>> {

        public final String DEBUG_TAG = "LoadMessagesAsyncTask";

        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Downloading messages.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayAdapter<String> doInBackground(Void... voids) {

            try {
                URL url = new URL("http://resttime-kubaspatny.rhcloud.com/rest/msg/latest");
                String result = NetworkUtils.getHTTP(url);
                Gson gson = new Gson();
                Message[] messages = gson.fromJson(result, Message[].class);
                List<String> text_list = new ArrayList<String>();

                getActivity().getContentResolver().delete(NuntiusContentProvider.CONTENT_URI, null, null); // delete previous messages

                for(Message m : messages){
                    text_list.add(m.getmMessageBody());

                    // add message to DB;

                    ContentValues values = new ContentValues();
                    values.put(NuntiusDataContract.MessageEntry.COLUMN_NAME_TEXT, m.getmMessageBody());
                    values.put(NuntiusDataContract.MessageEntry.COLUMN_NAME_TIMESTAMP, m.getTimestampString());
                    getActivity().getContentResolver().insert(NuntiusContentProvider.CONTENT_URI, values);

                }

                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, text_list);

                return itemsAdapter;

            } catch(MalformedURLException e){
                Log.e(DEBUG_TAG, e.getLocalizedMessage());
            } catch(Exception e){
                String message = (e.getLocalizedMessage() == null) ? "Error downloading messages." : e.getLocalizedMessage();
                Log.e(DEBUG_TAG, message); // <----------------- this causes error ---------------------------------------
            }

            return null;

        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> adapter) {

            swipeContainer.setRefreshing(false);

//            if(adapter != null){
//
//                listView.setAdapter(adapter);
//                Toast.makeText(getActivity(), "Messages downloaded.", Toast.LENGTH_SHORT).show();
//
//            } else {
//
//                if(getActivity() != null) {
//                    Toast.makeText(getActivity(), "Error while downloading messages.", Toast.LENGTH_SHORT).show();
//                }
//
//            }


        }
    }

}
