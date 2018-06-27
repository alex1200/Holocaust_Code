package InterviewParser;

import Commons.DB.DBConnection;
import Commons.DB.DBRow;
import Commons.File.FileCommons;
import Commons.uid.UIDCommons;
import Geoparser.Geoparser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TaskThread2 implements Runnable
{
    private String name;
    private File file;

    public TaskThread2(String s, File f)
    {
        name = s;
        file = f;
    }

    // Prints task name and sleeps for 1s
    // This Whole process is repeated 5 times
    public void run()
    {
        try
        {
            GenericGeoparse parser = new GenericGeoparse(file);
            parser.run();
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

public class GenericGeoparse {

    private String text;
    private String filename;
    private String dir;
    static final int MAX_T = 10;

    public static void main(String[] args) {
        File dir = new File("Non-Geoparsed/USHMM_Full");
//        File dir = new File("Non-Geoparsed/USHMM_Full_noQuestions");
//        File dir = new File("Non-Geoparsed/USHMM_Full_noQuestions_noInterview");
//        File dir = new File("Non-Geoparsed/USHMM_Web");

        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        int skip = 0;
        for(File file: dir.listFiles()) {


            if(!file.isDirectory()
                    && !file.getName().equalsIgnoreCase("combine.txt")
                    && !file.getName().equalsIgnoreCase("directory_summary.txt")
                    && !file.getName().equalsIgnoreCase("Flags.xml")
                    && !file.getName().equalsIgnoreCase("TableMatches.xml")
                    ) {
                skip++;
                if(skip<132){
                    continue;
                }
//                GenericGeoparse parser = new GenericGeoparse(file);
//                parser.run();
                    Runnable r1 = new TaskThread2("task "+skip, file);
                    pool.execute(r1);

            }
        }

        pool.shutdown();
    }

    public GenericGeoparse(File inFile){
        dir = "Geoparsed/USHMM_Full";
//        dir = "Geoparsed/USHMM_Full_noQuestions";
//        dir = "Geoparsed/USHMM_Full_noQuestions_noInterview";
//        dir = "Geoparsed/USHMM_Web";
        filename = inFile.getName();
        String rawText = FileCommons.getInstance().readFile("Non-Geoparsed/USHMM_Full/" + inFile.getName());
//        String rawText = FileCommons.getInstance().readFile("Non-Geoparsed/USHMM_Full_noQuestions/" + inFile.getName());
//        String rawText = FileCommons.getInstance().readFile("Non-Geoparsed/USHMM_Full_noQuestions_noInterview/" + inFile.getName());
//        String rawText = FileCommons.getInstance().readFile("Non-Geoparsed/USHMM_Web/" + inFile.getName());
        text = rawText;
    }

    public void run(){
        DBConnection.getInstance().createConnection("root", "", "127.0.0.1", 3306, "deepmap");
        DBConnection.getInstance().openConnection();
        Connection conn = DBConnection.getInstance().getConn();

//        List<DBRow> results = DBConnection.getInstance().queryDB("SELECT * FROM deepmap.gazetteer_rows WHERE location = '"+ search+"' OR location in ('"+ search+"') OR INSTR('"+ search+"',location)<>0 OR alt_locations in ('"+ search+"') OR INSTR('"+ search+"',alt_locations)<>0;\n");

//        PreparedStatement stmt = null;
//        try {
//            stmt = conn.prepareStatement("DELETE FROM deepmap.text_word_location WHERE latitude=0.0 AND longitude=0.0 AND confidence = 0");
//            stmt.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        Geoparser geoparser = new Geoparser(conn);

        Document doc = Jsoup.parse(text);
        Elements elements = doc.select("interviewee");
        String gender = "unknown";
        if (null != elements && elements.size() > 0) {
            gender = elements.get(0).text();
        }

        String textUID = UIDCommons.getInstance().build30StrongCat("TXT");
        DBConnection.getInstance().prepareInsert("INSERT INTO `text_meta` (text_UID, title, author)" +
                "VALUES ('" + textUID +
                "', '" + filename +
                "', '"+"unknown"+
                "' );");
//        String[] pieces = text.split(".");
//        String finalText = "";
//        for(String piece: pieces) {
//            finalText += geoparser.run(piece, filename, "unknown", textUID);
//        }
        String finalText = geoparser.run(text, filename, "unknown", textUID);
        toFile(filename.replace(".xml",""), gender, finalText);
    }

    private void toFile(String index, String gender, String text) {

        boolean specGhetto = false;
        FileCommons.getInstance().write(new File(dir + "/" + index + ".xml"), text);
        FileCommons.getInstance().write(new File(dir + "/gender/" + gender + "/" + index + ".xml"), text);
        if (text.toLowerCase().indexOf("ghetto") != -1) {
            FileCommons.getInstance().write(new File(dir + "/gender/" + gender + "/ghetto/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/ghetto/" + index + ".xml"), text);
        }
        if (text.toLowerCase().indexOf("budapest") != -1) {
            FileCommons.getInstance().write(new File(dir + "/budapest/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/budapest/gender/" + gender + "/" + index + ".xml"), text);
            specGhetto = true;
        }
        if (text.toLowerCase().indexOf("krakow") != -1) {
            FileCommons.getInstance().write(new File(dir + "/krakow/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/krakow/gender/" + gender + "/" + index + ".xml"), text);
            specGhetto = true;
        }
        if (text.toLowerCase().indexOf("cracow") != -1) {
            FileCommons.getInstance().write(new File(dir + "/cracow/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/cracow/gender/" + gender + "/" + index + ".xml"), text);
            specGhetto = true;
        }
        if (text.toLowerCase().indexOf("kaunas") != -1) {
            FileCommons.getInstance().write(new File(dir + "/kaunas/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/kaunas/gender/" + gender + "/" + index + ".xml"), text);
            specGhetto = true;
        }
        if (text.toLowerCase().indexOf("kovno") != -1) {
            FileCommons.getInstance().write(new File(dir + "/kovno/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/kovno/gender/" + gender + "/" + index + ".xml"), text);
            specGhetto = true;
        }

        if(specGhetto == false && text.toLowerCase().indexOf("ghetto") == -1){
            FileCommons.getInstance().write(new File(dir + "/No_Ghetto_Mention/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dir + "/No_Ghetto_Mention/gender/" + gender + "/" + index + ".xml"), text);
        }
    }
}
