package Geoparser;

import Commons.DB.DBConnection;
import Commons.DB.DBRow;
import Commons.uid.UIDCommons;
import com.google.common.base.Charsets;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Geoparser {

    private Connection conn;
    private boolean tokenize = true;
    private boolean ssplit = true;
    private boolean pos = true;
    private boolean lemma = true;
    private boolean ner = true;
    private boolean regexner = true;
    private boolean parse = true;
    private boolean dcoref = true;
    private boolean sentiment = true;
    public List<GazetteerObject> gazetteers = new ArrayList<>();

    public List<String>people = new ArrayList<>();
    public String firstPerson = "";

    public Geoparser(Connection inConn) {
    conn = inConn;
    }

    public String run(String text, String textUID) {
        List<CoreMap> coremap = runNLP(text);
        text = runNER(coremap, text, "tempTitle", "tempAuthor", textUID);

        return text;
    }

    public String run(String text,String textTitle, String textAuthor, String textUID) {
        List<CoreMap> coremap = runNLP(text);
        text = runNER(coremap, text, textTitle, textAuthor, textUID);

        return text;
    }


    public List<CoreMap> runNLP(String text) {
        List<CoreMap> sentences = new ArrayList<>();
        try {

            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, regexner, parse, dcoref, sentiment");

//            props.setProperty("annotators","tokenize, ssplit, pos, lemma, ner, parse, mention, coref");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

            Annotation annotation;
            if (text.length() > 0) {
                annotation = new Annotation(text);
            } else {
                return null;
            }

            pipeline.annotate(annotation);

            sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        } catch (Exception e) {
            System.out.println(e);
        }
        return sentences;
    }

    public String runNER(List<CoreMap> sentences, String text, String title, String author, String textUID) {



        Map<String, List<List<CoreLabel>>> locations = new HashMap<>();
        int offset = 0;
        if (sentences != null && !sentences.isEmpty()) {
            for (CoreMap sentence : sentences) {
                String sentenceUID = UIDCommons.getInstance().build30StrongCat("SNT");
                try {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO `text_sentence` (sentence_UID, text_UID, sentence_sequence, sentence, sentiment,wordcount) VALUES (?,?,?,?,?,?);");
                    stmt.setString(1,sentenceUID);
                    stmt.setString(2,textUID);
                    stmt.setString(3,sentence.get(CoreAnnotations.SentenceIndexAnnotation.class).toString());
                    stmt.setString(4,sentence.get(CoreAnnotations.TextAnnotation.class).toString());
                    stmt.setString(5,sentence.get(SentimentCoreAnnotations.SentimentClass.class).toString());
                    stmt.setInt(6,sentence.get(CoreAnnotations.TokensAnnotation.class).size());

                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < sentence.get(CoreAnnotations.TokensAnnotation.class).size(); i++) {
                    CoreLabel word = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i);

                    String wordUID = UIDCommons.getInstance().build30StrongCat("WRD");
                    try {
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `text_word` (word_UID, sentence_UID, word_sequence, word, pos, ner, sentiment_class) VALUES (?,?,?,?,?,?,?);");
                        stmt.setString(1,wordUID);
                        stmt.setString(2,sentenceUID);
                        stmt.setInt(3,word.index());
                        stmt.setString(4,word.value());
                        stmt.setString(5,word.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                        stmt.setString(6,word.ner());
                        stmt.setString(7,word.get(SentimentCoreAnnotations.SentimentClass.class));

                        stmt.execute();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    List<CoreLabel> wordsInLocation = new ArrayList<>();
                    String wordNER = word.ner();
                    if (wordNER.equalsIgnoreCase("I-LOC")
                            || wordNER.equalsIgnoreCase("LUG")) {
                        wordNER = "LOCATION";
                    }
                    if (wordNER.equalsIgnoreCase("LOCATION")) {
                        String location = word.value();
                        wordsInLocation.add(word);
                        int beginIndex = word.beginPosition();
                        int endIndex = word.endPosition();
                        boolean multiWordLoc = true;
                        while (multiWordLoc == true && i < sentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1) {
                            CoreLabel nextWord = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i + 1);
                            if (nextWord.ner().equalsIgnoreCase("LOCATION")
                                    || nextWord.value().equalsIgnoreCase(" ")
                                    || nextWord.value().equalsIgnoreCase("-")) {
                                location += " " + nextWord.value();
                                wordsInLocation.add(nextWord);
                                endIndex = nextWord.endPosition();
                                i++;
                            } else {
                                multiWordLoc = false;
                            }
                        }
//                        System.out.println("Found Location '" + location + "' at " + wordsInLocation.get(0).beginPosition() + "" + wordsInLocation.get(wordsInLocation.size() - 1).endPosition());

                        List<GazetteerLocationMatch> parsedLocations = findLocation(location);
                        String beginEnamex = buildEnamex(wordUID ,wordsInLocation, location, parsedLocations);
                        String endEnamex = "</enamex>";
                        //text = text.substring(0,word.beginPosition()+offset) + beginEnamex + text.substring(word.beginPosition()+offset,word.endPosition()+offset) + endEnamex + text.substring(word.endPosition()+offset,text.length());
                        text = text.substring(0,beginIndex+offset) + beginEnamex + text.substring(beginIndex+offset,endIndex+offset) + endEnamex + text.substring(endIndex+offset,text.length());

                        for(GazetteerLocationMatch loc: parsedLocations) {
                            String locationUID = UIDCommons.getInstance().build30StrongCat("LOC");
                            if((loc.latitude == 0.0 && loc.longitude == 0.0) || loc.confidence == 0){
                                continue;
                            }
                            try {
                                PreparedStatement stmt = conn.prepareStatement("INSERT INTO `text_word_location2` (location_UID, word_UID, location, latitude, longitude, gazetteer, confidence, gazetteerRow) VALUES (?,?,?,?,?,?,?,?);");
                                stmt.setString(1,locationUID);
                                stmt.setString(2,wordUID);
                                stmt.setString(3,word.value());
                                stmt.setDouble(4,loc.latitude);
                                stmt.setDouble(5,loc.longitude);
                                stmt.setString(6,loc.gazetteer);
                                stmt.setDouble(7,loc.confidence);
                                stmt.setString(8,loc.data);

                                stmt.execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        offset += beginEnamex.length() + endEnamex.length();
//                        System.out.println(parsedLocations);

                    }
                    List<CoreLabel> wordsInPerson = new ArrayList<>();
                    wordNER = word.ner();
                    if (wordNER.equalsIgnoreCase("I-PER")
                            || wordNER.equalsIgnoreCase("PER")) {
                        wordNER = "PERSON";
                    }
                    if (wordNER.equalsIgnoreCase("PERSON")) {
                        String person = word.value();
                        wordsInPerson.add(word);
                        int beginIndex = word.beginPosition();
                        int endIndex = word.endPosition();
                        boolean multiWordPer = true;
                        while (multiWordPer == true && i < sentence.get(CoreAnnotations.TokensAnnotation.class).size() - 1) {
                            CoreLabel nextWord = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i + 1);
                            if (nextWord.ner().equalsIgnoreCase("PERSON")
                                    || nextWord.value().equalsIgnoreCase(" ")
                                    || nextWord.value().equalsIgnoreCase("-")) {
                                person += " " + nextWord.value();
                                wordsInPerson.add(nextWord);
                                endIndex = nextWord.endPosition();
                                i++;
                            } else {
                                multiWordPer = false;
                            }
                        }
                        if(firstPerson.equalsIgnoreCase("")){
                            firstPerson = person;
                        }
                        people.add(person);
                        String beginEnamex = "<person>";
                        String endEnamex = "</person>";
                        text = text.substring(0,beginIndex+offset) + beginEnamex + text.substring(beginIndex+offset,endIndex+offset) + endEnamex + text.substring(endIndex+offset,text.length());

                        offset += beginEnamex.length() + endEnamex.length();
//                        System.out.println(parsedLocations);

                    }
                }
            }
        }

        return text;
    }

    public List<String> getPeople() {
        return people;
    }

    public void setPeople(List<String> people) {
        this.people = people;
    }

    public String getFirstPerson() {
        return firstPerson;
    }

    public void setFirstPerson(String firstPerson) {
        this.firstPerson = firstPerson;
    }

    public String buildEnamex(String wordUID, List<CoreLabel> wordsInLocation, String location, List<GazetteerLocationMatch> parsedLocations){


        if(parsedLocations.size() == 0){
            return "<enamex>";
//            return "<enamex wordUID=\""+ wordUID+"\">";
        }
        GazetteerLocationMatch bestMatch = parsedLocations.get(0);
        for(GazetteerLocationMatch match : parsedLocations){
            if(match.confidence > bestMatch.confidence){
                bestMatch = match;
            }
        }
        StringBuilder sb = new StringBuilder("<enamex ");
        sb.append("wordUID=\"");
        sb.append(wordUID);
        sb.append("\" long=\"");
        sb.append(bestMatch.longitude);
        sb.append("\" lat=\"");
        sb.append(bestMatch.latitude);
        sb.append("\" type=\"location\" gazref=\"");
        sb.append(bestMatch.gazetteer);
        sb.append("\" name=\"");
        sb.append(location);
        sb.append("\" conf=\"");
        sb.append(bestMatch.confidence);
        sb.append("\">");

        return sb.toString();
    }


    public List<GazetteerLocationMatch> findLocation(String location) {
        List<GazetteerLocationMatch> matches = new ArrayList<>();
        String search = location.replace("'","");
        List<DBRow> results = DBConnection.getInstance().queryDB("SELECT * FROM deepmap.gazetteer_rows WHERE location = '"+ search+"' OR location in ('"+ search+"') OR INSTR('"+ search+"',location)<>0 OR alt_locations in ('"+ search+"') OR INSTR('"+ search+"',alt_locations)<>0;\n");

        for (DBRow row : results) {
                String gazLoc = row.row.get(2).getKey().toString();
                if (location.trim().equalsIgnoreCase(gazLoc.trim())) {
                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.FULL_MATCH, 100, row.row.get(6).getKey().toString(), row.row.get(4).getKey().toString(), row.row.get(5).getKey().toString(), row.row.get(1).getKey().toString()));
                } else if (gazLoc.trim().toLowerCase().contains(location.trim().toLowerCase()) == true) {
                    double score = levenshteinDistance(location, gazLoc);
                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.OUTER_PARTIAL_MATCH, 50 + standardizeScore(score, location, 50), row.row.get(6).getKey().toString(), row.row.get(4).getKey().toString(), row.row.get(5).getKey().toString(), row.row.get(1).getKey().toString()));
                    //The 50+ (and 50 availablePercentage) is to weigh outer matches in the top 50%

                } else if (location.trim().toLowerCase().contains(gazLoc.trim().toLowerCase()) == true) {
                    double score = levenshteinDistance(location, gazLoc);
                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.INNER_PARTIAL_MATCH, standardizeScore(score, location, 50), row.row.get(6).getKey().toString(), row.row.get(4).getKey().toString(), row.row.get(5).getKey().toString(), row.row.get(1).getKey().toString()));
                    //The 50 availablePercentage is to weigh inner matches more toward the bottom 50%, because these are much less likely to be a real match
                }

        }



