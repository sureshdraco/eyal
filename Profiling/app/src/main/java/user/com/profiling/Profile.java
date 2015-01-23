package user.com.profiling;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by suresh.kumar on 2015-01-23.
 */
public class Profile {
	String profileName;
	ArrayList<String> keywords = new ArrayList<>();
	ArrayList<String> matchedContents = new ArrayList<>();
	int interest = 0;

	public Profile(String profileName, int interest) {
		this.profileName = profileName;
		this.interest = interest;
	}

	public String getProfileName() {
		return profileName;
	}

	public int getInterest() {
		return interest;
	}

	public void addInterest(int interest) {
		this.interest += interest;
	}

	public void setKeywords(String commaSeparatedKeyword) {
		try {
			keywords = new ArrayList<>(Arrays.asList(commaSeparatedKeyword.split(",")));
		} catch (Exception ignored) {
		}
	}

	public static ArrayList<Profile> getProfileInterestList(ArrayList<String> profileNames) {
		ArrayList<Profile> profileInterests = new ArrayList<>();
		for (String profileName : profileNames) {
			profileInterests.add(new Profile(profileName, 0));
		}
		return profileInterests;
	}

	public void updateInteret(ArrayList<String> contentList) {
		for (String keyword : keywords) {
			for (String content : contentList) {
				if (content.contains(keyword)) {
					matchedContents.add(content);
					interest++;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Profile{" +
				"profileName='" + profileName + '\'' +
				", keywords=" + keywords +
				", matchedContents=" + matchedContents +
				", interest=" + interest +
				'}';
	}
}