package Geoparser;

public class NERInstanceObject {
    public String value;
    public String text;
    public String originalText;
    public int charOffsetBegin;
    public int charOffsetEnd;
    public String before;
    public String after;
    public int index;
    public int sentenceIndex;
    public String partOfSpeech;
    public String lemma;
    public String namedEntityTag;
    public int beginIndex;
    public int endIndex;
    public int utterance;
    public int paragraph;
    public String speaker;
    public int corefClusterID;
    public String sentiment;
    public String sentiment2;

    public NERInstanceObject() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public int getCharOffsetBegin() {
        return charOffsetBegin;
    }

    public void setCharOffsetBegin(int charOffsetBegin) {
        this.charOffsetBegin = charOffsetBegin;
    }

    public int getCharOffsetEnd() {
        return charOffsetEnd;
    }

    public void setCharOffsetEnd(int charOffsetEnd) {
        this.charOffsetEnd = charOffsetEnd;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSentenceIndex() {
        return sentenceIndex;
    }

    public void setSentenceIndex(int sentenceIndex) {
        this.sentenceIndex = sentenceIndex;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getNamedEntityTag() {
        return namedEntityTag;
    }

    public void setNamedEntityTag(String namedEntityTag) {
        this.namedEntityTag = namedEntityTag;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getUtterance() {
        return utterance;
    }

    public void setUtterance(int utterance) {
        this.utterance = utterance;
    }

    public int getParagraph() {
        return paragraph;
    }

    public void setParagraph(int paragraph) {
        this.paragraph = paragraph;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public int getCorefClusterID() {
        return corefClusterID;
    }

    public void setCorefClusterID(int corefClusterID) {
        this.corefClusterID = corefClusterID;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public String getSentiment2() {
        return sentiment2;
    }

    public void setSentiment2(String sentiment2) {
        this.sentiment2 = sentiment2;
    }
}
