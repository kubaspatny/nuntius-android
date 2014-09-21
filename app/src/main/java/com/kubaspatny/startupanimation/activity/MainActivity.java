package com.kubaspatny.startupanimation.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kubaspatny.startupanimation.network.NetworkUtils;
import com.kubaspatny.startupanimation.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends Activity {

    private final static String DEBUG_TAG = "MainActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final String REGISTRATION_PARAMETER = "com.kubaspatny.nuntius.gcm.registrationID";
    public static final String USERNAME_PARAMETER = "com.kubaspatny.nuntius.gcm.username";

    private GoogleCloudMessaging gcm;
    private SharedPreferences prefs;
    private String regid;

    private String SENDER_ID = "321813590674";
    private Context context;

    @InjectView(R.id.logo) ImageView logo;
    @InjectView(R.id.username_edittext) EditText edittext;
    @InjectView(R.id.register_button) Button button;
    @InjectView(R.id.skip_button) Button skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(!getRegistrationId().isEmpty()){
            Intent i = new Intent(MainActivity.this, DrawerActivity.class);
            startActivity(i);
        }

        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        context = getApplicationContext();

        edittext.setEnabled(false);
        button.setEnabled(false);

        {
            // *************** LOGO ANIMATIONS ************************

            ObjectAnimator logo_moveDown = ObjectAnimator.ofFloat(logo, "translationY", 0f, 250f);
            logo_moveDown.setDuration(0);

            ObjectAnimator logo_moveUp = ObjectAnimator.ofFloat(logo, "translationY", 250f, 0f);
            logo_moveUp.setDuration(1000);
            logo_moveUp.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator logo_alphaToZero = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f);
            logo_alphaToZero.setDuration(0);

            ObjectAnimator logo_alphaToOne = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
            logo_alphaToOne.setDuration(2000);
            logo_alphaToOne.setInterpolator(new DecelerateInterpolator());

            // ***************** EDIT TEXT ANIMATIONS **********************

            ObjectAnimator edittext_alphaToZero = ObjectAnimator.ofFloat(edittext, "alpha", 1f, 0f);
            edittext_alphaToZero.setDuration(0);

            ObjectAnimator edittext_alphaToOne = ObjectAnimator.ofFloat(edittext, "alpha", 0f, 1f);
            edittext_alphaToOne.setDuration(1000);

            // ***************** BUTTON ANIMATIONS **********************

            ObjectAnimator button_alphaToZero = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f);
            button_alphaToZero.setDuration(0);

            ObjectAnimator button_alphaToOne = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);
            button_alphaToOne.setDuration(1000);

            // ***************** ANIMATOR SET ****************************

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(logo_moveDown, edittext_alphaToZero, button_alphaToZero, logo_alphaToZero);
            animatorSet.play(logo_alphaToOne).after(logo_moveDown).after(500);
            animatorSet.play(logo_moveUp).after(logo_alphaToOne);
            animatorSet.play(edittext_alphaToOne).after(logo_moveUp).after(500);
            animatorSet.play(button_alphaToOne).after(logo_moveUp).after(500);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    edittext.setEnabled(true);
                    button.setEnabled(true);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorSet.start();

        }

        final SmoothProgressBar smoothProgressBar = (SmoothProgressBar) findViewById(R.id.progressBar);
        smoothProgressBar.setVisibility(View.GONE);
        smoothProgressBar.progressiveStop();

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DrawerActivity.class);
                startActivity(i);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edittext.getText().toString().trim().isEmpty()) return;
                checkUserRegistration();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        return true;
    }

    private void checkUserRegistration(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                Toast.makeText(context, "Device already registered with ID: " + regid, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i(DEBUG_TAG, "No valid Google Play Services APK found.");
        }
    }

    private String getRegistrationId() {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i(DEBUG_TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
            Log.i(DEBUG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences() {
        return getSharedPreferences(MainActivity.class.getSimpleName(), getApplicationContext().MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void sendRegistrationIdToBackend(String regid, String username) {


        try {
            URL url = new URL("http://resttime-kubaspatny.rhcloud.com/rest/push/android/register");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(REGISTRATION_PARAMETER, regid));

            if(username != null && !username.trim().isEmpty()) params.add(new BasicNameValuePair(USERNAME_PARAMETER, username));

            int result = NetworkUtils.postHTTP(url, params);

            Log.i(DEBUG_TAG, "sendRegistrationIdToBackend result: " + result);


        } catch(MalformedURLException e){
            Log.e(DEBUG_TAG, e.getLocalizedMessage());
        }

    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);
        Log.i(DEBUG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {

                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(regid, edittext.getText().toString());

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }



}
