package user.com.profiling.network.response;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by suresh on 18/01/15.
 */
public class EmailOffers {
    ArrayList<HashMap<String, ArrayList<Offer>>> Emails;

    public ArrayList<HashMap<String, ArrayList<Offer>>> getEmails() {
        return Emails;
    }
}
