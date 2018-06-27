package Geoparser;

import Commons.File.FileCommons;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class Summary {


    public String allSummary = "";

    public static void main(String[] args) {
        File dir = new File("Geoparsed");
        Summary summary = new Summary();
        summary.processDirectory(dir);
        FileCommons.getInstance().write(new File(dir + "/summary.txt"),summary.allSummary);
    }

    public void processDirectory(File dir){
        String combineText = "";
        String dirTable = "FILE\tNAME\r\n";
        int dirCount = 0;

        for(File file: dir.listFiles()) {
            if(file.isDirectory()){
                processDirectory(file);
            }
            else {
                if(file.getName().equalsIgnoreCase("combine.txt") || file.getName().equalsIgnoreCase("directory_summary.txt")){
                    continue;
                }
                String text = FileCommons.getInstance().readFile(file.getAbsolutePath());
                Document doc = Jsoup.parse(text);
                Elements elements = doc.select("interviewee");
                String interviewee = "";
                if (null != elements && elements.size() > 0) {
                    interviewee = elements.get(0).text();
                }
                dirTable += file.getName() + "\t" + interviewee + "\r\n";
                combineText += "\n **********" + file.getName() + "************\n";
                combineText += text;


                dirCount++;
            }
        }
        allSummary += dir.getPath() + "\t"+ dirCount + "\r\n";
        FileCommons.getInstance().write(new File(dir + "/combine.txt"),combineText);
        FileCommons.getInstance().write(new File(dir + "/directory_summary.txt"),dirTable);
    }
}
