package Geoparser;

import java.util.Properties;

public enum LanguageProperties {
    ARABIC("ARABIC"),
    CHINESE_HYBRID("CHINESE_HYBRID"),
    CHINESE_DETERMINISTIC("CHINESE_DETERMINISTIC"),
//    CHINESE_DETERMINISTIC_CONLL("CHINESE_DETERMINISTIC_CONLL"),
    CHINESE_NEURTAL("CHINESE_NEURTAL"),
//    CHINESE_NEURTAL_CONLL("CHINESE_NEURTAL_CONLL"),
    GERMAN("GERMAN"),
    FRENCH("FRENCH"),
    SPANISH("SPANISH");

    public Properties properties;

    LanguageProperties(String lang) {
        this.properties=new Properties();
        switch (lang) {
            case "ARABIC":
                getArabic();
                break;
            case "CHINESE_HYBRID":
                getChineseHybrid();
                break;
            case "CHINESE_DETERMINISTIC":
                getChineseDeterminisitic();
                break;
            case "CHINESE_DETERMINISTIC_CONLL":
                getChineseDeterminisiticConll();
                break;
            case "CHINESE_NEURTAL":
                getChineseNeutral();
                break;
            case "CHINESE_NEURTAL_CONLL":
                getChineseNeutralConll();
                break;
            case "GERMAN":
                getGerman();
                break;
            case "FRENCH":
                getFrench();
                break;
            case "SPANISH":
                getSpanish();
                break;
        }
    }

