import Commons.File.FileCommons;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Alexander on 3/1/2018.
 */
public class LoadFilesToSort {
    public static void main(String[] args) {
        File dir = new File("Processed\\gender\\male");
        String gender = "male";
        for(File file: dir.listFiles())
        {
            if(file.getName().equalsIgnoreCase("ghetto")){
                continue;
            }
            String text = FileCommons.getInstance().readFile(file.getAbsolutePath());
            if(text.toLowerCase().indexOf("budapest") != -1){
                FileCommons.getInstance().write(new File("Processed/budapest/" + file.getName()),text);
                FileCommons.getInstance().write(new File("Processed/budapest/gender/" + gender + "/" + file.getName()),text);
            }
            if(text.toLowerCase().indexOf("krakow") != -1){
                FileCommons.getInstance().write(new File("Processed/krakow/" + file.getName()),text);
                FileCommons.getInstance().write(new File("Processed/krakow/gender/" + gender + "/" + file.getName()),text);
            }
            if(text.toLowerCase().indexOf("cracow") != -1){
                FileCommons.getInstance().write(new File("Processed/cracow/" + file.getName()),text);
                FileCommons.getInstance().write(new File("Processed/cracow/gender/" + gender + "/" + file.getName()),text);
            }
            if(text.toLowerCase().indexOf("kaunas") != -1){
                FileCommons.getInstance().write(new File("Processed/kaunas/" + file.getName()),text);
                FileCommons.getInstance().write(new File("Processed/kaunas/gender/" + gender + "/" + file.getName()),text);
            }
            if(text.toLowerCase().indexOf("kovno") != -1){
                FileCommons.getInstance().write(new File("Processed/kovno/" + file.getName()),text);
                FileCommons.getInstance().write(new File("Processed/kovno/gender/" + gender + "/" + file.getName()),text);
            }
        }
    }

    
}
