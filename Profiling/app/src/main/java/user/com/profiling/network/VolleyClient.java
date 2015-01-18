package user.com.profiling.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

public class VolleyClient {
	private static final int NETWORK_THREAD_POOL_SIZE = 1;
	private static VolleyClient mInstance;
	private RequestQueue mRequestQueue;
	private static Context mCtx;
	private static final String DEFAULT_CACHE_DIR = "volley";

	public static final RetryPolicy NO_RETRY_POLICY = new DefaultRetryPolicy(0, 0, 0);

	private VolleyClient(Context context) {
		mCtx = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized VolleyClient getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new VolleyClient(context);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = VolleyClient.newRequestQueue(mCtx.getApplicationContext(), new HttpClientStack(new DefaultHttpClient()));
		}
		return mRequestQueue;
	}

	// Creates requestqueue with thread pool size 1
	public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}
		Network network = new BasicNetwork(stack);
		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network, NETWORK_THREAD_POOL_SIZE);
		queue.start();
		return queue;
	}
}
