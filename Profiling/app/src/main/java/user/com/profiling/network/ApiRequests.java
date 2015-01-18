package user.com.profiling.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import user.com.profiling.network.response.EmailOffers;
import user.com.profiling.network.response.UserProfiling;

/**
 * Created by suresh on 12/10/14.
 */
public class ApiRequests {
    public static final String GET_USER_PROFILES_URL = "http://cdn2.appicano.com/CPLOffers/Profiles.txt";
    public static final String GET_EMAIL_OFFERS_URL = "http://cdn2.appicano.com/CPLOffers/EOffers.txt";

    public static void userProfiles(Context context, Response.Listener<UserProfiling> listener, Response.ErrorListener errorListener) {
        VolleyClient
                .getInstance(context)
                .getRequestQueue()
                .add(new GsonRequest<UserProfiling>(Request.Method.GET, GET_USER_PROFILES_URL, null, UserProfiling.class, null, listener,
                        errorListener, VolleyClient.NO_RETRY_POLICY));
    }

    public static void getEmailOffers(Context context, Response.Listener<EmailOffers> listener, Response.ErrorListener errorListener) {
        VolleyClient
                .getInstance(context)
                .getRequestQueue()
                .add(new GsonRequest<EmailOffers>(Request.Method.GET, GET_EMAIL_OFFERS_URL, null, EmailOffers.class, null, listener,
                        errorListener, VolleyClient.NO_RETRY_POLICY));
    }
}