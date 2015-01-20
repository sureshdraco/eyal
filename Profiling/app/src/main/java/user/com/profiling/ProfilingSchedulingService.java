package user.com.profiling;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import user.com.profiling.network.ApiRequests;
import user.com.profiling.network.response.UserProfiling;

/**
 * This {@code IntentService} does the app's actual work. {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a partial wake lock for this service while the
 * service does its work. When the service is finished, it calls {@code completeWakefulIntent()} to release the wake lock.
 */
public class ProfilingSchedulingService extends IntentService {
	private static final String TAG = ProfilingSchedulingService.class.getSimpleName();
	private static final String DATER = "Dater";
	private static final String GAMBLER = "Gambler";
	private static final String FINANCER = "Financer";

	public ProfilingSchedulingService() {
		super("SchedulingService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		startProfiling();
		// SampleAlarmReceiver.completeWakefulIntent(intent);
	}

	private void startProfiling() {
		// get profile data
		getUserProfiles();
		// get browser history
		// get installed apps
		// calculate threshold
		// email offer
		// sms offer
	}

	private void getUserProfiles() {
		UserProfiling userProfiling = ApiRequests.userProfiles(getApplicationContext());
		Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().get(0).get(DATER));
		Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().get(0).get(GAMBLER));
		Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().get(0).get(FINANCER));
	}
}