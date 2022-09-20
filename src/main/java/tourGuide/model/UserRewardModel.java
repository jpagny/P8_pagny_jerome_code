package tourGuide.model;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserRewardModel {
    public final VisitedLocation visitedLocation;
    public final Attraction attraction;
    private int rewardPoints;


}
