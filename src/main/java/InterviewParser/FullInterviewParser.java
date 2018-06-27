package InterviewParser;

import Commons.File.FileCommons;
import InterviewParser.cdo.TableMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FullInterviewParser {
    private String[] textArray;
//    private String text = "";
    private String filename = "";
//    private String interviewee = "";
    private List<TableMetadata> metadataList  = new ArrayList<>();
    private List<String> flags = new ArrayList<>();
    private List<String> tableMatches = new ArrayList<>();
//    private String gender = "unknown";
//    private boolean ghetto = false;

    public static void main(String[] args)
    {
//        File dir = new File("WebDriverDownloads");
//        for(File file: dir.listFiles())
//        {

            FullInterviewParser parser = new FullInterviewParser(new File("Full800.xml"));
            parser.run();
//            if(parser.text.contains("Q:") || parser.text.contains("Question:")){
//
//            }
//            else{
//                System.out.println(parser.filename);
//            }
////            if(added == false && parser.interviewee.equalsIgnoreCase("") == false){
////                parser.genderLookup();
////                System.out.println(parser.gender);
////            }
////            parser.addWebMetadata();
////            parser.toFile();
////            break;
//        }
    }

    private void genderLookup(String interviewee, String gender, String text) {
        try {
            String firstName = interviewee.split(" ")[0];
            GenderSearch search = new GenderSearch();
            gender = search.run(firstName);
//            System.out.println(gender);
            StringBuilder sb = new StringBuilder();
            sb.append(buildTag("gender", gender, ""));

            sb.append(text);
            text = sb.toString();
        }
        catch (Exception e){

        }
    }

    public FullInterviewParser(File inFile)
    {
        loadTableMetadata();
        filename = inFile.getName();
        String rawText = FileCommons.getInstance().readFile(inFile.getName());
        textArray = rawText.split("</doc>");
//        text = FileCommons.getInstance().readFile("english_web_transcripts/"+ filename);
    }

    public void run() {
////        Geoparser geoparser = new Geoparser();
////        try {
////            geoparser.loadGazetteer(new File("text/uk.txt"), "FULL_NAME_RO", "LAT", "LONG", "\n", "\t");
////            geoparser.loadGazetteer(new File("text/DE.txt"), 1, 4, 5, "\n", "\t");
////            geoparser.loadGazetteer(new File("text/PL.txt"), 1, 4, 5, "\n", "\t");
//////            geoparser.loadGazetteer(new File("text/allCountries.txt"), 1, 4, 5, "\n", "\t");
////        }
//        catch(Exception e){
//            System.out.println(e);
//        }


        int index = 0;
        int noQuest = 0;
        int hasInter = 0;
        GenderScrapper scrapper = new GenderScrapper("CHROME");
        for (String mainText : textArray) {
            index++;
            StringBuilder finalTextSB = new StringBuilder("");

            String rg_number = "";
            String regex = "";
            String replacement = "";
            if(mainText.contains("<doc>") == false){
                continue;
            }
            mainText = mainText + "</doc>";
            Document document = Jsoup.parse(mainText);
            Elements doc = document.select("doc");
            if(null == doc.get(0)){
                System.out.println("Error on XML");
                continue;
            }
            Elements  elements = doc.get(0).children();

            String interviewee = "";
            boolean hasInterview = false;
            for (Element element : elements) {
                String name = element.attr("name");
                String tag = element.tagName();
                String text = element.text();

                if(name.equalsIgnoreCase("interviewee") == true){
                    interviewee = element.text();
                }
                if(name.equalsIgnoreCase("rg_number") == true){
                    rg_number = text;//text.replace("*",".");
                }
                if (name.equalsIgnoreCase("fnd_content_web") == true) {
                    hasInterview = true;
                    hasInter++;
                    text = text.replace("http://collections.ushmm.org", "");
                    text = text.replace("Contact reference@ushmm.org for further information about this collection", "");
                    text = text.replace("This is a verbatim transcript of spoken word. It is not the primary source, and it has not been checked for spelling or accuracy.", "");
                    text = text.replace("The following transcript is the result of a recorded interview. The recording is the primary source document, not this transcript.  It has \n" +
                            "not been checked for spelling nor verified for accuracy.  This document should not be quoted or used without first checking it against  \n" +
                            "the interview.", "");
                    text = text.replace("The interview is part of the United States Holocaust Memorial Museum's collection of oral testimonies.  Information about access and \n" +
                            "usage rights can be found in the catalog record.", "");

                    regex = "USHMM Archives\\s+RG-\\d+.\\d+.\\d+\\*\\d+\\s+\\d?";
                    replacement = "";
                    text = replace(text, regex, replacement);

                    regex = "(\\w+ \\w+) \\d+\\s+((\\w+ \\d+, \\d+)|\\[Date not labeled\\])\\s+(Page \\d+)";
                    replacement = "";
                    text = replace(text, regex, replacement);

                    /**
                     * Insert Interviewee Tags
                     * ***/
                    regex = "Interview with (.+?\\n)";
                    replacement = "Interview with <interviewee> $1 </interviewee>";
//                    interviewee = find(text, "Interview with");
                    text = replace(text, regex, replacement);

                    /**
                     * Insert Preface Tags
                     * ***/
                    regex = "PREFACE([.\\s\\S]*)(Interview with)";//Not stopping at first interview with, maybe add ?
                    replacement = "<preface>PREFACE \n $1 </preface>\n$2";
                    text = replace(text, regex, replacement);

                    /**
                     * Insert Tape Tags
                     * ***/
                    if (text.indexOf("Beginning Tape") != -1) {
                        regex = "Beginning Tape (\\w+), Side (\\w+)";
                        replacement = "<tape state=\"start\" num=\"$1\" side=\"$2\">Beginning Tape $1, Side $2</tape>";
                        text = replace(text, regex, replacement);

                        regex = "End of Tape (\\w+), Side (\\w+)";
                        replacement = "<tape state=\"end\" num=\"$1\" side=\"$2\">End of Tape $1, Side $2</tape>";
                        text = replace(text, regex, replacement);
                    } else {
                        regex = "\\[tape break\\]";
                        replacement = "<tape state=\"break\">[tape break]</tape>";
                        text = replace(text, regex, replacement);
                        regex = "Tape (\\w+), Side (\\w+)";
                        replacement = "<tape num=\"$1\" side=\"$2\"/>Tape $1, Side $2</tape>";
                        text = replace(text, regex, replacement);
                        regex = "((\\d\\d):(\\d\\d):(\\d\\d)(:(\\d\\d))?)";
                        replacement = "<tape time=\"$1\"></tape>";
                        text = replace(text, regex, replacement);
                    }
                    if (text.indexOf("Interviewer:") != -1 || text.indexOf("Int:") != -1) {
                        regex = "(Int:|Interviewer:)(.*?)\\s*([^\\w+[^\\d:]]):";
                        replacement = "<question>$1$2</question>\n$3:";
                        text = replace(text, regex, replacement);

                        regex = "([^\\w+[^\\d:]]):(.*?)\\s*(<question>Int:|<question>Interviewer:)";
                        replacement = "<answer>$1$2</answer>\n$3";
                        text = replace(text, regex, replacement);
                    } else if (text.indexOf("Question:") != -1 || text.indexOf("Q:") != -1) {
                        regex = "(Q:|Question:)(.*?)\\s*(A:|Answer:)";
                        replacement = "<question>$1$2</question>\n$3";
                        text = replace(text, regex, replacement);

                        regex = "(A:|Answer:)(.*?)\\s*(<question>Q:|<question>Question:|End of Tape|Conclusion of interview)";
                        replacement = "<answer>$1$2</answer>\n$3";
                        text = replace(text, regex, replacement);
                    } else {
                        regex = "(\\w+[^\\d]:)([^\\w+[^\\d:]])*";
                        replacement = "<participant>$1$2</participant>";
                        text = replace(text, regex, replacement);

                    }

//                    String geoparsedText =  geoparser.run(text);
//                    if(null != geoparsedText && geoparsedText.equalsIgnoreCase("") == false){
//                        text = geoparsedText;
//                    }

                }
                finalTextSB.append(buildTag(name, text, ""));
            }
                String gender = "unknown";
                boolean ghetto = false;
                System.out.println("Finished file:" + index);
                if(interviewee.equalsIgnoreCase("") == false) {
//                genderLookup(interviewee,gender,text);
                    String preInterviewee = interviewee.replaceAll("Mr.","").replaceAll("Mrs.","").replaceAll("Ms.","").replaceAll("Dr.","").trim();
                    String firstName = preInterviewee.split(" ")[0];
                    gender = scrapper.getGender(firstName);
                    if(gender.equalsIgnoreCase("unknown")== true || gender.equalsIgnoreCase("unisex")){
                        if(interviewee.contains("Mr.")){
                            gender = "male";
                        }
                        else if (interviewee.contains("Ms.") == true || interviewee.contains("Mrs.")){
                            gender = "female";
                        }
                    }
                    finalTextSB.append(buildTag("gender", gender, ""));
                }
                String finalText = finalTextSB.toString();
                if(finalText.toLowerCase().contains("ghetto") == true){
                    ghetto = true;
                }
                finalText = addTableMetadata(interviewee, rg_number ,gender,ghetto,finalText);

//            addWebMetadata(interviewee,gender,ghetto,text);
            boolean hasQuestions = false;
            if(finalText.contains("Q:") || finalText.contains("Question:")){
                hasQuestions = true;
            }
            else{
                noQuest++;
            }
            System.out.println(noQuest + " no Q / " + hasInter + " has Int/ "+index);
            toFile(rg_number.replace("*","."), interviewee,gender,ghetto,finalText, hasQuestions, hasInterview);

        }

        toFile("Flags",flags.toString());
        toFile("TableMatches",tableMatches.toString());
    }

    private void loadTableMetadata() {
        try
        {

            FileReader fr = new FileReader("USHMM_Survivor_Testimony_Metadata.csv");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");
                TableMetadata metadata = new TableMetadata(attributes);
                metadataList.add(metadata);
                line = br.readLine();
            }
        } catch (Exception ioe)
        {
            ioe.printStackTrace();
        }
    }

    private String addTableMetadata(String interviewee, String rg_number, String gender, boolean ghetto, String text) {
        TableMetadata meta = null;
        for (TableMetadata entry : metadataList) {
//            if ((interviewee.equalsIgnoreCase("") != false && interviewee.trim().length() > 0
//                    && (
//                    entry.getName().equalsIgnoreCase(interviewee)
//                            || entry.getName().toLowerCase().contains(interviewee.toLowerCase())
//                            || interviewee.toLowerCase().contains(entry.getName().toLowerCase())
//            ))||
//                    (filename.replace(".pdf","").replace(".01.","*").equalsIgnoreCase(entry.getRg_number()))
//                    ) {
            if(entry.getRg_number().equalsIgnoreCase(rg_number)){
                meta = entry;
                System.out.println("Matched Entry -" + interviewee );//"/" + filename.replace(".pdf","").replace(".01.","*"));
                break;
            }
        }
        if(meta != null) {
            System.out.println("FOUNT TABLE ENTRY FOR: "+ interviewee);
            StringBuilder sb = new StringBuilder();
            if (meta.getName().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_name", meta.getName(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getRg_number().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_rg_number", meta.getRg_number(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getGender().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_gender", meta.getGender(), "USHMM_Survivor_Testimony_Metadata"));
                if((meta.getGender().equalsIgnoreCase("f") == true && gender.equalsIgnoreCase("male"))
                        || (meta.getGender().equalsIgnoreCase("m") == true && gender.equalsIgnoreCase("female"))){
                    flags.add(rg_number + " - Gender in Table Match");
                }
            }
            if (meta.getDate_of_birth().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_date_of_birth", meta.getDate_of_birth(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getPlace_of_birth().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_place_of_birth", meta.getPlace_of_birth(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getCountry().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_country", meta.getCountry(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getSource().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_source", meta.getSource(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getClassification().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_classification", meta.getClassification(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getGhetto_association().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_ghetto_association", meta.getGhetto_association(), "USHMM_Survivor_Testimony_Metadata"));
                ghetto = true;//meta.getGhetto_association();
            }
            if (meta.getSearchable().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_searchable", meta.getSearchable(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getComments().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("meta_comments", meta.getComments(), "USHMM_Survivor_Testimony_Metadata"));
            }
            tableMatches.add(rg_number);
            sb.append(text);
            text = sb.toString();
            return text;
        }
        return text;
    }

    private void addWebMetadata(String interviewee, String gender, boolean ghetto, String text)
    {
        SeleniumScrapper scrapper = new SeleniumScrapper(filename.substring(0,filename.indexOf("_")));
        Map<String,String> datapairs = scrapper.run();
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : datapairs.entrySet())
        {
            if(entry.getKey().trim().equalsIgnoreCase(""))
            {

            }
            else
            {
                sb.append(
                        buildTag(entry.getKey().replaceAll(":", "")
                                        .replaceAll(" ", "")
                                        .replaceAll("/", "-"),
                                entry.getValue(),
                                "https://collections.ushmm.org/search/catalog/irn507310")
                );
//                if(entry.getKey().trim().replaceAll(":", "")
//                        .replaceAll(" ", "")
//                        .replaceAll("/", "-").equalsIgnoreCase("Geographic Name") == true){
                if(entry.getValue().toLowerCase().contains("ghetto")){
                    ghetto = true;
                }
//                }
            }
        }
        sb.append(text);
        text = sb.toString();
    }

    private String buildTag (String tag, String content, String source)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(tag);
