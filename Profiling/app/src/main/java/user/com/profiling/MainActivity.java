package user.com.profiling;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;

import user.com.profiling.network.ApiRequests;
import user.com.profiling.network.VolleyClient;
import user.com.profiling.network.response.EmailOffers;
import user.com.profiling.network.response.Offer;
import user.com.profiling.network.response.UserProfiling;


public class MainActivity extends ActionBarActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private String emailOffers = "{\n" +
            "    \"Emails\": [\n" +
            "        {\n" +
            "            \"US\": [\n" +
            "                {\n" +
            "                    \"profile\": \"Dater\",\n" +
            "                    \"Subject\": \"New Dater message for you\",\n" +
            "                    \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                    \"Link\": \"http://www.date.com\",\n" +
            "                    \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"profile\": \"Gamer\",\n" +
            "                    \"Subject\": \"New Gamer message for you\",\n" +
            "                    \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                    \"Link\": \"http://www.poker.com\",\n" +
            "                    \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"profile\": \"Dater\",\n" +
            "                    \"Subject\": \"New Dater2 message for you\",\n" +
            "                    \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                    \"Link\": \"http://www.cupid.com\",\n" +
            "                    \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "            {\n" +
            "                \"UK\": [\n" +
            "                    {\n" +
            "                        \"profile\": \"Dater\",\n" +
            "                        \"Subject\": \"New Dater message for you\",\n" +
            "                        \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                        \"Link\": \"http://www.date.com\",\n" +
            "                        \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"profile\": \"Gamer\",\n" +
            "                        \"Subject\": \"New Gamer message for you\",\n" +
            "                        \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                        \"Link\": \"http://www.poker.com\",\n" +
            "                        \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"profile\": \"Dater\",\n" +
            "                        \"Subject\": \"New Dater2 message for you\",\n" +
            "                        \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                        \"Link\": \"http://www.cupid.com\",\n" +
            "                        \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "                {\n" +
            "                    \"Default\": [\n" +
            "                        {\n" +
            "                            \"profile\": \"Default\",\n" +
            "                            \"Subject\": \"New Deals for you\",\n" +
            "                            \"Body\": \"Check this Link-> HYPERLINK\",\n" +
            "                            \"Link\": \"http://www.Dealextreme.com\",\n" +
            "                            \"Unsubscribe\": \"If you wish to stop getting this   mails -> HYPERLINK\"\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }],\n" +
            "                \"NextIntervalInHours\": \"5\"\n" +
            "            }";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getInstalledApps();
        //getUserProfiles();
        //getEmailOffers();
        Offer offer = new Gson().fromJson(emailOffers, EmailOffers.class).getEmails().get(0).get("US").get(0);
        ((TextView) findViewById(R.id.content)).setText(offer.getBody());
    }

    private void getUserProfiles() {
        ApiRequests.userProfiles(getApplicationContext(), new Response.Listener<UserProfiling>() {
            @Override
            public void onResponse(UserProfiling userProfiling) {
                Log.d(TAG, userProfiling.getInterestTH());
                Log.d(TAG, userProfiling.getUserProfilesJson().getProfiles().toString());
                ((TextView) findViewById(R.id.content)).setText(userProfiling.getUserProfilesJson().getProfiles().get(0).get("Dater").toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, String.valueOf(volleyError.getMessage()));
            }
        });
    }

    private void getEmailOffers() {
        ApiRequests.getEmailOffers(getApplicationContext(), new Response.Listener<EmailOffers>() {
            @Override
            public void onResponse(EmailOffers emailOffers) {
                Log.d(TAG, emailOffers.getEmails().get(0).get("US").toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, String.valueOf(volleyError.getMessage()));
            }
        });
    }

    private void getInstalledApps() {
        PackageManager packageManager = this.getPackageManager();
        List<ApplicationInfo> applist = packageManager.getInstalledApplications(0);
        for (ApplicationInfo applicationInfo : applist) {
            Log.d(TAG, String.valueOf(packageManager.getApplicationLabel(applicationInfo)));
        }

    }
}
