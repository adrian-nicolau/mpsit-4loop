package ro.pub.cs.mpsit.loop4;

import android.content.Context;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Manages HTTP requests to the node backend, via Volley.
 */
public class HTTPController {

    protected static final String TAG = "HTTPController";

    private Context context;
    private RequestQueue queue;
    private String baseUrl;

    protected ArrayList<String> allowedDistances = new ArrayList<String>();

    public HTTPController(Context context, String ipAddress) {
        this.context = context;
        // Instantiate the RequestQueue.
        this.queue = Volley.newRequestQueue(this.context);
        this.baseUrl = "http://" + ipAddress + ":8080/api/";
    }

    protected void getTweets(String query, double lon, double lat, String distance, final TextView debug) {

        String url = baseUrl + "post/?q=" + query + "&longitude=" + lon + "&latitude=" + lat + "&distance=" + distance;

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Response is: " + response.toString());
                        debug.setText("Response is: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                debug.setText("That didn't work: " + error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    protected void postMessage(String message, double lon, double lat, final TextView debug) {
        String url = baseUrl + "post";

        // Create a JSON message: {message: messageText, location: {longitude: lon, latitude: lat}}
        JSONObject outerObject = new JSONObject();
        JSONObject locationObject = new JSONObject();
        try {
            locationObject.put("longitude", lon);
            locationObject.put("latitude", lat);
            outerObject.put("message", message);
            outerObject.put("location", locationObject);
            Log.i(TAG, outerObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Post a JSON message to the provided URL.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url, outerObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Response is: " + response.toString());
                        debug.setText("Response is: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                debug.setText("That didn't work: " + error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    protected void getDistances() {

        String url = baseUrl + "distances";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject reader = new JSONObject(response);
                            JSONArray distances = reader.getJSONArray("distances");
                            if (distances != null) {
                                for (int i = 0; i < distances.length(); i++) {
                                    allowedDistances.add(distances.get(i).toString());
                                }
                            }
                            printDistances();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void printDistances() {
        for (int i = 0; i < allowedDistances.size(); i++) {
            Log.i(TAG, allowedDistances.get(i));
        }
    }
}