//        sb.append(" source=\"" + source + "\"");
        sb.append(">");

        sb.append(content);

        sb.append("</");
        sb.append(tag);
        sb.append(">\n");
        return sb.toString();
    }

//    private void fromPDF(File inFile)
//    {
//        PDFParser parser = null;
//        PDDocument pdDoc = null;
//        COSDocument cosDoc = null;
//        PDFTextStripper pdfStripper;
//
//        String parsedText = "";
//        try
//        {
//            parser = new PDFParser(new FileInputStream(inFile));
//            parser.parse();
//            cosDoc = parser.getDocument();
//            pdfStripper = new PDFTextStripper();
//            pdDoc = new PDDocument(cosDoc);
//            parsedText = pdfStripper.getText(pdDoc);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            try
//            {
//                if (cosDoc != null)
//                    cosDoc.close();
//                if (pdDoc != null)
//                    pdDoc.close();
//            } catch (Exception e1)
//            {
//                e.printStackTrace();
//            }
//
//        }
//        text = parsedText;
//    }

    private void toFile(String index, String interviewee, String gender, boolean ghetto, String text, boolean hasQuestions, boolean hasInterview)
    {
        String dir = "";
        String dup = "";
        File dupDir = new File("WebDriverDownloads");
        for(File file: dupDir.listFiles()) {
            if(index.equalsIgnoreCase(file.getName().replace("_trs_en.pdf","")) == true){
                dup = "duplicate/";
            }
        }
        if(hasQuestions == false){
            dir += "_noQuestions";
        }
        if(hasInterview == false){
            dir += "_noInterview";
        }
        FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/" + index + ".xml"),text);
        FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/gender/" + gender + "/" + index + ".xml"),text);
        if(ghetto == true) {
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/gender/" + gender + "/ghetto/" + index + ".xml"), text);
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/ghetto/" + index + ".xml"), text);
        }
        if(text.toLowerCase().indexOf("budapest") != -1){
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/budapest/" + index + ".xml"),text);
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/budapest/gender/" + gender + "/" + index + ".xml"),text);
        }
        if(text.toLowerCase().indexOf("krakow") != -1){
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/krakow/" + index + ".xml"),text);
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/krakow/gender/" + gender + "/" + index + ".xml"),text);
        }
        if(text.toLowerCase().indexOf("cracow") != -1){
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/cracow/" + index + ".xml"),text);
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/cracow/gender/" + gender + "/" + index + ".xml"),text);
        }
        if(text.toLowerCase().indexOf("kaunas") != -1){
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/kaunas/" + index + ".xml"),text);
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/kaunas/gender/" + gender + "/" + index + ".xml"),text);
        }
        if(text.toLowerCase().indexOf("kovno") != -1){
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/kovno/" + index + ".xml"),text);
            FileCommons.getInstance().write(new File(dup + "Processed_Full"+dir+"/kovno/gender/" + gender + "/" + index + ".xml"),text);
        }
    }

    private void toFile(String name, String text)
    {
        String dir = "";
        FileCommons.getInstance().write(new File("Processed_Full"+dir+"/" + name + ".xml"),text);

    }

    private String replace(String text, String regex, String replacement)
    {
        Pattern p = Pattern.compile(regex,
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        return p.matcher(text).replaceAll(replacement);
    }

    private String regfind(String text, String regex) {
        Pattern p = Pattern.compile(regex,
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        try {
            return p.matcher(text).group();
        } catch (Exception e) {
            System.out.println("no match found");
            return "";
        }
    }

    private String find (String text, String find){
        int index = text.indexOf(find);
        if(index != -1) {
            int endIndex = text.indexOf("\n",index);
            if(endIndex != -1) {
                String found = text.substring(index + find.length(), endIndex);
                System.out.println(found.trim());
                return found.trim();
            }
        }
        System.out.println("no match found - " + find);
        return "";
    }
}
