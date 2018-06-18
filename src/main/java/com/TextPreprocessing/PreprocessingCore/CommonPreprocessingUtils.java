package com.TextPreprocessing.PreprocessingCore;

import com.Configuration.CommonPreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.Utility.Helper.FileHelper;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonPreprocessingUtils implements IPreprocessingUtility
{
    //region Fields

    private Set<String> _stopwords;
    private Set<String> _abbreviations;

    private final Logger _log;

    //endregion

    public CommonPreprocessingUtils(CommonPreprocessingConfiguration config, Logger log)
    {
        _log = log;
        initializeInternal(config);
    }

    //region Private Methods

    private void loadStopwords(String localResourceFile)
    {
        System.out.println("INFO: Loading stopwords from file...");
        try
        {
            _stopwords = FileHelper.loadDocumentLinesToSetFromLocalResource(localResourceFile, Charset.forName("UTF-8"));
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in loadStopwords.", ex);
        }
    }

    private void loadAbbreviations(String localResourceFile)
    {
        System.out.println("INFO: Loading abbreviations from file...");
        try
        {
            _abbreviations = FileHelper.loadDocumentLinesToSetFromLocalResource(localResourceFile, Charset.forName("UTF-8"));
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in loadAbbreviations.", ex);
        }
    }

    private void initializeInternal(CommonPreprocessingConfiguration config)
    {
        loadStopwords(config.getStopwordsFilePath());
        loadAbbreviations(config.getAbbreviationsFilePath());
    }

    //endregion

    //region Interface Methods

    @Override
    public List<String> removeStopwords(List<String> input)
    {
        input.removeAll(_stopwords);
        return input;
    }

    @Override
    public List<String> removeAbbreviations(List<String> input)
    {
        input.removeAll(_abbreviations);
        return input;
    }

    @Override
    public List<String> ensureTokenMinLength(List<String> input, int tokenMinLength)
    {
        Set<String> toRemove = new HashSet<>();
        for(String word:input)
        {
            if(word.length() < tokenMinLength)
                toRemove.add(word);
        }
        input.removeAll(toRemove);
        return input;
    }

    @Override
    public String fixWhiteSpaces(String input)
    {
        return input.replaceAll("\\u00A0", " ");
    }

    @Override
    public String normalizeWhitespaces(String input)
    {
        return input.trim().replaceAll("[ ]+", " ");
    }

    @Override
    public String transformToLowerCaseTrim(String input)
    {
        return input.toLowerCase().trim();
    }

    @Override
    public String replaceUmlauts(String input)
    {
        return input.replaceAll("ü", "ue").replaceAll("ä", "ae")
                    .replaceAll("ö", "oe").replaceAll("ß", "ss");
    }

    @Override
    public String replaceDates(String input)
    {
        //remove all dates separated by dot, hyphen or slash [dd.MM.YY{YY} OR MM.dd.YY{YY} OR d.M.YY{YY} OR M.d.YY{YY}]
        return input.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
    }

    @Override
    public String replaceHyphenAndApostropheWithWhitespace(String input)
    {
        return input.replaceAll("['-]+", " ");
    }

    @Override
    public String replaceAllSentenceEndingWithDot(String input)
    {
        return input.replaceAll("[!?]", "\\.");
    }

    @Override
    public String replaceSpecialCharacters(String input)
    {
        return input.replaceAll("[\\d\"§$%&/()=`ß?!,;.:´²³{\\[\\]}\\\\+*~<>_#'’\\-|^°@€]+", " ");
    }

    @Override
    public String replaceSpecialCharactersExceptDot(String input)
    {
        return input.replaceAll("[\\d\"§$%&/()=`ß?!,;:´²³{\\[\\]}\\\\+*~<>_#'’\\-|^°@€]+", " ");
    }

    @Override
    public String normalizeText(String input)
    {
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        return input.replaceAll("[^\\p{ASCII}]", "");
    }

    @Override
    public String unescapeText(String input)
    {
        input = StringEscapeUtils.unescapeXml(input);
        input = StringEscapeUtils.unescapeJava(input);
        input = input.replace("c;", "c");
        input = input.replace("ø", "oe");
        return input;
    }

    @Override
    public String replaceNonAsciiCharacters(String input)
    {
        return input.replaceAll("[^\\x00-\\x7F]+", " ").replaceAll("\\uFFFD", " ");
    }

    @Override
    public String replaceSpecialMedCharacters(String input)
    {
        input = input.replaceAll("o25", "oe");
        input = input.replaceAll("a25", "ae");
        input = input.replaceAll("u25", "ue");
        return input.replaceAll("s39", "ss");
    }

    @Override
    public String repairMissingWhitespaces(String input)
    {
        return input.replaceAll("([A-Z])([A-Z])([a-z])|([a-z])([A-Z])", "$1$4 $2$3$5");
    }

    //endregion
}