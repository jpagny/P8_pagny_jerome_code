package tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardService;
import tourGuide.service.TourGuideService;
import tourGuide.model.UserModel;
import tourGuide.service.UserService;

public class TestPerformance {

	@Autowired
	private UserService userService;
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	
	@Ignore
	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(),userService);
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardService,userService);

		List<UserModel> allUserModels = new ArrayList<>();
		allUserModels = tourGuideService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(UserModel userModel : allUserModels) {
			tourGuideService.trackUserLocation(userModel);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
	@Ignore
	@Test
	public void highVolumeGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(),userService);

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardService,userService);
		
	    Attraction attraction = gpsUtil.getAttractions().get(0);
		List<UserModel> allUserModels = new ArrayList<>();
		allUserModels = tourGuideService.getAllUsers();
		allUserModels.forEach(u -> userService.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));
	     
	    allUserModels.forEach(u -> rewardService.calculateRewards(u));
	    
		for(UserModel userModel : allUserModels) {
			assertTrue(userModel.getUserRewardModels().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
	
}
