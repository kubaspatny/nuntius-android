package com.kubaspatny.startupanimation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SendMessageActivity extends Activity {

    public  final static String DEBUG_TAG = "SendMessageActivity";

    @InjectView(R.id.mButtonSend) ActionProcessButton mSendButton;
    @InjectView(R.id.mMessageEditText) EditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        ButterKnife.inject(this);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMessageEditText.getText().toString() == null || mMessageEditText.getText().toString().trim().isEmpty()) return;

                if(!checkConnectivity()){
                    Toast.makeText(SendMessageActivity.this, "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    URL url = new URL("http://resttime-kubaspatny.rhcloud.com/rest/msg/add");
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("message", mMessageEditText.getText().toString()));

                    new PostMessageAsyncTask().execute(new PostMessageHolder(url, params));

                } catch(MalformedURLException e){
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                    Toast.makeText(SendMessageActivity.this, "Error sending message. Wrong URL.", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_checkGooglePlayServices) {

            Intent i = new Intent(this, MessagesActivity.class);
            startActivity(i);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }

    private class PostMessageAsyncTask extends AsyncTask<PostMessageHolder, Void, Integer> {

        public final String DEBUG_TAG = "PostMessageAsyncTask";

        @Override
        protected void onPreExecute() {
            mSendButton.setMode(ActionProcessButton.Mode.ENDLESS);
            mSendButton.setProgress(1);
        }

        @Override
        protected Integer doInBackground(PostMessageHolder... urls) {
            int result = NetworkUtils.postHTTP(urls[0].getUrl(), urls[0].getParms());
            Log.i(DEBUG_TAG, "Result code is: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if(result == null || result != 200){
                Toast.makeText(SendMessageActivity.this.getApplicationContext(), "Error sending message.", Toast.LENGTH_SHORT).show();
                mSendButton.setProgress(0);
                return;
            }

            Toast.makeText(SendMessageActivity.this.getApplicationContext(), "Message sent!", Toast.LENGTH_SHORT).show();

            mSendButton.setProgress(0);
            mMessageEditText.setText("");

        }
    }

}


