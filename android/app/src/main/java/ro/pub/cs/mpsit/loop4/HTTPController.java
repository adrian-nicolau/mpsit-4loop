package ro.pub.cs.mpsit.loop4;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    private String baseUrl = "http://192.168.0.103:8080/api/";

    private ArrayList<String> allowedDistances = new ArrayList<String>();

    public HTTPController(Context context) {
        this.context = context;
        // Instantiate the RequestQueue.
        this.queue = Volley.newRequestQueue(this.context);
    }

    public void getTweets(String query, double lon, double lat, String distance, final TextView container) {

        String url = baseUrl + "post/?q=" + query + "&longitude=" + lon + "&latitude=" + lat + "&distance=" + distance;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        container.setText("Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                container.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getDistances() {

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