//        for (GazetteerObject gazetteer : gazetteers) {
//            for (Map<String, String> dataRow : gazetteer.getData()) {
//                String gazLoc = dataRow.get(gazetteer.getLocationName());
//                if (location.equalsIgnoreCase(gazLoc)) {
//                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.FULL_MATCH, 100, dataRow, dataRow.get(gazetteer.getLatColumn()), dataRow.get(gazetteer.getLongColumn()), gazetteer.getUid()));
//                } else if (gazLoc.toLowerCase().contains(location.toLowerCase()) == true) {
//                    double score = levenshteinDistance(location, gazLoc);
//                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.OUTER_PARTIAL_MATCH, 50 + standardizeScore(score, location, 50), dataRow, dataRow.get(gazetteer.getLatColumn()), dataRow.get(gazetteer.getLongColumn()), gazetteer.getUid()));
//                    //The 50+ (and 50 availablePercentage) is to weigh outer matches in the top 50%
//
//                } else if (location.toLowerCase().contains(gazLoc.toLowerCase()) == true) {
//                    double score = levenshteinDistance(location, gazLoc);
//                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.INNER_PARTIAL_MATCH, standardizeScore(score, location, 50), dataRow, dataRow.get(gazetteer.getLatColumn()), dataRow.get(gazetteer.getLongColumn()), gazetteer.getUid()));
//                    //The 50 availablePercentage is to weigh inner matches more toward the bottom 50%, because these are much less likely to be a real match
//                }
//            }
//        }

        return matches;
    }

