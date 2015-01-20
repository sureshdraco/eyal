package user.com.profiling;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.Browser;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	private final static long THIRTY_DAYS_IN_MILLS = 2592000000l;

	private ArrayList<String> browserHistory = new ArrayList<>();
	private ArrayList<String> appNames = new ArrayList<>();
	private ArrayList<String> daterKeywords = new ArrayList<>();
	private ArrayList<String> gamblerKeywords = new ArrayList<>();
	private ArrayList<String> financerKeywords = new ArrayList<>();

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
        getBrowserHistory();
        getInstalledApps();
		// get browser history

		// get installed apps
		// calculate threshold
		// email offer
		// sms offer
	}

	private void getBrowserHistory() {
		String[] projection = new String[] {
				Browser.BookmarkColumns.DATE
				, Browser.BookmarkColumns.URL
		};
		Cursor mCur = getContentResolver().query(android.provider.Browser.BOOKMARKS_URI,
				projection, null, null, Browser.BookmarkColumns.DATE + " DESC"
				);
		mCur.moveToFirst();
		int urlIdx = mCur.getColumnIndex(Browser.BookmarkColumns.URL);
		int dateIdx = mCur.getColumnIndex(Browser.BookmarkColumns.DATE);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		long currentTimeInMills = new Date().getTime();
		long thirtyDaysBefore = currentTimeInMills - THIRTY_DAYS_IN_MILLS;
		while (!mCur.isAfterLast()) {
			if (mCur.getLong(dateIdx) > thirtyDaysBefore) {
				Log.d("browser", "Url: " + mCur.getString(urlIdx));
				Log.d("browser", "Date: " + simpleDateFormat.format(new Date(mCur.getLong(dateIdx))));
				mCur.moveToNext();
			} else {
				break;
			}
		}
		mCur.close();
	}

	private void getInstalledApps() {
		PackageManager packageManager = this.getPackageManager();
		List<ApplicationInfo> applist = packageManager.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : applist) {
			Log.d(TAG, String.valueOf(packageManager.getApplicationLabel(applicationInfo)));
		}
	}

	private void getUserProfiles() {
		UserProfiling userProfiling = ApiRequests.userProfiles(getApplicationContext());
		Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().get(0).get(DATER));
		Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().get(0).get(GAMBLER));
		Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().get(0).get(FINANCER));
	}
}