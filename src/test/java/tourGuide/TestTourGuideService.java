package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.UserModel;
import tripPricer.Provider;

public class TestTourGuideService {

	@Test
	public void getUserLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(userModel);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(userModel.getUserId()));
	}
	
	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserModel userModel2 = new UserModel(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(userModel);
		tourGuideService.addUser(userModel2);
		
		UserModel retrivedUserModel = tourGuideService.getUser(userModel.getUserName());
		UserModel retrivedUser2Model = tourGuideService.getUser(userModel2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(userModel, retrivedUserModel);
		assertEquals(userModel2, retrivedUser2Model);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserModel userModel2 = new UserModel(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(userModel);
		tourGuideService.addUser(userModel2);
		
		List<UserModel> allUserModels = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUserModels.contains(userModel));
		assertTrue(allUserModels.contains(userModel2));
	}
	
	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(userModel);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(userModel.getUserId(), visitedLocation.userId);
	}
	
	@Ignore // Not yet implemented
	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(userModel);
		
		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		UserModel userModel = new UserModel(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(userModel);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}
	
	
}
