package tourGuide.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardService;
import tourGuide.service.UserService;

@Configuration
@AllArgsConstructor
public class ModuleConfiguration {

	private final UserService userService;
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
	@Bean
	public RewardService getRewardsService() {
		return new RewardService(getGpsUtil(), getRewardCentral(), userService);
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}
