package com.Utility.Helper;

import com.Contracts.IPreprocessingUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreprocessingHelper
{
    public static List<String> preprocessElementToList(String input, IPreprocessingUtility preprocessingUtils)
    {
        input = preprocessElement(input, preprocessingUtils);
        if(input == null)
            return null;
        return new ArrayList<>(Arrays.asList(input.split(" ")));
    }

    public static String preprocessElement(String input, IPreprocessingUtility preprocessingUtils)
    {
        input = preprocessingUtils.repairMissingWhitespaces(input);
        input = preprocessingUtils.transformToLowerCaseTrim(input);
        input = preprocessingUtils.fixWhiteSpaces(input);
        input = preprocessingUtils.replaceSpecialMedCharacters(input);
        try
        {
            input = preprocessingUtils.unescapeText(input);
        }
        catch (Exception ex)
        {
            return null;
        }
        input = preprocessingUtils.replaceUmlauts(input);
        input = preprocessingUtils.replaceDates(input);
        input = preprocessingUtils.replaceSpecialCharacters(input);
        input = preprocessingUtils.normalizeText(input);
        input = preprocessingUtils.replaceNonAsciiCharacters(input);

        List<String> lineSplit = new ArrayList<>(Arrays.asList(input.split(" ")));
        lineSplit = preprocessingUtils.ensureTokenMinLength(lineSplit, 3);

        if(lineSplit.size() == 0)
            return null;
        input = CollectionHelper.collectionToString(lineSplit, " ");

        input = preprocessingUtils.normalizeWhitespaces(input);

        return input.trim();
    }
}