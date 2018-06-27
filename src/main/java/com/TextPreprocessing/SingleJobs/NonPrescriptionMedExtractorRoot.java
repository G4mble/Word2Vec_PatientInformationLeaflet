package com.TextPreprocessing.SingleJobs;

import com.Configuration.CommonPreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.TextPreprocessing.PreprocessingCore.CommonPreprocessingUtils;
import com.Utility.Helper.CollectionHelper;
import com.Utility.Helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NonPrescriptionMedExtractorRoot
{
    private final String INPUT_FILE_PATH = "H:\\Daten\\Uni\\Master\\2.Semester\\EEB\\%Project\\009_FreieMedikamente\\Products.txt";
    private final String OUTPUT_FILE_PATH = "H:\\Daten\\Uni\\Master\\2.Semester\\EEB\\%Project\\009_FreieMedikamente\\Products_preprocessed.txt";
    private static final String CONFIG_PATH = "configuration/commonPreprocessing_config.cfg";
    private final int TOKEN_MIN_LENGTH = 3;
    private static final Logger _log = LoggerFactory.getLogger(NonPrescriptionMedExtractorRoot.class);
    private static IPreprocessingUtility _preprocessingUtils;
    private static CommonPreprocessingConfiguration _config;

    public static void main(String[] args)
    {
        NonPrescriptionMedExtractorRoot extractor = new NonPrescriptionMedExtractorRoot();
        _config = new CommonPreprocessingConfiguration(_log);
        _config.initialize(CONFIG_PATH);
        _preprocessingUtils = new CommonPreprocessingUtils(_config, _log);
        extractor.runProcess();
    }

    private String preprocess(String input)
    {
        input = _preprocessingUtils.transformToLowerCaseTrim(input);
        input = _preprocessingUtils.fixWhiteSpaces(input);
        input = _preprocessingUtils.replaceSpecialMedCharacters(input);
        try
        {
            input = _preprocessingUtils.unescapeText(input);
        }
        catch (Exception ex)
        {
            _log.warn("Unable to unescape text. Skip line: " + input);
            return null;
        }
        input = _preprocessingUtils.replaceUmlauts(input);
        input = _preprocessingUtils.replaceDates(input);
        input = _preprocessingUtils.replaceSpecialCharacters(input);
        input = _preprocessingUtils.normalizeText(input);
        input = _preprocessingUtils.replaceNonAsciiCharacters(input);

        try
        {
            List<String> inputSplit = new ArrayList<>(Arrays.asList(input.split(" ")));
            inputSplit = _preprocessingUtils.ensureTokenMinLength(inputSplit, TOKEN_MIN_LENGTH);
            //TODO TS evalutate
            if(inputSplit.size() == 0)
                return null;
            int index = 0;
            int max = inputSplit.size();
            input = "";
            while(input.isEmpty() && index < max)
            {
                input = inputSplit.get(index);
                index++;
            }
            if(input.isEmpty())
                return null;
            //TODO TS
//            input = CollectionHelper.collectionToString(inputSplit, " ");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        input = _preprocessingUtils.normalizeWhitespaces(input);

        return input.trim();
    }

    private void runProcess()
    {
        try
        {
            File outputFile = FileHelper.createFileAndDirectory(OUTPUT_FILE_PATH);
            List<String> inputMeds = FileHelper.loadDocumentLinesToList(INPUT_FILE_PATH, StandardCharsets.UTF_8);
            List<String> outputMeds = new ArrayList<>();
            for(String currentMed:inputMeds)
            {
                currentMed = preprocess(currentMed);
                if(currentMed != null && currentMed.length() > 0)
                    outputMeds.add(currentMed);
            }
            Set<String> uniqueMeds = new LinkedHashSet<>(outputMeds);
            String output = CollectionHelper.collectionToString(uniqueMeds, System.lineSeparator());
            FileHelper.writeContentToExistingFile(output.trim(), outputFile);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}