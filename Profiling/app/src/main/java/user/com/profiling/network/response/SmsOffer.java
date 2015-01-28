package user.com.profiling.network.response;

/**
 * Created by suresh on 18/01/15.
 */
public class SmsOffer {
	private String profile, Body, Link, Unsubscribe, Sender;

	public String getSender() {
		return Sender;
	}

	public String getProfile() {
		return profile;
	}

	public String getBody() {
		return Body;
	}

	public String getLink() {
		return Link;
	}

	public String getUnsubscribe() {
		return Unsubscribe;
	}
}
