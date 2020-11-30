package com.breaking.news.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenNLPAEnglishAnalyzer {

    private static OpenNLPAEnglishAnalyzer instance;

    public static OpenNLPAEnglishAnalyzer getInstance() {
        if (instance == null) {
            instance = new OpenNLPAEnglishAnalyzer();
        }
        return instance;
    }

    private static POSModel getPosModel() throws IOException {
        File f = new File("en-pos-maxent.bin");
        InputStream modelIn = new FileInputStream(f);
        return new POSModel(modelIn);
    }

    public List<String> getNounsFromText(String text) {

        try {
            text = replaceAllSpecialCharacterByEmptyCharacter(text);

            POSModel posModel = getPosModel();
            POSTaggerME tagger = new POSTaggerME(posModel);

            Analyzer analyzer = new EnglishAnalyzer();
            TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(text));

            List<String> allWords = analyzeText(analyzer, tokenStream);
            List<String> validNouns = extractNounsFromText(tagger, allWords);

            return validNouns;

        } catch (IOException ex) {
            log.error("Error occurred while analyzing the text `{}`. Error message: `{}`", text, ex.getMessage());
        }
        return new ArrayList<>();
    }

    private String replaceAllSpecialCharacterByEmptyCharacter(String text) {
        //^ - Match any character that is not in the set.
        //w - (word)  Matches any word character (alphanumeric & underscore).
        //s - Matches any whitespace character (spaces, tabs, line breaks).
        // See: https://regexr.com/
        return text.replaceAll("[^\\w\\s]", "");
    }

    private List<String> extractNounsFromText(POSTaggerME tagger, List<String> allWords) {
        String[] tokenizerLine = allWords.toArray(String[]::new);
        String[] tags = tagger.tag(tokenizerLine);

        List<String> validNouns = new ArrayList<>();
        for (int index = 0; index < tokenizerLine.length; index++) {

            if (tags[index].equals("NN")) {
                validNouns.add(tokenizerLine[index]);
            }
        }
        return validNouns;
    }

    private List<String> analyzeText(Analyzer analyzer, TokenStream tokenStream) throws IOException {
        List<String> words = new ArrayList<>();
        CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
        try {
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                words.add(term.toString());
            }

            tokenStream.end();
        } catch (IOException ex) {
            log.error("Error occurred while extracting text from analyzer. `{}`", ex.getMessage());
        } finally {
            tokenStream.close();
            analyzer.close();
        }

        return words;
    }
}
