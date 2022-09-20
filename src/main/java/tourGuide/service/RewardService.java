package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.configuration.DefaultConfiguration;
import tourGuide.model.UserModel;
import tourGuide.model.UserRewardModel;

import java.util.List;

@Service
public class RewardService {

    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;
    private final UserService userService;
    private final int proximityBuffer;

    public RewardService(GpsUtil gpsUtil, RewardCentral rewardCentral, UserService userService) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
        this.userService = userService;
        this.proximityBuffer = DefaultConfiguration.DEFAULT_PROXIMITY_BUFFER;
    }

    public void calculateRewards(UserModel userModel) {
        List<VisitedLocation> userLocations = userModel.getVisitedLocations();
        List<Attraction> attractions = gpsUtil.getAttractions();

        for (VisitedLocation visitedLocation : userLocations) {
            for (Attraction attraction : attractions) {
                if (userModel.getUserRewardModels().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
                    if (nearAttraction(visitedLocation, attraction)) {
                        userService.addUserReward(new UserRewardModel(visitedLocation, attraction, getRewardPoints(attraction, userModel)));
                    }
                }
            }
        }
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return !(getDistance(attraction, location) > DefaultConfiguration.ATTRACTION_PROXIMITY_RANGE);
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    private int getRewardPoints(Attraction attraction, UserModel userModel) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userModel.getUserId());
    }

    private double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return DefaultConfiguration.STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }

}
