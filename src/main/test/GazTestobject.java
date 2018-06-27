import Commons.Geo.GeoCommon;

public class GazTestobject {
    public String location;
    public double CD_lat;
    public double CD_lng;
    public double GEOFF_lat;
    public double GEOFF_lng;
    public double GC_lat;
    public double GC_lng;

    private double GC_GEOFF_Dist;
    private double CD_GEOFF_Dist;
    private double GC_CD_Dist;

    public GazTestobject(){

    }

    public void calcDistances(){
        if(GEOFF_lat != 0 && GEOFF_lng != 0 && GC_lat != 0 && GC_lng !=0){
            GC_GEOFF_Dist = GeoCommon.getInstance().greaterSphereDistance(GEOFF_lat,GEOFF_lng,GC_lat,GC_lng);
        }
        if(GEOFF_lat != 0 && GEOFF_lng != 0 && CD_lat != 0 && CD_lng !=0){
            CD_GEOFF_Dist = GeoCommon.getInstance().greaterSphereDistance(GEOFF_lat,GEOFF_lng,CD_lat,CD_lng);
        }
        if(CD_lat != 0 && CD_lng != 0 && GC_lat != 0 && GC_lng !=0){
            GC_CD_Dist = GeoCommon.getInstance().greaterSphereDistance(CD_lat,CD_lng,GC_lat,GC_lng);
        }
    }

    public boolean isEmpty(){
        if(CD_lat == 0 && CD_lng == 0 && GC_lat == 0 && GC_lng ==0 && GEOFF_lat == 0 && GEOFF_lng == 0){
            return true;
        }
        else{
            return false;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getCD_lat() {
        return CD_lat;
    }

    public void setCD_lat(double CD_lat) {
        this.CD_lat = CD_lat;
    }

    public double getCD_lng() {
        return CD_lng;
    }

    public void setCD_lng(double CD_lng) {
        this.CD_lng = CD_lng;
    }

    public double getGEOFF_lat() {
        return GEOFF_lat;
    }

    public void setGEOFF_lat(double GEOFF_lat) {
        this.GEOFF_lat = GEOFF_lat;
    }

    public double getGEOFF_lng() {
        return GEOFF_lng;
    }

    public void setGEOFF_lng(double GEOFF_lng) {
        this.GEOFF_lng = GEOFF_lng;
    }

    public double getGC_lat() {
        return GC_lat;
    }

    public void setGC_lat(double GC_lat) {
        this.GC_lat = GC_lat;
    }

    public double getGC_lng() {
        return GC_lng;
    }

    public void setGC_lng(double GC_lng) {
        this.GC_lng = GC_lng;
    }

    public double getGC_GEOFF_Dist() {
        return GC_GEOFF_Dist;
    }

    public void setGC_GEOFF_Dist(double GC_GEOFF_Dist) {
        this.GC_GEOFF_Dist = GC_GEOFF_Dist;
    }

    public double getCD_GEOFF_Dist() {
        return CD_GEOFF_Dist;
    }

    public void setCD_GEOFF_Dist(double CD_GEOFF_Dist) {
        this.CD_GEOFF_Dist = CD_GEOFF_Dist;
    }

    public double getGC_CD_Dist() {
        return GC_CD_Dist;
    }

    public void setGC_CD_Dist(double GC_CD_Dist) {
        this.GC_CD_Dist = GC_CD_Dist;
    }
}