//    public List<GazetteerLocationMatch> findLocationInt(String location) {
//        List<GazetteerLocationMatch> matches = new ArrayList<>();
//        for (GazetteerObject gazetteer : gazetteers) {
//            for (Map<Integer, String> dataRow : gazetteer.getDataInt()) {
//                String gazLoc = dataRow.get(gazetteer.getLocationNameInt());
//                if (location.equalsIgnoreCase(gazLoc)) {
//                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.FULL_MATCH, 100, dataRow, dataRow.get(gazetteer.getLatColumnInt()), dataRow.get(gazetteer.getLongColumnInt()), gazetteer.getUid()));
//                } else if (gazLoc.toLowerCase().contains(location.toLowerCase()) == true) {
//                    double score = levenshteinDistance(location, gazLoc);
//                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.OUTER_PARTIAL_MATCH, 50 + standardizeScore(score, location, 50), dataRow, dataRow.get(gazetteer.getLatColumnInt()), dataRow.get(gazetteer.getLongColumnInt()), gazetteer.getUid()));
//                    //The 50+ (and 50 availablePercentage) is to weigh outer matches in the top 50%
//
//                } else if (location.toLowerCase().contains(gazLoc.toLowerCase()) == true) {
//                    double score = levenshteinDistance(location, gazLoc);
//                    matches.add(new GazetteerLocationMatch(gazLoc, GazetteerMatchType.INNER_PARTIAL_MATCH, standardizeScore(score, location, 50), dataRow, dataRow.get(gazetteer.getLatColumnInt()), dataRow.get(gazetteer.getLongColumnInt()), gazetteer.getUid()));
//                    //The 50 availablePercentage is to weigh inner matches more toward the bottom 50%, because these are much less likely to be a real match
//                }
//            }
//        }
//
//        return matches;
//    }

    public Map<String, String> getSentiment(List<CoreMap> sentences, String text) {
        Map<String, String> sentimentMap = new HashMap();
        for (CoreMap sentence : sentences) {
            sentimentMap.put(sentence.toShorterString(), sentence.get(SentimentCoreAnnotations.SentimentClass.class));
        }

        return sentimentMap;
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

    public void loadGazetteer(File file, String nameColumn, String latColumn, String longColumn, String lineDelimiter, String columnDelimiter, String altName1, String altName2) {
       Connection conn = DBConnection.getInstance().getConn();
       String gazID = UIDCommons.getInstance().build20StrongCat("GAZ");
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `gazetteer` (`gazetteerID`,`name`) VALUES (?,?);");
            stmt.setString(1,gazID);
            stmt.setString(2,file.getName());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.ISO_8859_1));//new FileReader(file)