    private void getChineseHybrid() {
//        Properties props = PropertiesUtils.asProperties("props", "StanfordCoreNLP-chinese.properties");
        //Pipeline options
        this.properties.setProperty("annotators", "segment, ssplit, pos, lemma, ner, parse");

        this.properties.setProperty("customAnnotatorClass.segment","edu.stanford.nlp.pipeline.ChineseSegmenterAnnotator");
        this.properties.setProperty("segment.model","edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
        this.properties.setProperty("segment.sighanCorporaDict","edu/stanford/nlp/models/segmenter/chinese");
        this.properties.setProperty("segment.serDictionary","edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
        this.properties.setProperty("segment.sighanPostProcessing","true");
        this.properties.setProperty("ssplit.boundaryTokenRegex","[.]|[!?]+|[。]|[！？]+");

        this.properties.setProperty("coref.algorithm", "hybrid");

        this.properties.setProperty("coref.sieves", "ChineseHeadMatch, ExactStringMatch, PreciseConstructs, StrictHeadMatch1, StrictHeadMatch2, StrictHeadMatch3, StrictHeadMatch4, PronounMatch");
        this.properties.setProperty("coref.doScore", "true");
        this.properties.setProperty("coref.postprocessing", "true");
        this.properties.setProperty("coref.calculateFeatureImportance", "false");
        this.properties.setProperty("coref.useConstituencyTree", "true");
        this.properties.setProperty("coref.useSemantics", "false");
        this.properties.setProperty("coref.md.type", "RULE");
        this.properties.setProperty("coref.md.liberalChineseMD", "false");
//        this.properties.setProperty("coref.path.word2vec");
        this.properties.setProperty("coref.language", "zh");
        this.properties.setProperty("coref.print.md.log", "false");
        this.properties.setProperty("coref.big.gender.number", "edu/stanford/nlp/models/dcoref/gender.data.gz");
        this.properties.setProperty("coref.zh.dict", "edu/stanford/nlp/models/dcoref/zh-attributes.txt.gz");

        this.properties.setProperty("coref.conll", "true");

        this.properties.setProperty("coref.specialCaseNewswire", "true");
        this.properties.setProperty("coref.input.type", "conll");

//Evaluation
        this.properties.setProperty("coref.path.output", "/scr/nlp/coref/output/");
        this.properties.setProperty("coref.data", "/scr/nlp/data/conll-2012/");
        this.properties.setProperty("coref.inputPath", "/scr/nlp/data/conll-2012/v4/data/development/data/chinese/annotations");
        this.properties.setProperty("coref.scorer", "/scr/nlp/data/conll-2012/scorer/v8.01/scorer.pl");

//NER
        this.properties.setProperty("ner.model", "edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz");
        this.properties.setProperty("ner.applyNumericClassifiers", "false");
        this.properties.setProperty("ner.useSUTime", "false");
    }

    private void getChineseDeterminisitic() {
        this.properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref");

        this.properties.setProperty("coref.algorithm", "hybrid");
        this.properties.setProperty("coref.language", "zh");
        this.properties.setProperty("coref.sieves", "ChineseHeadMatch, ExactStringMatch, PreciseConstructs, StrictHeadMatch1, StrictHeadMatch2, StrictHeadMatch3, StrictHeadMatch4, PronounMatch");
        this.properties.setProperty("coref.postprocessing", "true");
        this.properties.setProperty("coref.md.liberalChineseMD", "false");
        this.properties.setProperty("coref.zh.dict", "edu/stanford/nlp/models/dcoref/zh-attributes.txt.gz");

        this.properties.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger");
        this.properties.setProperty("parse.model", "edu/stanford/nlp/models/srparser/chineseSR.ser.gz");

    }

    private void getChineseDeterminisiticConll() {
        this.properties.setProperty("coref.algorithm", "hybrid");
        this.properties.setProperty("coref.language", "zh");
        this.properties.setProperty("coref.conll", "true");

        this.properties.setProperty("coref.sieves", "ChineseHeadMatch, ExactStringMatch, PreciseConstructs, StrictHeadMatch1, StrictHeadMatch2, StrictHeadMatch3, StrictHeadMatch4, PronounMatch");
        this.properties.setProperty("coref.postprocessing", "true");
        this.properties.setProperty("coref.md.liberalChineseMD", "false");
        this.properties.setProperty("coref.zh.dict", "edu/stanford/nlp/models/dcoref/zh-attributes.txt.gz");
        this.properties.setProperty("coref.specialCaseNewswire", "true");
    }

    private void getChineseNeutral() {
        this.properties.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, mention, coref");
        this.properties.setProperty("coref.algorithm", "neural");
        this.properties.setProperty("coref.language", "chinese");

        this.properties.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger");
        this.properties.setProperty("parse.model", "edu/stanford/nlp/models/srparser/chineseSR.ser.gz");
    }

    private void getChineseNeutralConll() {
        this.properties.setProperty("coref.algorithm", "neural");
        this.properties.setProperty("coref.language", "chinese");
        this.properties.setProperty("coref.conll", "true");

        this.properties.setProperty("ner.applyNumericClassifiers", "false");
        this.properties.setProperty("ner.useSUTime", "false");
        this.properties.setProperty("ner.model", "edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz");

    }
    
    private void getFrench(){
        this.properties.setProperty("annotators","tokenize, ssplit, pos, parse");

        this.properties.setProperty("tokenize.language","fr");

        this.properties.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/french/french.tagger");

        this.properties.setProperty("parse.model","edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");

// dependency parser
        this.properties.setProperty("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_French.gz");
        this.properties.setProperty("depparse.language","french");
    }
    
    private void getGerman(){
        this.properties.setProperty("annotators","tokenize, ssplit, pos, ner, parse");

        this.properties.setProperty("tokenize.language","de");

        this.properties.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger");

        this.properties.setProperty("ner.model","edu/stanford/nlp/models/ner/german.conll.hgc_175m_600.crf.ser.gz");
        this.properties.setProperty("ner.applyNumericClassifiers","false");
        this.properties.setProperty("ner.useSUTime","false");

        this.properties.setProperty("parse.model","edu/stanford/nlp/models/lexparser/germanFactored.ser.gz");

//# depparse
        this.properties.setProperty("depparse.model   ","edu/stanford/nlp/models/parser/nndep/UD_German.gz");
        this.properties.setProperty("depparse.language","german");
    }
    
    private void getSpanish(){
        this.properties.setProperty("annotators","tokenize, ssplit, pos, ner, parse");

        this.properties.setProperty("tokenize.language","es");

        this.properties.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");

        this.properties.setProperty("ner.model","edu/stanford/nlp/models/ner/spanish.ancora.distsim.s512.crf.ser.gz");
        this.properties.setProperty("ner.applyNumericClassifiers","false");
        this.properties.setProperty("ner.useSUTime","false");

        this.properties.setProperty(" parse.model","edu/stanford/nlp/models/lexparser/spanishPCFG.ser.gz");

        this.properties.setProperty("depparse.model","edu/stanford/nlp/models/parser/nndep/UD_Spanish.gz");
        this.properties.setProperty("depparse.language","spanish");
    }
    
    private void getArabic(){
//        # Pipeline options
        this.properties.setProperty("annotators","tokenize, ssplit, pos, parse");

//# segment
//#customAnnotatorClass.segment","edu.stanford.nlp.pipeline.ArabicSegmenterAnnotator
        this.properties.setProperty("tokenize.language","ar");
        this.properties.setProperty("segment.model","edu/stanford/nlp/models/segmenter/arabic/arabic-segmenter-atb+bn+arztrain.ser.gz");

//# sentence split
        this.properties.setProperty("ssplit.boundaryTokenRegex","[.]|[!?]+|[!\u061F]+");

//# pos
                this.properties.setProperty("pos.model","edu/stanford/nlp/models/pos-tagger/arabic/arabic.tagger");

//# parse
        this.properties.setProperty("parse.model","edu/stanford/nlp/models/lexparser/arabicFactored.ser.gz");
    }
}
