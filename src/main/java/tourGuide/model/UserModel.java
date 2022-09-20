package tourGuide.model;

import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Component
public class UserModel {

    private UUID userId;
    private String userName;
    private String phoneNumber;
    private String emailAddress;
    private Date latestLocationTimestamp;
    private List<VisitedLocation> visitedLocations = new ArrayList<>();
    private List<UserRewardModel> userRewardModels = new ArrayList<>();
    private UserPreferenceModel userPreferenceModel = new UserPreferenceModel();
    private List<Provider> tripDeals = new ArrayList<>();

    public UserModel(UUID userId, String userName, String phoneNumber, String emailAddress) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public UserModel() {

    }

}
