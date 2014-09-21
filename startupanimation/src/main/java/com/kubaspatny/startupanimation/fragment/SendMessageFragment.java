package com.kubaspatny.startupanimation.fragment;



import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.kubaspatny.startupanimation.network.NetworkUtils;
import com.kubaspatny.startupanimation.network.PostMessageHolder;
import com.kubaspatny.startupanimation.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link SendMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SendMessageFragment extends Fragment {

    private static final String DEBUG_TAG = "SendMessageFragment";

    private ActionProcessButton mSendButton;
    private EditText mMessageEditText;

    public static SendMessageFragment newInstance() {
        SendMessageFragment fragment = new SendMessageFragment();
        return fragment;
    }
    public SendMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_send_message, container, false);

        mSendButton = (ActionProcessButton) result.findViewById(R.id.mButtonSend);
        mMessageEditText = (EditText) result.findViewById(R.id.mMessageEditText);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMessageEditText.getText().toString() == null || mMessageEditText.getText().toString().trim().isEmpty()) return;

                if(!NetworkUtils.checkConnectivity(getActivity())){
                    Toast.makeText(getActivity(), "Please connect to the internet.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    URL url = new URL("http://resttime-kubaspatny.rhcloud.com/rest/msg/add");
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("message", mMessageEditText.getText().toString()));

                    new PostMessageAsyncTask().execute(new PostMessageHolder(url, params));

                } catch(MalformedURLException e){
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                    Toast.makeText(getActivity(), "Error sending message. Wrong URL.", Toast.LENGTH_SHORT).show();
                }


            }
        });



        return result;
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
                Toast.makeText(getActivity(), "Error sending message.", Toast.LENGTH_SHORT).show();
                mSendButton.setProgress(0);
                return;
            }

            Toast.makeText(getActivity(), "Message sent!", Toast.LENGTH_SHORT).show();

            mSendButton.setProgress(0);
            mMessageEditText.setText("");

        }
    }


}
