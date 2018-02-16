package core;

public class Route {

    int sid;
    int bid;
    String description;
    double length;
    int numberVotes;
    double averageVotes;
    String waypoints;

    public Route(int sid, int bid, String description, double length, int numberVotes, double averageVotes, String waypoints) {
        this.sid = sid;
        this.bid = bid;
        this.description = description;
        this.length = length;
        this.numberVotes = numberVotes;
        this.averageVotes = averageVotes;
        this.waypoints = waypoints;
    }

    public int getId() {
        return sid;
    }

    public int getOwner() {
        return bid;
    }

    public void setOwner(int ownerId) {
        this.bid = bid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getNumberVotes() {
        return numberVotes;
    }

    public void setNumberVotes(int numberVotes) {
        this.numberVotes = numberVotes;
    }

    public double getAverageVotes() {
        return averageVotes;
    }

    public void setAverageVotes(double averageVotes) {
        this.averageVotes = averageVotes;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

}
