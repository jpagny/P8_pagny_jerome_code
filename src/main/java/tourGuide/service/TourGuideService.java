package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.UserModel;
import tourGuide.tracker.Tracker;
import tourGuide.model.UserRewardModel;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final GpsUtil gpsUtil;
    private final RewardService rewardService;
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;

    private final UserService userService;


    boolean testMode = true;


    public TourGuideService(GpsUtil gpsUtil, RewardService rewardService, UserService userService) {
        this.gpsUtil = gpsUtil;
        this.rewardService = rewardService;
        this.userService = userService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }


    public List<UserRewardModel> getUserRewards(UserModel userModel) {
        return userModel.getUserRewardModels();
    }

    public VisitedLocation getUserLocation(UserModel userModel) {
        VisitedLocation visitedLocation = (userModel.getVisitedLocations().size() > 0) ?
                userService.getLastVisitedLocation() :
                trackUserLocation(userModel);
        return visitedLocation;
    }

    public UserModel getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<UserModel> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void addUser(UserModel userModel) {
        if (!internalUserMap.containsKey(userModel.getUserName())) {
            internalUserMap.put(userModel.getUserName(), userModel);
        }
    }

    public List<Provider> getTripDeals(UserModel userModel) {
        int cumulatativeRewardPoints = userModel.getUserRewardModels().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, userModel.getUserId(), userModel.getUserPreferenceModel().getNumberOfAdults(),
                userModel.getUserPreferenceModel().getNumberOfChildren(), userModel.getUserPreferenceModel().getTripDuration(), cumulatativeRewardPoints);
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
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, UserModel> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            logger.debug("internalUser" +i);
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            UserModel userModel = new UserModel(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(userModel);

            internalUserMap.put(userName, userModel);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(UserModel userModel) {
        IntStream.range(0, 3).forEach(i -> {
            userService.addToVisitedLocations(new VisitedLocation(userModel.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
