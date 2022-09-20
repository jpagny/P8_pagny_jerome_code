package tourGuide;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.UserModel;
import tourGuide.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

public class GeneratorInternalUser {

    @Autowired
    UserService userService;

    private final Map<String, UserModel> internalUserMap = new HashMap<>();

    public GeneratorInternalUser(){
        initializeInternalUsers();
    }

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            UserModel userModel = new UserModel(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(userModel);

            internalUserMap.put(userName, userModel);
        });
    }

    public Map<String, UserModel>getInternalUserMap(){
        return internalUserMap;
    }

    private void generateUserLocationHistory(UserModel userModel) {
        IntStream.range(0, 3).forEach(i -> {
            userService.addToVisitedLocations(new VisitedLocation(userModel.getUserId(),
                    new Location(generateRandomLatitude(),
                            generateRandomLongitude()),
                    getRandomTime())
            );
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
