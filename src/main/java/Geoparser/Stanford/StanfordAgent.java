package Geoparser.Stanford;


import Geoparser.LanguageProperties;
import Geoparser.NERInstanceObject;
import Geoparser.NERObject;
import Geoparser.NEROccurence;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class StanfordAgent {
    private List<CoreMap> sentences;
    private boolean tokenize = true;
    private boolean ssplit = true;
    private boolean pos = true;
    private boolean lemma = true;
    private boolean ner = true;
    private boolean regexner = true;
    private boolean parse = true;
    private boolean dcoref = true;
    private boolean sentiment = true;

    public StanfordAgent(String text, StanfordLanguages language) {
        try {
            Properties props = null;// = new StanfordCoreNLP(props);
            switch (language) {
                case Chinese:
                    props = LanguageProperties.CHINESE_HYBRID.properties;
                    break;
                case French:
                    props = LanguageProperties.FRENCH.properties;
                    break;
                case English:
                    props = new Properties();
                    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
                    break;
                case German:
                    props = LanguageProperties.GERMAN.properties;
                    break;
                case Spanish:
                    props = LanguageProperties.SPANISH.properties;
                    break;
                case Arabic:
                    props = LanguageProperties.ARABIC.properties;
                    break;
            }
            if (null == props) {
                props = new Properties();
                props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
            }
            run(text, props);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public StanfordAgent(String text, boolean tokenize, boolean ssplit, boolean pos, boolean lemma, boolean ner,
                     boolean regexner, boolean parse, boolean dcoref, boolean sentiment) {
        this.tokenize = tokenize;
        this.ssplit = ssplit;
        this.pos = pos;
        this.lemma = lemma;
        this.ner = ner;
        this. regexner = regexner;
        this.parse = parse;
        this.dcoref = dcoref;
        this.sentiment = sentiment;

        String annatators = buildAnnatators();
        Properties props = new Properties();
        props.setProperty("annotators",annatators);

        run(text, props);
    }

    private void run(String text, Properties props){
        //More Detailed Descriptions can be found at http://stanfordnlp.github.io/CoreNLP/annotators.html
        try {


            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

            Annotation annotation;
            if (text.length() > 0) {
                annotation = new Annotation(text);
            }
            else{
                return;
            }

            pipeline.annotate(annotation);

            sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String buildAnnatators(){
        StringBuilder sb = new StringBuilder();
        if(this.tokenize == true){
            sb.append("tokenize,");
        }
        if(this.ssplit == true){
            sb.append("ssplit,");
        }
        if(this.pos == true){
            sb.append("pos,");
        }
        if(this.lemma == true){
            sb.append("lemma,");
        }
        if(this.ner == true){
            sb.append("ner,");
        }
        if(this.regexner == true){
            sb.append("regexner,");
        }
        if(this.parse == true){
            sb.append("parse,");
        }
        if(this.dcoref == true){
            sb.append("dcoref,");
        }
        if(this.sentiment == true){
            sb.append("sentiment,");
        }
        return sb.toString().substring(0, sb.toString().length()-1);
        //Trim off the last comma from the string
    }

    public List<CoreMap> getSentences(){
        return sentences;
    }

    //Recognizes named (PERSON, LOCATION, ORGANIZATION, MISC), numerical (MONEY, NUMBER, ORDINAL, PERCENT), and temporal (DATE, TIME, DURATION, SET) entities.

    public Map<String,List<List<CoreLabel>>> getLocations(){
        return getNER("LOCATION");
    }
    public Map<String,List<List<CoreLabel>>> getPeople(){
        return getNER("PERSON");
    }
    public Map<String,List<List<CoreLabel>>> getOrganizations(){
        return getNER("ORGANIZATION");
    }
    public Map<String,List<List<CoreLabel>>> getMiscs(){
        return getNER("MISC");
    }

    public Map<String,List<List<CoreLabel>>> getMoney(){
        return getNER("MONEY");
    }
    public Map<String,List<List<CoreLabel>>> getNumbers(){
        return getNER("NUMBER");
    }
    public Map<String,List<List<CoreLabel>>> getOrdinals(){
        return getNER("ORDINAL");
    }
    public Map<String,List<List<CoreLabel>>> getPercents(){
        return getNER("PERCENT");
    }

    public Map<String,List<List<CoreLabel>>> getDates(){
        return getNER("DATE");
    }
    public Map<String,List<List<CoreLabel>>> getTimes(){
        return getNER("TIME");
    }
    public Map<String,List<List<CoreLabel>>> getDurations(){
        return getNER("DURATION");
    }
    public Map<String,List<List<CoreLabel>>> getSets(){
        return getNER("SET");
    }

    public Map<String,List<List<CoreLabel>>> getIdealogies(){
        return getNER("IDEOLOGY");
    }
    public Map<String,List<List<CoreLabel>>> getNationalities(){
        return getNER("NATIONALITY");
    }
    public Map<String,List<List<CoreLabel>>> getReligions(){
        return getNER("RELIGION");
    }
    public Map<String,List<List<CoreLabel>>> getTitles(){
        return getNER("TITLE");
    }

    private Map<String,List<List<CoreLabel>>> getNER(String ner){
        Map<String,List<List<CoreLabel>>> locations = new HashMap<>();
        if (sentences != null && !sentences.isEmpty()) {
            for(CoreMap sentence : sentences) {
                for(int i = 0; i < sentence.get(CoreAnnotations.TokensAnnotation.class).size(); i++){
                    CoreLabel word = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i);

                    List<CoreLabel> wordsInLocation = new ArrayList<>();
                    String wordNER = word.ner();
                    if (wordNER.equalsIgnoreCase("I-PER")
                            || wordNER.equalsIgnoreCase("PERS")){
                        wordNER = "PERSON";
                    }
                    else if (wordNER.equalsIgnoreCase("I-ORG")
                            || wordNER.equalsIgnoreCase("ORG")){
                        wordNER = "ORGANIZATION";
                    }
                    else if (wordNER.equalsIgnoreCase("I-LOC")
                            || wordNER.equalsIgnoreCase("LUG")){
                        wordNER = "LOCATION";
                    }
                    else if (wordNER.equalsIgnoreCase("I-MISC")
                            || wordNER.equalsIgnoreCase("OTROS")){
                        wordNER = "MISC";
                    }
                    if(wordNER.equalsIgnoreCase("O")==false){
                        System.out.println(wordNER);
                    }
                    if(wordNER.equalsIgnoreCase(ner)){
                        String location = word.value();
                        wordsInLocation.add(word);

                        boolean multiWordLoc = true;
                        while(multiWordLoc == true && i < sentence.get(CoreAnnotations.TokensAnnotation.class).size()-1 ){
                            CoreLabel nextWord = sentence.get(CoreAnnotations.TokensAnnotation.class).get(i+1);
                            if(nextWord.ner().equalsIgnoreCase(ner)){
                                location += " " + nextWord.value();
                                wordsInLocation.add(nextWord);
                                i++;
                            }
                            else{
                                multiWordLoc = false;
                            }
                        }
                        if(locations.containsKey(location) == false){
                            List<List<CoreLabel>> locationAppearences = new ArrayList<>();
                            locationAppearences.add(wordsInLocation);
                            locations.put(location,locationAppearences);
                        }
                        else{
                            locations.get(location).add(wordsInLocation);
                        }
                    }
                }
            }
        }

        return locations;
    }

    public List<NERObject> getLocationsList(){
        return getCleanList("LOCATION",getNER("LOCATION"));
    }
    public List<NERObject> getPeopleList(){
        return getCleanList("PERSON",getNER("PERSON"));
    }
    public List<NERObject> getOrganizationsList(){
        return getCleanList("ORGANIZATION",getNER("ORGANIZATION"));
    }
    public List<NERObject> getMiscsList(){
        return getCleanList("MISC",getNER("MISC"));
    }

    public List<NERObject> getMoneyList(){
        return getCleanList("MONEY",getNER("MONEY"));
    }
    public List<NERObject> getNumbersList(){
        return getCleanList("NUMBER",getNER("NUMBER"));
    }
    public List<NERObject> getOrdinalsList(){
        return getCleanList("ORDINAL",getNER("ORDINAL"));
    }
    public List<NERObject> getPercentsList(){
        return getCleanList("PERCENT",getNER("PERCENT"));
    }

    public List<NERObject> getDatesList(){
        return getCleanList("DATE",getNER("DATE"));
    }
    public List<NERObject> getTimesList(){
        return getCleanList("TIME",getNER("TIME"));
    }
    public List<NERObject> getDurationsList(){
        return getCleanList("DURATION",getNER("DURATION"));
    }
    public List<NERObject> getSetsList(){
        return getCleanList("SET",getNER("SET"));
    }

    public List<NERObject> getIdealogiesList(){
        return getCleanList("IDEOLOGY",getNER("IDEOLOGY"));
    }
    public List<NERObject> getNationalitiesList(){
        return getCleanList("NATIONALITY",getNER("NATIONALITY"));
    }
    public List<NERObject> getReligionsList(){
        return getCleanList("RELIGION",getNER("RELIGION"));
    }
    public List<NERObject> getTitlesList(){
        return getCleanList("TITLE",getNER("TITLE"));
    }

    private List<NERObject> getCleanList (String nerClass, Map<String,List<List<CoreLabel>>> nerMap){
        List<NERObject> nerObjects = new ArrayList<>();
        if(null != nerMap) {
            for (Map.Entry<String, List<List<CoreLabel>>> entry : nerMap.entrySet()) {
                NERObject ner = new NERObject();
                ner.setNerClass(nerClass);
                ner.setMainText(entry.getKey());
                for (List<CoreLabel> occurence : entry.getValue()) {
                    NEROccurence nerOccurence = new NEROccurence();
                    boolean firstLabel = true;

                    ner.setWordCount(occurence.size());

                    for (CoreLabel coreLabel : occurence) {
                        NERInstanceObject instance = parseCoreLabel(coreLabel);
                        if (firstLabel == true) {
                            nerOccurence.setStartOfOccurence(instance.getCharOffsetBegin());
                            nerOccurence.setOccurenceSentiment(instance.getSentiment());
                            firstLabel = false;
                        }
                        nerOccurence.setEndOfOccurence(instance.getCharOffsetEnd());
                        nerOccurence.addWordInOccurence(instance);
                    }
                    ner.addOccurence(nerOccurence);
                }
                nerObjects.add(ner);
            }
        }
        return nerObjects;
    }

    private NERInstanceObject parseCoreLabel(CoreLabel coreLabel) {
        NERInstanceObject instance = new NERInstanceObject();
        try {
            instance.setValue(coreLabel.get(CoreAnnotations.ValueAnnotation.class));
            instance.setText(coreLabel.get(CoreAnnotations.TextAnnotation.class));
            instance.setOriginalText(coreLabel.get(CoreAnnotations.OriginalTextAnnotation.class));
            instance.setCharOffsetBegin(coreLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class));
            instance.setCharOffsetEnd(coreLabel.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
            instance.setBefore(coreLabel.get(CoreAnnotations.BeforeAnnotation.class));
            instance.setAfter(coreLabel.get(CoreAnnotations.AfterAnnotation.class));
            instance.setIndex(coreLabel.get(CoreAnnotations.IndexAnnotation.class));
            instance.setSentenceIndex(coreLabel.get(CoreAnnotations.SentenceIndexAnnotation.class));
            instance.setPartOfSpeech(coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class));
            instance.setLemma(coreLabel.get(CoreAnnotations.LemmaAnnotation.class));
            instance.setNamedEntityTag(coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class));
            instance.setBeginIndex(coreLabel.get(CoreAnnotations.BeginIndexAnnotation.class));
            instance.setEndIndex(coreLabel.get(CoreAnnotations.EndIndexAnnotation.class));
            instance.setUtterance(coreLabel.get(CoreAnnotations.UtteranceAnnotation.class));
            instance.setParagraph(coreLabel.get(CoreAnnotations.ParagraphAnnotation.class));
            instance.setSpeaker(coreLabel.get(CoreAnnotations.SpeakerAnnotation.class));

            instance.setSentiment(coreLabel.get(SentimentCoreAnnotations.SentimentClass.class));
            instance.setSentiment2(coreLabel.get(SentimentCoreAnnotations.SentimentClass.class));

//            Integer clusterId = coreLabel.get(CorefCoreAnnotations.CorefClusterIdAnnotation.class);
//            instance.setCorefClusterID(clusterId);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }

    public Map<String, String> getSentiment(){
        Map<String, String> sentimentMap = new HashMap();
        for(CoreMap sentence : sentences){
            sentimentMap.put(sentence.toShorterString(),sentence.get(SentimentCoreAnnotations.SentimentClass.class));
        }

        return sentimentMap;
    }
}
