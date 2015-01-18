
package user.com.profiling.network.response;

public class UserProfiling {
   	private String InterestTH;
   	private UserProfilesJson UserProfilesJson;

 	public String getInterestTH(){
		return this.InterestTH;
	}
	public void setInterestTH(String interestTH){
		this.InterestTH = interestTH;
	}
 	public UserProfilesJson getUserProfilesJson(){
		return this.UserProfilesJson;
	}
	public void setUserProfilesJson(UserProfilesJson userProfilesJson){
		this.UserProfilesJson = userProfilesJson;
	}
}
