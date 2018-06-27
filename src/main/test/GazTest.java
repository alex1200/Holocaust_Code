import Commons.File.FileCommons;
import Commons.Geo.GeoLocation;
import Geoparser.GazetteerObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class GazTest {

    public static void main(String[] args) {
        List<GazetteerObject> gazetteers = new ArrayList<>();
//        File file = new File("text/GazRevDups.txt");
//
//        if(file.exists()){
//            System.out.println("file exists");
//            System.out.println(file.getAbsolutePath());
//        }

        try {
//            Scanner scanner = new Scanner(new BufferedReader(new FileReader("GazRevDups.txt")));
            Scanner scanner = new Scanner(new FileInputStream("GazRevDups.txt"), "ISO8859_1");

            String linedel = "\n";
            scanner.useDelimiter(linedel);

            Map<String,Map<String,GeoLocation>> locations = new HashMap<>();
            int line = 0;

            while (scanner.hasNext()) {
                String[] columns = scanner.next().split("\t");
                if(locations.containsKey(columns[1]) == false){
                    System.out.println(columns[1]);
                    Map<String,GeoLocation> entry = new HashMap<>();
                    GeoLocation geo = new GeoLocation();
                    try {
                        geo.setLat(Double.parseDouble(columns[2]));
                        geo.setLng(Double.parseDouble(columns[3]));
                    }catch (Exception e){
                        System.out.println(e);
                        System.out.println(columns[2]+","+columns[3]);
                        geo.setLat(0);
                        geo.setLng(0);
                    }
                    entry.put(columns[0],geo) ;
                    locations.put(columns[1],entry);
                }
                else{
                    Map<String,GeoLocation> entry = locations.get(columns[1]);
                    GeoLocation geo = new GeoLocation();
                    try {
                        geo.setLat(Double.parseDouble(columns[2]));
                        geo.setLng(Double.parseDouble(columns[3]));
                    }catch (Exception e){
                        System.out.println(e);
                        System.out.println(columns[2]+","+columns[3]);
                        geo.setLat(0);
                        geo.setLng(0);
                    }
                    entry.put(columns[0],geo) ;
                    locations.put(columns[1],entry);
                }

            }
            scanner.close();

            List<GazTestobject> allLocs = new ArrayList<>();
            StringBuilder cdtable = new StringBuilder();
            StringBuilder cdrow = new StringBuilder();
            cdrow.append("Location");
            cdrow.append("\t");
            cdrow.append("Camps Definitive Lat");
            cdrow.append("\t");
            cdrow.append("Camps Definitive Long");
            cdrow.append("\t");
            cdrow.append("GEOFF Lat");
            cdrow.append("\t");
            cdrow.append("GEOFF Long");
            cdrow.append("\t");
//            cdrow.append("Ghetto Complete Lat");
//            cdrow.append("\t");
//            cdrow.append("Ghetto Complete Long");
//            cdrow.append("\t");
            cdrow.append("Distance Between Camps Def. and GEOFF");
            cdrow.append("\t");
//            cdrow.append("Distance Between Camps Def. and Ghetto Comp");
//            cdrow.append("\t");
//            cdrow.append("Distance Between GEOFF and Ghetto Comp");
            cdrow.append("\n");
            cdtable.append(cdrow.toString());

            StringBuilder gctable = new StringBuilder();
            StringBuilder gcrow = new StringBuilder();
            gcrow.append("Location");
            gcrow.append("\t");
//            gcrow.append("Camps Definitive Lat");
//            gcrow.append("\t");
//            gcrow.append("Camps Definitive Long");
//            gcrow.append("\t");
            gcrow.append("GEOFF Lat");
            gcrow.append("\t");
            gcrow.append("GEOFF Long");
            gcrow.append("\t");
            gcrow.append("Ghetto Complete Lat");
            gcrow.append("\t");
            gcrow.append("Ghetto Complete Long");
            gcrow.append("\t");
//            gcrow.append("Distance Between Camps Def. and GEOFF");
//            gcrow.append("\t");
//            gcrow.append("Distance Between Camps Def. and Ghetto Comp");
//            gcrow.append("\t");
            gcrow.append("Distance Between GEOFF and Ghetto Comp");
            gcrow.append("\n");
            gctable.append(gcrow.toString());

            for(Map.Entry<String, Map<String,GeoLocation>> entry : locations.entrySet()){
                GazTestobject location = new GazTestobject();
                location.setLocation(entry.getKey());
                Map<String,GeoLocation> coorMap = entry.getValue();
                for(Map.Entry<String, GeoLocation> coors : coorMap.entrySet()){
                    GeoLocation geo = coors.getValue();
                    if(coors.getKey().equalsIgnoreCase("Ghettos_correct")){
                        location.setGC_lat(geo.getLng());
                        location.setGC_lng(geo.getLat());
                    }
                    else if(coors.getKey().equalsIgnoreCase("Camps_Definitive")){
                        location.setCD_lat(geo.getLat());
                        location.setCD_lng(geo.getLng());
                    }
                    else if(coors.getKey().equalsIgnoreCase("GEOFF")){
                        location.setGEOFF_lat(geo.getLat());
                        location.setGEOFF_lng(geo.getLng());
                    }
                }
                if(location.isEmpty() == true){
                    continue;
                }
                location.calcDistances();
                allLocs.add(location);

                if(location.getCD_lat()!=0 && location.getCD_lng()!=0) {
                    cdrow = new StringBuilder();
                    cdrow.append(location.getLocation());
                    cdrow.append("\t");
                    cdrow.append(location.getCD_lat());
                    cdrow.append("\t");
                    cdrow.append(location.getCD_lng());
                    cdrow.append("\t");
                    cdrow.append(location.getGEOFF_lat());
                    cdrow.append("\t");
                    cdrow.append(location.getGEOFF_lng());
                    cdrow.append("\t");
//                cdrow.append(location.getGC_lat());
//                cdrow.append("\t");
//                cdrow.append(location.getGC_lng());
//                cdrow.append("\t");
                    cdrow.append(location.getCD_GEOFF_Dist());
                    cdrow.append("\t");
//                cdrow.append(location.getGC_CD_Dist());
//                cdrow.append("\t");
//                cdrow.append(location.getGC_GEOFF_Dist());
                    cdrow.append("\n");
                    cdtable.append(cdrow.toString());
                }
                if(location.getGC_lat()!=0 && location.getGC_lng()!=0) {
                    gcrow = new StringBuilder();
                    gcrow.append(location.getLocation());
                    gcrow.append("\t");
//                gcrow.append(location.getCD_lat());
//                gcrow.append("\t");
//                gcrow.append(location.getCD_lng());
//                gcrow.append("\t");
                    gcrow.append(location.getGEOFF_lat());
                    gcrow.append("\t");
                    gcrow.append(location.getGEOFF_lng());
                    gcrow.append("\t");
                    gcrow.append(location.getGC_lat());
                    gcrow.append("\t");
                    gcrow.append(location.getGC_lng());
                    gcrow.append("\t");
//                gcrow.append(location.getCD_GEOFF_Dist());
//                gcrow.append("\t");
//                gcrow.append(location.getGC_CD_Dist());
//                gcrow.append("\t");
                    gcrow.append(location.getGC_GEOFF_Dist());
                    gcrow.append("\n");
                    gctable.append(gcrow.toString());
                }
            }

            FileCommons.getInstance().write(new File("DuplicateCompDefGazEntries.txt"),cdtable.toString());
            FileCommons.getInstance().write(new File("DuplicateGhettoCompGazEntries.txt"),gctable.toString());

        } catch (Exception e) {
            System.out.println(e);
        }

    }
    public double levenshteinDistance(String a, String b) {
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

    public double standardizeScore(double score, String location, int availablePercentage) {

        double standardScore = 0;
        // if there are more changes than the length of the location we are letting it have a 0 score.
        if (location.length() > score) {
            standardScore = (1 - (score / location.length())) * availablePercentage;
        }
        return standardScore;
    }

}
