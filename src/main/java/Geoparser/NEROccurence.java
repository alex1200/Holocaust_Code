package Geoparser;

import java.util.ArrayList;
import java.util.List;

public class NEROccurence {

    public int startOfOccurence;
    public int endOfOccurence;
    public String occurenceSentiment;
    public List<NERInstanceObject> wordsInOccurence = new ArrayList<>();

    public NEROccurence() {
    }

    public int getStartOfOccurence() {
        return startOfOccurence;
    }

    public void setStartOfOccurence(int startOfOccurence) {
        this.startOfOccurence = startOfOccurence;
    }

    public int getEndOfOccurence() {
        return endOfOccurence;
    }

    public void setEndOfOccurence(int endOfOccurence) {
        this.endOfOccurence = endOfOccurence;
    }

    public String getOccurenceSentiment() {
        return occurenceSentiment;
    }

    public void setOccurenceSentiment(String occurenceSentiment) {
        this.occurenceSentiment = occurenceSentiment;
    }

    public List<NERInstanceObject> getWordsInOccurence() {
        return wordsInOccurence;
    }

    public void setWordsInOccurence(List<NERInstanceObject> wordsInOccurence) {
        this.wordsInOccurence = wordsInOccurence;
    }

    public void addWordInOccurence(NERInstanceObject wordInOccurence) {
        this.wordsInOccurence.add(wordInOccurence);
    }
}
