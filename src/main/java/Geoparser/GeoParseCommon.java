package Geoparser;

import java.io.File;
import java.util.*;

public class GeoParseCommon {
    public static GeoParseCommon _instance = new GeoParseCommon();
    public List<GazetteerObject> gazetteers = new ArrayList<>();

    public static void main(String[] args) {
        GeoParseCommon common = new GeoParseCommon();
        double x = common.levenshteinDistance("Rydal","Rydal Mount");
        System.out.println(x);
    }

    public GeoParseCommon(){
        try {
            loadGazetteer(new File("text/uk.txt"), "FULL_NAME_RO", "LAT", "LONG", "\n", "\t");
//            loadGazetteer(new File(getClass().getResource("uk.txt").getFile()), "FULL_NAME_RO", "LAT", "LONG", "\n", "\t");

        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static GeoParseCommon getInstance(){
        return _instance;
    }



    public Map<String,List<GazetteerLocationMatch>> findLocations(List<String> locations){

        Map<String,List<GazetteerLocationMatch>> locationMatches = new HashMap<>();
        for(String location : locations) {
            List<GazetteerLocationMatch> matches = findLocation(location);
            locationMatches.put(location,matches);
        }

        return locationMatches;
    }

    public List<GazetteerLocationMatch> findLocation(String location){
//        List<GazetteerLocationMatch> matches = new ArrayList<>();
//        for(GazetteerObject gazetteer : gazetteers){
//            for(Map<String,String> dataRow : gazetteer.getData()){
//                String gazLoc = dataRow.get(gazetteer.getLocationName());
//                if(location.equalsIgnoreCase(gazLoc)){
//                    matches.add(new GazetteerLocationMatch(gazLoc,GazetteerMatchType.FULL_MATCH,100,dataRow, dataRow.get(gazetteer.getLatColumn()), dataRow.get(gazetteer.getLongColumn())));
//                }
//                else if(gazLoc.toLowerCase().contains(location.toLowerCase()) == true){
//                    double score = levenshteinDistance(location,gazLoc);
//                    matches.add(new GazetteerLocationMatch(gazLoc,GazetteerMatchType.OUTER_PARTIAL_MATCH,50 + standardizeScore(score,location, 50),dataRow, dataRow.get(gazetteer.getLatColumn()), dataRow.get(gazetteer.getLongColumn())));
//                    //The 50+ (and 50 availablePercentage) is to weigh outer matches in the top 50%
//
//                }
//                else if(location.toLowerCase().contains(gazLoc.toLowerCase()) == true){
//                    double score = levenshteinDistance(location,gazLoc);
//                    matches.add(new GazetteerLocationMatch(gazLoc,GazetteerMatchType.INNER_PARTIAL_MATCH,standardizeScore(score,location, 50),dataRow, dataRow.get(gazetteer.getLatColumn()), dataRow.get(gazetteer.getLongColumn())));
//                    //The 50 availablePercentage is to weigh inner matches more toward the bottom 50%, because these are much less likely to be a real match
//                }
//            }
//        }

//        return matches;
    return null;
    }

    public void loadGazetteer(File file, String nameColumn, String latColumn, String longColumn, String lineDelimiter, String columnDelimiter) {
        try {
            Scanner scanner = new Scanner(file);

            scanner.useDelimiter(lineDelimiter);

            List<String> columnHeaders = new ArrayList<>();
            List<Map<String,String>> dataRows = new ArrayList<>();

            int line = 0;
            while (scanner.hasNext()) {
                String[] columns = scanner.next().split(columnDelimiter);

                if (line == 0) {
                    for (String column : columns) {
                        columnHeaders.add(column);
                    }

                    line++;
                } else {

                    Map<String, String> data = new HashMap<>();

                    for (int i = 0; i < columns.length; i++) {
                        if(i >= columnHeaders.size()){
                            System.out.println("somethings wrong");
                            continue;
                        }
                        data.put(columnHeaders.get(i), columns[i]);
                    }

                    dataRows.add(data);
                }
            }
            gazetteers.add(new GazetteerObject(file, nameColumn, latColumn, longColumn, dataRows, columnHeaders));
            scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void loadGazetteer(List<GazetteerObject> gazetteers) {
        for(GazetteerObject gazetteer : gazetteers) {
            try {
                Scanner scanner = new Scanner(new File(gazetteer.getFile().getPath()));

                scanner.useDelimiter(gazetteer.getLineDelimiter());

                List<String> columnHeaders = new ArrayList<>();
                List<Map<String, String>> dataRows = new ArrayList<>();

                int line = 0;
                while (scanner.hasNext()) {
                    String[] columns = scanner.next().split(gazetteer.getColumnDelimiter());

                    if (line == 0) {
                        for (String column : columns) {
                            columnHeaders.add(column);
                        }

                        line++;
                    } else {

                        Map<String, String> data = new HashMap<>();

                        for (int i = 0; i < columns.length; i++) {
                            if (i >= columnHeaders.size()) {
                                System.out.println("somethings wrong");
                                continue;
                            }
                            data.put(columnHeaders.get(i), columns[i]);
                        }

                        dataRows.add(data);
                    }
                }
                gazetteer.setData(dataRows);
                gazetteer.setColumnHeaders(columnHeaders);
                gazetteers.add(gazetteer);
                scanner.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public List<GazetteerObject> getGazetteers (){
        return gazetteers;
    }

    public double levenshteinDistance (String a, String b) {
        //This is an implementation if the levenshtein distance, which returns the number of changes required for  string a to equal string b.

        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public double standardizeScore(double score, String location, int availablePercentage){

        double standardScore = 0;
        // if there are more changes than the length of the location we are letting it have a 0 score.
        if(location.length() > score){
            standardScore = (1 - (score/location.length())) * availablePercentage;
        }
        return standardScore;
    }
}
