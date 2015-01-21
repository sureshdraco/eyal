package user.com.profiling;

import com.google.gson.Gson;

import user.com.profiling.network.response.UserProfilesJson;
import user.com.profiling.network.response.UserProfiling;

/**
 * Created by suresh.kumar on 2015-01-21.
 */
public class test {
	public static void main(String a[]) {
		String json = "{\"UserProfilesJson\": \n" +
                "\t{\"Profiles\":[\n" +
                "\t\t{\"Dater\":\"date, tinder, dating, love, cupid\",\n" +
                "\t\t\"Gambler\":\"poker, slot, texas, card, roullete\",\"Financer\":\"finance, insurance, money\"}\n" +
                "\t]},\n" +
                "\t\"InterestTH\":\"3\"\n" +
                "}";
		System.out.println(new Gson().fromJson(json, UserProfiling.class).getUserProfilesJson().getProfiles().get(0).get("Gambler"));
	}
}
