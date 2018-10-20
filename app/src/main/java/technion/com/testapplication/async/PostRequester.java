package technion.com.testapplication.async;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PostRequester {
    private static RequestQueue Queue;
    private org.json.JSONObject JsonObject;

    public PostRequester(Context context) {
        Queue = Volley.newRequestQueue(context);
    }

    public void SendRequest(final String URL, final Map<String, String> Params, final Runnable OnResponse, final Runnable OnError) {
        Log.d("I", "Requested to send to URL: " + URL + " params: " + Params.toString());
        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try
                {
                    Log.d("I", "Request returned from URL: " + URL);
                    Log.d("I", "Raw JSON response: " + response);
                    JsonObject = new JSONObject(response);
                    Log.d("I", "Response: " + JsonObject.toString());

                    boolean isError = JsonObject.getBoolean("error");
                    if (isError)
                    {
                        String errorDetails = JsonObject.getString("details");
                        Log.e("E", "Response with error = TRUE. details: " + errorDetails);
                        if (OnError != null)
                        {
                            OnError.run();
                        }
                        return;
                    }

                    if (OnResponse != null)
                    {
                        OnResponse.run();
                    }
                } catch (JSONException e)
                {
                    Log.e("E", "Error in JSON parsing.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("E", "Error in sending the request.");
                if (OnError != null)
                {
                    OnError.run();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return Params;
            }
        };

        Queue.add(sr);
    }
}
