package ro.pub.cs.mpsit.loop4;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Location sample.
 * <p/>
 * Demonstrates use of the Location API to retrieve the last known location for a device.
 * This sample uses Google Play services (GoogleApiClient) but does not need to authenticate a user.
 * See https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart if you are
 * also using APIs that need authentication.
 */
public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    protected static final String TAG = "MainActivity";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    protected HTTPController mHttpClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    protected String mRadius = "5km";

    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView mDebugText;
    protected Button mPostButton;
    protected Button mRefreshButton;
    protected Button mLocateButton;
    protected SeekBar mSeekBar;
    protected ListView mListView;
    protected ArrayAdapter<Tweet> mListAdapter;

    protected ArrayList<Tweet> mTweets = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
        mDebugText = (TextView) findViewById(R.id.debug_text);
        mPostButton = (Button) findViewById(R.id.post);
        mRefreshButton = (Button) findViewById(R.id.refresh);
        mLocateButton = (Button) findViewById(R.id.locate);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mListView = (ListView) findViewById(R.id.list);

        setEnv();

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        DialogFragment dialog = new GetServerIPDialogFragment();
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                    mLastLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                    mLastLocation.getLongitude()));
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /* Called from Dialog's onClick. */
    public void testConnection() {
        mHttpClient.getDistances();
    }

    private void setEnv() {
        setPostButton();
        setRefreshButton();
        setLocateButton();

        setSeekBar();
        setList();
    }

    private void setPostButton() {
        mPostButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // get last location
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                final EditText input = new EditText(MainActivity.this);

                alertDialog.setTitle("Post Message");
                alertDialog.setMessage("Say what's on your mind..");
                alertDialog.setView(input);

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Post", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String message = input.getText().toString();
                        Log.i(TAG, message);
                        mHttpClient.postMessage(message, mLastLocation.getLatitude(), mLastLocation.getLongitude(), mDebugText);
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void setRefreshButton() {
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // get tweets with new location
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                final EditText input = new EditText(MainActivity.this);

                alertDialog.setTitle("Optional");
                alertDialog.setMessage("Search by:");
                alertDialog.setView(input);

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String criteria = input.getText().toString();
                        Log.i(TAG, criteria);
                        mHttpClient.getTweets(
                                criteria,
                                mLastLocation.getLatitude(),
                                mLastLocation.getLongitude(),
                                mRadius,
                                mDebugText,
                                mListAdapter
                        );
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void setLocateButton() {
        mLocateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,
                            mLastLocation.getLatitude()));
                    mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,
                            mLastLocation.getLongitude()));
                    Toast.makeText(MainActivity.this, R.string.location_refreshed, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void setSeekBar() {
        mSeekBar.setProgress(2500);         // 2.5km
        mSeekBar.incrementProgressBy(50);   // 50m
        mSeekBar.setMax(5000);              // 5km

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 50m step
                progress = progress / 50;
                progress = progress * 50;
                if (fromUser) {
                    seekBar.setProgress(progress);
                }
                mDebugText.setText("I want to see messages within " + String.valueOf(progress) + " meters.");

                // ugly as hell
                if (progress < 75)
                    mRadius = "50m";
                else if (progress < 250)
                    mRadius = "100m";
                else if (progress < 1000)
                    mRadius = "500m";
                else if (progress < 1500)
                    mRadius = "1km";
                else if (progress < 2500)
                    mRadius = "2km";
                else
                    mRadius = "5km";
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    protected void setList() {
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mTweets);

        // Assign adapter to ListView
        mListView.setAdapter(mListAdapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Tweet itemValue = (Tweet) mListView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "posted by: " + itemValue.id + "\n" +
                                "date: " + itemValue.date + "\n" +
                                "coord: (" + itemValue.lat + ", " + itemValue.lon + ")",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

}
