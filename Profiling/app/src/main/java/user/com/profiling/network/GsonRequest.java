package user.com.profiling.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class GsonRequest<T> extends Request<T> {
    private static final String TAG = GsonRequest.class.getSimpleName();
    private final Gson gson = new Gson();
    private final Class<T> response;
    private Map<String, String> headers;
    private final Listener<T> listener;
    private final ErrorListener errorListener;
    private final Object request;
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json");

    /**
     * @param method   method type of the request (Get, Post)
     * @param url      URL of the request to make
     * @param response Relevant class object, for Gson's reflection
     * @param headers  Map of request headers
     */
    public GsonRequest(int method, String url, Object request, Class<T> response, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.errorListener = errorListener;
        this.response = response;
        this.headers = headers;
        this.listener = listener;
        this.request = request;
    }

    public GsonRequest(int method, String url, Object request, Class<T> response, Map<String, String> headers,
                       Listener<T> listener, ErrorListener errorListener, RetryPolicy retryPolicy) {
        this(method, url, request, response, headers, listener, errorListener);
        if (retryPolicy != null) {
            setRetryPolicy(retryPolicy);
        }
    }

    /**
     * @param method   method type of the request (Get, Post etc)
     * @param url      URL of the request to make
     * @param response Relevant class object, for Gson's reflection
     */
    public GsonRequest(int method, String url, Object request, Class<T> response,
                       Listener<T> listener, ErrorListener errorListener) {
        this(method, url, request, response, null, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json;
            json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("GsonRequest", json);
            T result = gson.fromJson(json, this.response);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        String body = "";
        if (request != null) {
            body = gson.toJson(request);
        }
        Log.d(TAG, body);
        return body.getBytes();
    }

    public Object getRequest() {
        return request;
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public Class<T> getResponse() {
        return response;
    }

    public Listener<T> getListener() {
        return listener;
    }
}