package user.com.profiling.network.response;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by suresh on 18/01/15.
 */
public class SmsOffers {
	ArrayList<HashMap<String, ArrayList<SmsOffer>>> messages;
	String NextIntervalInHours;

	public String getNextIntervalInHours() {
		return NextIntervalInHours;
	}

	public ArrayList<HashMap<String, ArrayList<SmsOffer>>> getMessages() {
		return messages;
	}
}
