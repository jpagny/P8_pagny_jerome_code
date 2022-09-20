package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tourGuide.model.UserModel;
import tourGuide.model.UserRewardModel;

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

    public void addUserReward(UserRewardModel userRewardModel) {
        if (userModel.getUserRewardModels().stream().filter(theReward -> !theReward.attraction.attractionName.equals(userRewardModel.attraction)).count() == 0) {
            userModel.getUserRewardModels().add(userRewardModel);
        }
    }

    public VisitedLocation getLastVisitedLocation() {
        return userModel.getVisitedLocations().get(userModel.getVisitedLocations().size() - 1);
    }

}
