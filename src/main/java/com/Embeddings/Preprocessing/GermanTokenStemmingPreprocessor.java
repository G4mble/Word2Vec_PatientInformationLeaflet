package com.Embeddings.Preprocessing;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;

import java.util.regex.Pattern;

public class GermanTokenStemmingPreprocessor implements TokenPreProcess
{
    private final Pattern _preprocessPattern = Pattern.compile("[\\d!\"§$%&/()=?`ß´²³{\\[\\]}\\\\+*~#'\\-_.:,;<>|^°@€\\uFFFD]+");
    private final Pattern _nonAsciiPattern = Pattern.compile("[^\\x00-\\x7F]");

    @Override
    public String preProcess(String token)
    {
        return token;
//        String output = _preprocessPattern.matcher(token.toLowerCase()).replaceAll("");
//        output = _nonAsciiPattern.matcher(output).replaceAll("").trim();
//        //TODO TS evaluate stemming
//        output = GermanLanguageStemmer.stem(output, true);
//        return output.length() > 2 ? output : "";
    }
}