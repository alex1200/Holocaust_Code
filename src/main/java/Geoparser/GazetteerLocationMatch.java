package Geoparser;

import java.util.Map;

public class GazetteerLocationMatch {
    public String data;
    public String location;
    public String gazetteer;
    public GazetteerMatchType matchType;
    public double confidence;
    public Map<String,String> gazDataRow;
    public Map<Integer,String> gazDataRowInt;

    public double latitude = 0;
    public double longitude = 0;

    public GazetteerLocationMatch(String inLocation, GazetteerMatchType inMatchType,double inConfidence, Map<String,String> inGazDataRow, String inLatitude, String inLongitude, String inGazetteer){
        location = inLocation;
        matchType = inMatchType;
        confidence = inConfidence;
        gazDataRow = inGazDataRow;
        gazetteer = inGazetteer;

        try {
            latitude = Double.parseDouble(inLatitude);
            longitude = Double.parseDouble(inLongitude);
        }
        catch (Exception e){
        }
    }

    public GazetteerLocationMatch(String inLocation, GazetteerMatchType inMatchType, double inConfidence, String inGazDataRow, String inLatitude, String inLongitude, String inGazetteer) {
        location = inLocation;
        matchType = inMatchType;
        confidence = inConfidence;
        data = inGazDataRow;
        gazetteer = inGazetteer;

        try {
            latitude = Double.parseDouble(inLatitude);
            longitude = Double.parseDouble(inLongitude);
        }
        catch (Exception e){
        }

    }

//    public GazetteerLocationMatch(String inLocation, GazetteerMatchType inMatchType,double inConfidence, Map<Integer,String> inGazDataRow, String inLatitude, String inLongitude, String inGazetteer){
//        location = inLocation;
//        matchType = inMatchType;
//        confidence = inConfidence;
//        gazDataRowInt = inGazDataRow;
//        gazetteer = inGazetteer;
//
//        try {
//            latitude = Double.parseDouble(inLatitude);
//            longitude = Double.parseDouble(inLongitude);
//        }
//        catch (Exception e){
//        }
//    }
}
