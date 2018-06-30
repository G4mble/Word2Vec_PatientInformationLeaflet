package com.Utility.Helper;

import com.Contracts.IPreprocessingUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PreprocessingHelper
{
    //region Public Methods

    public static List<String> preprocessElementFromStringToList(String input, IPreprocessingUtility preprocessingUtils)
    {
        input = preprocessSingleElement(input, preprocessingUtils);
        if(input == null)
            return null;
        return new ArrayList<>(Arrays.asList(input.split(" ")));
    }

    public static String preprocessSingleElement(String input, IPreprocessingUtility preprocessingUtils)
    {
        return preprocessElementCore(input, preprocessingUtils);
    }

    public static Collection<String> preprocessElementsFromCollection(Collection<String> input, IPreprocessingUtility preprocessingUtils)
    {
        Collection<String> output = new ArrayList<>();
        for(String element:input)
        {
            element = preprocessElementCore(element, preprocessingUtils);
            if(element != null)
                output.add(element);
        }
        return output.size() > 0 ? output : null;
    }

    //endregion

    //region Private Methods

    private static String preprocessElementCore(String input, IPreprocessingUtility preprocessingUtils)
    {
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

    //endregion
}