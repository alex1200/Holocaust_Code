package Geoparser;

import java.util.ArrayList;
import java.util.List;

public class NERObject {
    public String nerClass;
    public String mainText;
    public int wordCount;

    public List<NEROccurence> listOccurences = new ArrayList<>();

    public NERObject() {
    }

    public String getNerClass() {
        return nerClass;
    }

    public void setNerClass(String nerClass) {
        this.nerClass = nerClass;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public List<NEROccurence> getListOccurences() {
        return listOccurences;
    }

    public void setListOccurences(List<NEROccurence> listOccurences) {
        this.listOccurences = listOccurences;
    }

    public void addOccurence(NEROccurence occurence) {
        this.listOccurences.add(occurence);
    }
}
