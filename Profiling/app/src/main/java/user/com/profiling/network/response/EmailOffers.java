package user.com.profiling.network.response;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by suresh on 18/01/15.
 */
public class EmailOffers {
	ArrayList<HashMap<String, ArrayList<EmailOffer>>> Emails;
	String NextIntervalInHours;

    public String getNextIntervalInHours() {
        return NextIntervalInHours;
    }

    public ArrayList<HashMap<String, ArrayList<EmailOffer>>> getEmails() {
		return Emails;
	}
}
