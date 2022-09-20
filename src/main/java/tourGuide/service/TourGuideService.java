package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.model.UserModel;
import tourGuide.model.UserRewardModel;
import tourGuide.tracker.Tracker;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TourGuideService {

    private final GpsUtil gpsUtil;
    private final RewardService rewardService;
    private final UserService userService;

    public final Tracker tracker;

    private final Map<String, UserModel> internalUserMap = new HashMap<>();
    private static final String tripPricerApiKey = "test-server-api-key";
    private final TripPricer tripPricer = new TripPricer();


    public TourGuideService(GpsUtil gpsUtil, RewardService rewardService, UserService userService) {
        this.gpsUtil = gpsUtil;
        this.rewardService = rewardService;
        this.userService = userService;

        tracker = new Tracker(this);
        addShutDownHook();
    }


    public List<UserRewardModel> getUserRewards(UserModel userModel) {
        return userModel.getUserRewardModels();
    }

    public VisitedLocation getUserLocation(UserModel userModel) {
        return (userModel.getVisitedLocations().size() > 0) ?
                userService.getLastVisitedLocation() :
                trackUserLocation(userModel);
    }

    public UserModel getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<UserModel> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    public void addUser(UserModel userModel) {
        if (!internalUserMap.containsKey(userModel.getUserName())) {
            internalUserMap.put(userModel.getUserName(), userModel);
        }
    }

    public List<Provider> getTripDeals(UserModel userModel) {
        int cumulativeRewardPoints = userModel.getUserRewardModels().stream().mapToInt(UserRewardModel::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, userModel.getUserId(), userModel.getUserPreferenceModel().getNumberOfAdults(),
                userModel.getUserPreferenceModel().getNumberOfChildren(), userModel.getUserPreferenceModel().getTripDuration(), cumulativeRewardPoints);
        userModel.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(UserModel userModel) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(userModel.getUserId());
        userService.addToVisitedLocations(visitedLocation);
        rewardService.calculateRewards(userModel);
        return visitedLocation;
    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtil.getAttractions()) {
            if (rewardService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }
        }

        return nearbyAttractions;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
    }


}
