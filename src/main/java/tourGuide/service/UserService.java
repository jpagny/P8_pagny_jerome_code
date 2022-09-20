package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tourGuide.model.UserModel;
import tourGuide.user.UserReward;

@Service
@AllArgsConstructor
public class UserService {

    private final UserModel userModel;


    public void addToVisitedLocations(VisitedLocation visitedLocation) {
        userModel.getVisitedLocations().add(visitedLocation);
    }

    public void clearVisitedLocations() {
        userModel.getVisitedLocations().clear();
    }

    public void addUserReward(UserReward userReward) {
        if (userModel.getUserRewards().stream().filter(theReward -> !theReward.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
            userModel.getUserRewards().add(userReward);
        }
    }

    public VisitedLocation getLastVisitedLocation() {
        return userModel.getVisitedLocations().get(userModel.getVisitedLocations().size() - 1);
    }

}
