package com.TextPreprocessing.DataSourcePreprocessor;

import com.Configuration.MedDataPreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.Contracts.ITextPreprocessor;
import com.Utility.Helper.CollectionHelper;
import com.Utility.Helper.FileHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MedPDFPreprocessor implements ITextPreprocessor
{
    //region Fields

    private final MedDataPreprocessingConfiguration _config;
    private final Logger _log;
    private IPreprocessingUtility _preprocessingUtils;
    private int _linesSkipped;
    private int _documentsEmpty;

    //endregion

    //region Constructors

    public MedPDFPreprocessor(MedDataPreprocessingConfiguration config, Logger log)
    {
        _config = config;
        _log = log;
    }

    //endregion

    @Override
    public void runProcess(IPreprocessingUtility preprocessingUtils)
    {
        _preprocessingUtils = preprocessingUtils;
        try
        {
            _linesSkipped = 0;
            _documentsEmpty = 0;
            runProcessInternal();

            _log.info("Total lines skipped: " + _linesSkipped);
            _log.info("Total documents empty: " + _documentsEmpty);
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in runProcess.", ex);
        }
    }

    private String processLine(String input)
    {
        input = _preprocessingUtils.repairMissingWhitespaces(input);
        input = _preprocessingUtils.transformToLowerCaseTrim(input);
        input = _preprocessingUtils.fixWhiteSpaces(input);
        if(input.startsWith("trial")) return null;
        input = input.replaceAll("\\*demo\\*\\S*", " ");
        input = _preprocessingUtils.replaceSpecialMedCharacters(input);
        try
        {
            input = _preprocessingUtils.unescapeText(input);
        }
        catch (Exception ex)
        {
            _log.warn("Unable to unescape text. Skip line: " + input);
            _linesSkipped++;
            return null;
        }
        input = _preprocessingUtils.replaceUmlauts(input);
        input = _preprocessingUtils.replaceDates(input);
        input = _preprocessingUtils.replaceSpecialCharacters(input);

        if (_config.getPerformPerWordProcesses())
        {
            List<String> lineSplit = new ArrayList<>(Arrays.asList(input.split(" ")));
            if (_config.getRemoveAbbreviations())
                lineSplit = _preprocessingUtils.removeAbbreviations(lineSplit);
            if (_config.getRemoveStopwords())
                lineSplit = _preprocessingUtils.removeStopwords(lineSplit);
            if (_config.getCheckTokenMinLength())
                lineSplit = _preprocessingUtils.ensureTokenMinLength(lineSplit, _config.getTokenMinLength());

            if(lineSplit.size() < 3)
                return null;
            input = CollectionHelper.collectionToString(lineSplit, " ");
        }

        input = _preprocessingUtils.normalizeText(input);
        input = _preprocessingUtils.replaceNonAsciiCharacters(input);
        input = _preprocessingUtils.normalizeWhitespaces(input);

        return input.trim();
    }

    private String preprocessFile(File file, Object lineSeparator)
    {
        String currentLine;
        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader))
        {
            while ((currentLine = br.readLine()) != null)
            {
                currentLine = processLine(currentLine);
                if(currentLine == null || currentLine.length() < 15)
                    continue;
                builder.append(currentLine.trim()).append(lineSeparator);
            }
            return builder.toString().trim();
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in preprocessFile.", ex);
        }
        return null;
    }

    private void preprocess(Path path, String outputPath)
    {
        _log.info("Processing " + path + " ...");
        File inputFile;
        File outputFile;
        try
        {
            inputFile = path.toFile();
            String outputFileName = outputPath + "\\" + inputFile.getName().replace(".txt", "") + "_preprocessed.txt";
            outputFile = FileHelper.createFileAndDirectory(outputFileName);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in preprocess.", ex);
            _log.warn("TERMINATING...");
            return;
        }

        try
        {
            String output = preprocessFile(inputFile, System.lineSeparator());
            if(output == null)
            {
                _documentsEmpty++;
                _log.warn("PROCESS-FILE RETURNED NULL -- CHECK DOCUMENT: " + path);
                return;
            }
            FileHelper.writeContentToExistingFile(output.trim(), outputFile);
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in preprocess.", ex);
        }
    }

    private void runProcessInternal()
    {
        try(Stream<Path> paths = Files.walk(Paths.get(_config.getInputSourcePath())))
        {
            paths.filter(Files::isRegularFile).forEach(x -> preprocess(x, _config.getOutputPath()));
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in runProcessInternal.", ex);
        }
    }
}