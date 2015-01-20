package user.com.profiling.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import user.com.profiling.network.response.EmailOffers;
import user.com.profiling.network.response.UserProfiling;

/**
 * Created by suresh on 12/10/14.
 */
public class ApiRequests {
    public static final String GET_USER_PROFILES_URL = "http://cdn2.appicano.com/CPLOffers/Profiles.txt";
    public static final String GET_EMAIL_OFFERS_URL = "http://cdn2.appicano.com/CPLOffers/EOffers.txt";
    private static final String TAG = ApiRequests.class.getSimpleName();

    public static UserProfiling userProfiles(Context context) {
        RequestFuture<UserProfiling> future = RequestFuture.newFuture();
        GsonRequest<UserProfiling> request = new GsonRequest<>(Request.Method.GET, GET_USER_PROFILES_URL, null, UserProfiling.class, null, future,
                future, VolleyClient.NO_RETRY_POLICY);
        VolleyClient.getInstance(context).getRequestQueue().add(request);
        UserProfiling response = null;
        try {
            response = future.get(); // this will block
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return response;
    }

    public static void getEmailOffers(Context context, Response.Listener<EmailOffers> listener, Response.ErrorListener errorListener) {
        VolleyClient
                .getInstance(context)
                .getRequestQueue()
                .add(new GsonRequest<EmailOffers>(Request.Method.GET, GET_EMAIL_OFFERS_URL, null, EmailOffers.class, null, listener,
                        errorListener, VolleyClient.NO_RETRY_POLICY));
    }
}