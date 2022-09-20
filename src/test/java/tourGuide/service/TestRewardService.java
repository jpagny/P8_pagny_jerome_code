package tourGuide.service;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

import gpsUtil.location.Location;
import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.UserModel;
import tourGuide.model.UserRewardModel;

public class TestRewardService {

	@Autowired
	private UserService userService;



	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(),userService);

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardService,userService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		userService.addToVisitedLocations(new VisitedLocation(userModel.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(userModel);
		List<UserRewardModel> userRewardModels = userModel.getUserRewardModels();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewardModels.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(),userService);
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardService.isWithinAttractionProximity(attraction, attraction));
	}
	
	@Ignore // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(),userService);
		//rewardService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardService,userService);
		
		rewardService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserRewardModel> userRewardModels = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewardModels.size());
	}


	
}