//            Scanner scanner = new Scanner(file);
//
//            scanner.useDelimiter(lineDelimiter);

            List<String> columnHeaders = new ArrayList<>();
            List<Map<String,String>> dataRows = new ArrayList<>();


            int lineCount = 0;
            String line;
//            while (scanner.hasNext()) {
            while ((line = reader.readLine()) != null) {
                String raw = line;
                System.out.println(line);
                String[] columns = line.split(columnDelimiter);

                String location ="";
                String lat = "";
                String lon = "";
                String alt = "";
                if (lineCount == 0) {
                    for (String column : columns) {
                        columnHeaders.add(column);
                    }

                    lineCount++;
                } else {

                    Map<String, String> data = new HashMap<>();

                    for (int i = 0; i < columns.length; i++) {
                        if(i >= columnHeaders.size()){
                            System.out.println("somethings wrong");
                            continue;
                        }
                        data.put(columnHeaders.get(i), columns[i]);
                        if(columnHeaders.get(i).equalsIgnoreCase(nameColumn)){
                            location = columns[i];
                        }
                        if(columnHeaders.get(i).equalsIgnoreCase(latColumn)){
                            lat = columns[i];
                        }
                        if(columnHeaders.get(i).equalsIgnoreCase(longColumn)){
                            lon = columns[i];
                        }
                        if((altName1.equalsIgnoreCase("") == false && columnHeaders.get(i).equalsIgnoreCase(altName1)) || (altName2.equalsIgnoreCase("") == false && columnHeaders.get(i).equalsIgnoreCase(altName2))){
                            alt += columns[i] +",";
                        }
                    }

                    dataRows.add(data);
                    try {
                        String rowID = UIDCommons.getInstance().build20StrongCat("ROW");
                        ///String statement = "INSERT INTO `gazetteer_rows` (`gazRowID`, `gazetteer`, `location`, `alt_locations`, `lat`, `long`, `data`) VALUES (";
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO `gazetteer_rows` (`gazRowID`, `gazetteer`, `location`, `alt_locations`, `lat`, `long`, `data`) VALUES (?,?,?,?,?,?,?);");
                        stmt.setString(1,rowID);
                       // statement += "'" + rowID +"',";
                        stmt.setString(2,gazID);
                        //statement += "'" + gazID +"',";
                        byte ptext[] = location.getBytes();
                        String value = new String(ptext, "UTF-8");
                        stmt.setString(3,value);
                        //statement += "'" + value +"',";
                        System.out.println(value);
                        ptext = alt.getBytes();
                        value = new String(ptext, "UTF-8");
                        if(value.trim().equalsIgnoreCase("") == false) {
                            stmt.setString(4, value.substring(0, value.length() - 1));//In order to drop last comma
                            //statement += "'" + value.substring(0, value.length() - 1) +"',";
                        }
                        else{
                            stmt.setString(4, value);
                            //statement += "'" + value +"',";
                        }
                        stmt.setString(5,lat);
                       // statement += "'" + lat +"',";
                        stmt.setString(6,lon);
                        //statement += "'" + lon +"',";
                        ptext = data.toString().getBytes();
                        value = new String(ptext, "UTF-8");
                        stmt.setString(7,value);
                       // statement += "'" + value +"')";
                       // System.out.println(statement);

                       // DBConnection.getInstance().insertDB(statement);
                        System.out.println(stmt.toString());
                        stmt.execute();
//
                        List<DBRow> results = DBConnection.getInstance().queryDB("SELECT Location FROM deepmap.gazetteer_rows WHERE gazRowID = '"+rowID+"'");
                        System.out.println(results);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
//            GazetteerObject gazObj = new GazetteerObject(file, nameColumn, latColumn, longColumn, dataRows, columnHeaders);
//            gazObj.setGazetteerName(file.getName());

//            gazetteers.add(gazObj);
            //scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void loadGazetteer(File file, int nameColumn, int latColumn, int longColumn, String lineDelimiter, String columnDelimiter, int altName1, int altName2) {
        Connection conn = DBConnection.getInstance().getConn();
        String gazID = UIDCommons.getInstance().build20StrongCat("GAZ");
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `gazetteer` (`gazetteerID`,`name`) VALUES (?,?);");
            stmt.setString(1,gazID);
            stmt.setString(2,file.getName());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Scanner scanner = new Scanner(file);

            scanner.useDelimiter(lineDelimiter);

            List<String> columnHeaders = new ArrayList<>();
            List<Map<Integer,String>> dataRowsInt = new ArrayList<>();

            int index = 0;
            while (scanner.hasNext()) {
                String[] columns = scanner.next().split(columnDelimiter);

//                if (line == 0) {
//                    for (String column : columns) {
//                        columnHeaders.add(column);
//                    }
//
//                    line++;
//                } else {
                    String location ="";
                    String lat = "";
                    String lon = "";
                    String alt = "";
                    Map<Integer, String> data = new HashMap<>();

                    for (int i = 0; i < columns.length; i++) {
//                        if(i >= columnHeaders.size()){
//                            System.out.println("somethings wrong");
//                            continue;
//                        }
                        data.put(i, columns[i]);
                        if(i == nameColumn){
                            location = columns[i];
                        }
                        if(i == latColumn){
                            lat = columns[i];
                        }
                        if(i==longColumn){
                            lon = columns[i];
                        }
                        if(i==altName1 || i==altName2){
                            alt += columns[i] +",";
                        }
                    }

                    dataRowsInt.add(data);
                try {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO `gazetteer_rows` (`gazRowID`, `gazetteer`, `location`, `alt_locations`, `lat`, `long`, `data`) VALUES (?,?,?,?,?,?,?);");
                    stmt.setString(1,UIDCommons.getInstance().build20StrongCat("ROW"));
                    stmt.setString(2,gazID);
                    stmt.setString(3,location);
                    stmt.setString(4,alt.substring(0,alt.length()-1));
                    stmt.setString(5,lat);
                    stmt.setString(6,lon);
                    stmt.setString(7,data.toString());
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
//                }
            }
            GazetteerObject gazObj = new GazetteerObject(file, nameColumn, latColumn, longColumn, dataRowsInt, columnHeaders);
            gazObj.setGazetteerName(file.getName());
            gazetteers.add(gazObj);
            scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
