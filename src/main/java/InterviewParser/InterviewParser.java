package InterviewParser;

import Commons.File.FileCommons;
import InterviewParser.cdo.TableMetadata;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import javax.xml.bind.JAXB;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class InterviewParser
{
    private String text = "";
    private String filename = "";
    private String interviewee = "";
    private List<TableMetadata> metadataList  = new ArrayList<>();

    private String gender = "unknown";
    private boolean ghetto = false;

    public static void main(String[] args)
    {
        File dir = new File("WebDriverDownloads");
        for(File file: dir.listFiles())
        {

            InterviewParser parser = new InterviewParser(file);
            parser.run();
//            if(parser.text.contains("Q:") || parser.text.contains("Question:")){
//
//            }
//            else{
//                System.out.println(parser.filename);
//            }
            GenderScrapper scrapper = new GenderScrapper("CHROME");
            if(parser.gender.equalsIgnoreCase("unknown") && parser.interviewee.equalsIgnoreCase("") == false){

                parser.genderLookup(scrapper);
                System.out.println(parser.gender);
            }
            parser.addWebMetadata();
            parser.toFile();
            break;
        }
    }

    private void genderLookup(GenderScrapper scrapper) {
        try {
            String firstName = interviewee.split(" ")[0];
//            GenderSearch search = new GenderSearch();
//            gender = search.run(firstName);
            gender = scrapper.getGender(firstName);

//            System.out.println(gender);
            StringBuilder sb = new StringBuilder();
            sb.append(buildTag("gender", gender, ""));

            sb.append(text);
            text = sb.toString();
        }
        catch (Exception e){

        }
    }

    public InterviewParser(File inFile)
    {
        loadTableMetadata();
        filename = inFile.getName();
        fromPDF(inFile);
//        text = FileCommons.getInstance().readFile("english_web_transcripts/"+ filename);
    }

    private void run()
    {
        String regex = "";
        String replacement = "";

        /**
         * Insert Interview Tags
         * ***/
//        regex = "<![CDATA[";
//        replacement = "<interview>";
////        text = replace(text, regex, replacement);
//        text = text.replace(regex,replacement);
//
//        regex = "]]></field>";
//        replacement = "</interview></field>";
////        text = replace(text, regex, replacement);
//        text = text.replace(regex,replacement);

        text = text.replace("http://collections.ushmm.org","");
        text = text.replace("Contact reference@ushmm.org for further information about this collection","");
        text = text.replace("This is a verbatim transcript of spoken word. It is not the primary source, and it has not been checked for spelling or accuracy.","");
        text = text.replace("The following transcript is the result of a recorded interview. The recording is the primary source document, not this transcript.  It has \n" +
                "not been checked for spelling nor verified for accuracy.  This document should not be quoted or used without first checking it against  \n" +
                "the interview.","");
        text = text.replace("The interview is part of the United States Holocaust Memorial Museum's collection of oral testimonies.  Information about access and \n" +
                "usage rights can be found in the catalog record.","");

        //USHMM Archives RG-50.549.02*0001 27
        regex = "USHMM Archives\\s+RG-\\d+.\\d+.\\d+\\*\\d+\\s+\\d?";
        replacement = "";
        text = replace(text, regex, replacement);

//        Erwin Baum 12
//        December 5, 1995
//        Page 12
        regex = "(\\w+ \\w+) \\d+\\s+((\\w+ \\d+, \\d+)|\\[Date not labeled\\])\\s+(Page \\d+)";
        replacement = "";
        text = replace(text, regex, replacement);

        /**
         * Insert Interviewee Tags
         * ***/
        regex = "Interview with (.+?\\n)";
        replacement = "Interview with <interviewee> $1 </interviewee>";
        interviewee = find(text,"Interview with");
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
        if(text.indexOf("Beginning Tape") != -1) {
            regex = "Beginning Tape (\\w+), Side (\\w+)";
            replacement = "<tape state=\"start\" num=\"$1\" side=\"$2\">Beginning Tape $1, Side $2</tape>";
            text = replace(text, regex, replacement);

            regex = "End of Tape (\\w+), Side (\\w+)";
            replacement = "<tape state=\"end\" num=\"$1\" side=\"$2\">End of Tape $1, Side $2</tape>";
            text = replace(text, regex, replacement);
        }
        else {
//            regex = "(Beginning)? Tape ?#(\\w+)";
//            replacement = "<tape state=\"start\" num=\"$2\" >Beginning Tape $2</tape>";
//            text = replace(text, regex, replacement);
//
//            regex = "End of Tape #?(\\w+)";
//            replacement = "<tape state=\"end\" num=\"$1\" >End of Tape $1</tape>";
//            text = replace(text, regex, replacement);
            //[tape break]
            regex = "\\[tape break\\]";
            replacement = "<tape state=\"break\">[tape break]</tape>";
            text = replace(text, regex, replacement);
            //Tape 1, Side B
            regex = "Tape (\\w+), Side (\\w+)";
            replacement = "<tape num=\"$1\" side=\"$2\"/>Tape $1, Side $2</tape>";
            text = replace(text, regex, replacement);
            //00:01:32
            regex = "((\\d\\d):(\\d\\d):(\\d\\d)(:(\\d\\d))?)";
            replacement = "<tape time=\"$1\"></tape>";
            text = replace(text, regex, replacement);
            //10:01:02:12
        }
        /**
         * Insert Question/Answer Tags
         * ***/

        //Interviewer:  Doctor, you’re from Chicago?

//        Morton Waitzman:  Right, yes, Chicago, Illinois.

//            Int:  … drafted or did you enlist in the army?

//        MW:  I did

        if(text.indexOf("Interviewer:") != -1 || text.indexOf("Int:") != -1) {
            regex = "(Int:|Interviewer:)(.*?)\\s*([^\\w+[^\\d:]]):";
            replacement = "<question>$1$2</question>\n$3:";
            text = replace(text, regex, replacement);

            regex = "([^\\w+[^\\d:]]):(.*?)\\s*(<question>Int:|<question>Interviewer:)";
            replacement = "<answer>$1$2</answer>\n$3";
            text = replace(text, regex, replacement);
        }
        else if(text.indexOf("Question:") != -1 || text.indexOf("Q:") != -1) {
            regex = "(Q:|Question:)(.*?)\\s*(A:|Answer:)";
            replacement = "<question>$1$2</question>\n$3";
            text = replace(text, regex, replacement);

            regex = "(A:|Answer:)(.*?)\\s*(<question>Q:|<question>Question:|End of Tape|Conclusion of interview)";
            replacement = "<answer>$1$2</answer>\n$3";
            text = replace(text, regex, replacement);
        }
        else{
            //        TRACK 05 -  The Children’s Action
//        Life in the ghetto for me was not rea
            regex = "(\\w+[^\\d]:)([^\\w+[^\\d:]])*";
            replacement = "<participant>$1$2</participant>";
            text = replace(text, regex, replacement);

        }

        System.out.println("Finished file:"+filename);
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

    private boolean addTableMetadata() {
        TableMetadata meta = null;
        for (TableMetadata entry : metadataList) {
            if ((interviewee.equalsIgnoreCase("") != false && interviewee.trim().length() > 0
                    && (
                    entry.getName().equalsIgnoreCase(interviewee)
                    || entry.getName().toLowerCase().contains(interviewee.toLowerCase())
                    || interviewee.toLowerCase().contains(entry.getName().toLowerCase())
                    ))||
                    (filename.replace(".pdf","").replace(".01.","*").equalsIgnoreCase(entry.getRg_number()))
                    ) {
                meta = entry;
                System.out.println("Matched Entry -" + interviewee + "/" + filename.replace(".pdf","").replace(".01.","*"));
                break;
            }
        }
        if(meta != null) {
            StringBuilder sb = new StringBuilder();
            if (meta.getName().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("name", meta.getName(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getRg_number().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("rg_number", meta.getRg_number(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getGender().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("gender", meta.getGender(), "USHMM_Survivor_Testimony_Metadata"));
                gender = meta.getGender();
            }
            if (meta.getDate_of_birth().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("date_of_birth", meta.getDate_of_birth(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getPlace_of_birth().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("place_of_birth", meta.getPlace_of_birth(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getCountry().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("country", meta.getCountry(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getSource().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("source", meta.getSource(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getClassification().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("classification", meta.getClassification(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getGhetto_association().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("ghetto_association", meta.getGhetto_association(), "USHMM_Survivor_Testimony_Metadata"));
                ghetto = true;//meta.getGhetto_association();
            }
            if (meta.getSearchable().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("searchable", meta.getSearchable(), "USHMM_Survivor_Testimony_Metadata"));
            }
            if (meta.getComments().trim().equalsIgnoreCase("") == false) {
                sb.append(buildTag("comments", meta.getComments(), "USHMM_Survivor_Testimony_Metadata"));
            }
            sb.append(text);
            text = sb.toString();
            return true;
        }
        return false;
    }

    private void addWebMetadata()
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
        sb.append(" source=\"" + source + "\"");
        sb.append(">");

        sb.append(content);

        sb.append("</");
        sb.append(tag);
        sb.append(">\n");
        return sb.toString();
    }

    private void fromPDF(File inFile)
    {
        PDFParser parser = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper;

        String parsedText = "";
        try
        {
            parser = new PDFParser(new FileInputStream(inFile));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                if (cosDoc != null)
                    cosDoc.close();
                if (pdDoc != null)
                    pdDoc.close();
            } catch (Exception e1)
            {
                e.printStackTrace();
            }

        }
        text = parsedText;
    }

    private void toFile()
    {
        FileCommons.getInstance().write(new File("Processed/"+filename.replace(".pdf",".xml")),text);
        FileCommons.getInstance().write(new File("Processed/gender/"+gender+"/"+filename.replace(".pdf",".xml")),text);
        if(ghetto == true) {
            FileCommons.getInstance().write(new File("Processed/gender/" + gender + "/ghetto/" + filename.replace(".pdf", ".xml")), text);
            FileCommons.getInstance().write(new File("Processed/ghetto/" + filename.replace(".pdf", ".xml")), text);
        }

        if(text.toLowerCase().indexOf("budapest") != -1){
            FileCommons.getInstance().write(new File("Processed/budapest/" + filename.replace(".pdf", ".xml")),text);
            FileCommons.getInstance().write(new File("Processed/budapest/gender/" + gender + "/" + filename.replace(".pdf", ".xml")),text);
        }
        if(text.toLowerCase().indexOf("krakow") != -1){
            FileCommons.getInstance().write(new File("Processed/krakow/" + filename.replace(".pdf", ".xml")),text);
            FileCommons.getInstance().write(new File("Processed/cracow/gender/" + gender + "/" + filename.replace(".pdf", ".xml")),text);
        }
        if(text.toLowerCase().indexOf("kaunas") != -1){
            FileCommons.getInstance().write(new File("Processed/kaunas/" + filename.replace(".pdf", ".xml")),text);
            FileCommons.getInstance().write(new File("Processed/kaunas/gender/" + gender + "/" + filename.replace(".pdf", ".xml")),text);
        }
        if(text.toLowerCase().indexOf("kovno") != -1){
            FileCommons.getInstance().write(new File("Processed/kovno/" + filename.replace(".pdf", ".xml")),text);
            FileCommons.getInstance().write(new File("Processed/kovno/gender/" + gender + "/" + filename.replace(".pdf", ".xml")),text);
        }
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
        System.out.println("no match found");
        return "";
    }
}
