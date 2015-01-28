package user.com.profiling;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.gson.Gson;

import user.com.profiling.network.response.EmailOffer;
import user.com.profiling.network.response.EmailOffers;

public class MainActivity extends ActionBarActivity {

    private static final String CONFIRM_DIALOG_SHOWN = "confirmDialogShown";
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
        setContentView(R.layout.confirm_dialog);
        EmailOffer offer = new Gson().fromJson(emailOffers, EmailOffers.class).getEmails().get(0).get("US").get(0);
        initView();
    }

    private void initView() {
        final Button confirmBtn = (Button) findViewById(R.id.confirm);
        confirmBtn.setEnabled(false);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(CONFIRM_DIALOG_SHOWN, true).commit();
                Intent service = new Intent(getApplicationContext(), ProfilingSchedulingService.class);
                startService(service);
                finish();
            }
        });

        Button bLater = (Button) findViewById(R.id.confirmLater);
        bLater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final CheckBox checkBox = (CheckBox) findViewById(R.id.confirmCheckBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    confirmBtn.setEnabled(true);
                } else {
                    confirmBtn.setEnabled(false);
                }
            }
        });
        findViewById(R.id.confirmText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
            }
        });
    }
}
