package user.com.profiling;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.Browser;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import user.com.profiling.network.ApiRequests;
import user.com.profiling.network.response.UserProfiling;

/**
 * This {@code IntentService} does the app's actual work. {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a partial wake lock for this service while the
 * service does its work. When the service is finished, it calls {@code completeWakefulIntent()} to release the wake lock.
 */
public class ProfilingSchedulingService extends IntentService {
	private static final String TAG = ProfilingSchedulingService.class.getSimpleName();
	private final static long THIRTY_DAYS_IN_MILLS = 2592000000l;

	private ArrayList<String> browserHistory = new ArrayList<>();
	private ArrayList<String> appNames = new ArrayList<>();
	private ArrayList<Profile> profileInterests = new ArrayList<>();
	private int interestTh;
	HashMap<String, String> userProfileMap = new HashMap<>();

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
		if (!isValidUserProfileData()) {
			Toast.makeText(getApplicationContext(), "Not valid User profile", Toast.LENGTH_LONG).show();
			return;
		}
		getBrowserHistory();
		getInstalledApps();
		generateProfiles();
		ArrayList<Profile> profilesAboveThreshold = getProfilesAboveThreshold();
		if (BuildConfig.DEBUG) Log.d(TAG, profilesAboveThreshold.toString());
		// email offer
		// sms offer
	}

	private void generateProfiles() {
		for (int i = 0; i < profileInterests.size(); i++) {
			String keywords = userProfileMap.get(profileInterests.get(i).getProfileName());
			profileInterests.get(i).setKeywords(keywords);
			if (!browserHistory.isEmpty()) {
				profileInterests.get(i).updateInteret(browserHistory);
			}
			if (!appNames.isEmpty()) {
				profileInterests.get(i).updateInteret(appNames);
			}
		}
	}

	private boolean isValidUserProfileData() {
		return !profileInterests.isEmpty() && interestTh != -1;
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
		long currentTimeInMills = new Date().getTime();
		long thirtyDaysBefore = currentTimeInMills - THIRTY_DAYS_IN_MILLS;
		while (!mCur.isAfterLast()) {
			if (mCur.getLong(dateIdx) > thirtyDaysBefore) {
				browserHistory.add(mCur.getString(urlIdx));
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
			appNames.add(String.valueOf(packageManager.getApplicationLabel(applicationInfo)));
		}
	}

	private void getUserProfiles() {
		try {
			UserProfiling userProfiling = ApiRequests.userProfiles(getApplicationContext());
			userProfileMap = userProfiling.getUserProfilesJson().getProfiles().get(0);
			profileInterests = Profile.getProfileInterestList(new ArrayList<>(userProfileMap.keySet()));
			interestTh = Integer.parseInt(userProfiling.getInterestTH());
			if (BuildConfig.DEBUG) Log.d(TAG, profileInterests.toString());
		} catch (Exception ignored) {
			interestTh = -1;
		}
	}

	public ArrayList<Profile> getProfilesAboveThreshold() {
		ArrayList<Profile> profilesAboveThreshold = new ArrayList<>();
		for (Profile profile : profileInterests) {
			if (profile.getInterest() > interestTh) {
				profilesAboveThreshold.add(profile);
			}
		}
		return profilesAboveThreshold;
	}
}