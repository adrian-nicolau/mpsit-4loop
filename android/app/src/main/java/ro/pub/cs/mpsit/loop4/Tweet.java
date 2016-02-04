package ro.pub.cs.mpsit.loop4;

/**
 * Tweet container.
 */
public class Tweet {

    protected String id;
    protected String date;
    protected String message;
    protected double lon, lat;

    public Tweet(String id, String date, String message, double lon, double lat) {
        this.id = id;
        this.date = date;
        this.message = message;
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
