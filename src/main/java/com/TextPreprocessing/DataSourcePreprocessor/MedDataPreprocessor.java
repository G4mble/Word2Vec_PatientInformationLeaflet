package com.TextPreprocessing.DataSourcePreprocessor;

import com.Configuration.MedDataPreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.Contracts.ITextPreprocessor;
import com.Utility.Helper.CollectionHelper;
import com.Utility.Helper.FileHelper;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MedDataPreprocessor implements ITextPreprocessor
{
    //region Fields

    private IPreprocessingUtility _preprocessingUtils;
    private final MedDataPreprocessingConfiguration _config;
    private final Logger _log;
    private int _linesSkipped;
    private int _documentsEmpty;
    private int _currentDocNumber;
    private long _numberOfDocs;

    //endregion

    //region Constructors

    public MedDataPreprocessor(MedDataPreprocessingConfiguration config, Logger log)
    {
        _config = config;
        _log = log;
    }

    //endregion

    //region Public Methods

    @Override
    public void runProcess(IPreprocessingUtility preprocessingUtils)
    {
        _preprocessingUtils = preprocessingUtils;
        try
        {
            _linesSkipped = 0;
            _documentsEmpty = 0;
            if(_config.getConfigureOneMedPerLine())
                runOneMedPerLineSingleDocument();
            else
                runOneSentecePerLineOneMedPerDocument();

            _log.info("Total lines skipped: " + _linesSkipped);
            _log.info("Total documents empty: " + _documentsEmpty);
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in runProcess.", ex);
        }
    }

    //endregion

    //region Private Methods

    //region COMMON

    private String ensureAbdamedFirstLine(String input)
    {
        String[] firstLineItems = input.split(":");
        return firstLineItems[0];
    }

    private String processAbdamedLine(String input, boolean forceKeepLine)
    {
        input = _preprocessingUtils.repairMissingWhitespaces(input);
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
            _linesSkipped++;
            return null;
        }
        input = _preprocessingUtils.replaceUmlauts(input);
        input = _preprocessingUtils.replaceDates(input);
        input = _preprocessingUtils.replaceSpecialCharacters(input);
        input = _preprocessingUtils.normalizeText(input);
        input = _preprocessingUtils.replaceNonAsciiCharacters(input);

        if (_config.getPerformPerWordProcesses())
        {
            List<String> lineSplit = new ArrayList<>(Arrays.asList(input.split(" ")));
            if (_config.getRemoveAbbreviations())
                lineSplit = _preprocessingUtils.removeAbbreviations(lineSplit);
            if (_config.getRemoveStopwords())
                lineSplit = _preprocessingUtils.removeStopwords(lineSplit);
            if (_config.getCheckTokenMinLength())
                lineSplit = _preprocessingUtils.ensureTokenMinLength(lineSplit, _config.getTokenMinLength());

            if(!forceKeepLine && lineSplit.size() < 3)
                return null;
            input = CollectionHelper.collectionToString(lineSplit, " ");
        }

        input = _preprocessingUtils.normalizeWhitespaces(input);

        return input.trim();
    }

    private String processAbdamedFile(File file, Object lineSeparator)
    {
        String currentLine;
        boolean firstLineProcessed = false;
        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader))
        {
            while ((currentLine = br.readLine()) != null)
            {
                boolean forceKeepLine = false;
                if(!firstLineProcessed)
                {
                    currentLine = ensureAbdamedFirstLine(currentLine);
                    firstLineProcessed = true;
                    forceKeepLine = true;
                }
                else if(!_config.getKeepCategoryNamePerLine() || !_config.getKeepMedNamePerLine())
                {
                    String newLine = currentLine.substring(currentLine.indexOf(":") + 1, currentLine.length()).trim();
                    if(!_config.getKeepCategoryNamePerLine() && !_config.getKeepMedNamePerLine() )
                    {
                        currentLine = newLine;
                    }
                    else
                    {
                        String lineCategoryPrefix = currentLine.substring(0, currentLine.indexOf(":")).trim();
                        String[] split = lineCategoryPrefix.split(" ");
                        String categoryPrefix = split[split.length - 1];
                        if(!_config.getKeepCategoryNamePerLine())
                        {
                            lineCategoryPrefix = lineCategoryPrefix.replace(categoryPrefix, " ");
                        }
                        else
                        {
                            //case: only keep categoryName
                            lineCategoryPrefix = categoryPrefix;
                        }
                        currentLine = lineCategoryPrefix + " " + newLine;
                    }
                }

                currentLine = processAbdamedLine(currentLine, forceKeepLine);
                if(currentLine == null || (!forceKeepLine && currentLine.length() < 15))
                    continue;
                builder.append(currentLine.trim()).append(lineSeparator);
            }
            return builder.toString().trim();
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in processAbdamedFile.", ex);
        }
        return null;
    }

    //endregion

    //region ONE SENTENCE PER LINE - ONE MED PER DOCUMENT

    private void preprocessOneSentecePerLineOneMedPerDocument(Path path, String saveDirectory)
    {
        _log.info("Processing " + path + " ...");
        File inputFile;
        File outputFile;
        try
        {
            inputFile = path.toFile();
            String outputFileName = saveDirectory + "\\" + inputFile.getName().replace(".txt", "") + "_preprocessed.txt";
            outputFile = FileHelper.createFileAndDirectory(outputFileName);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in preprocessOneSentecePerLineOneMedPerDocument.", ex);
            _log.warn("TERMINATING...");
            return;
        }

        try
        {
            String output = processAbdamedFile(inputFile, System.lineSeparator());
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
            _log.error("Unexpected error in preprocessOneSentecePerLineOneMedPerDocument.", ex);
        }
    }

    private void runOneSentecePerLineOneMedPerDocument()
    {
        _log.warn("PERFORMING OPERATION FOR: \"ONE SENTENCE PER LINE - ONE MED PER DOCUMENT\"");
        try(Stream<Path> paths = Files.walk(Paths.get(_config.getInputSourcePath())))
        {
            paths.filter(Files::isRegularFile).forEach(x -> preprocessOneSentecePerLineOneMedPerDocument(x, _config.getOutputPath()));
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in runOneSentencePerLineOneMedPerDocument.", ex);
        }
    }

    //endregion

    //region ONE MED PER LINE - SINGLE DOCUMENT

    private void preprocessOneMedPerLineSingleDocument(Path path, BufferedWriter writer)
    {
        _log.info("Processing " + path + " ...");
        File currentFile = path.toFile();
        try
        {
            String output = processAbdamedFile(currentFile, " ");
            if(output == null)
            {
                _documentsEmpty++;
                _log.warn("PROCESS-FILE RETURNED NULL -- CHECK DOCUMENT: " + path);
                return;
            }
            writer.write(output.trim());

            if(_currentDocNumber != _numberOfDocs)
                writer.newLine();

            writer.flush();
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in preprocessOneMedPerLineSingleDocument.", ex);
        }
        finally
        {
            _currentDocNumber++;
        }
    }

    private void runOneMedPerLineSingleDocument()
    {
        _log.warn("PERFORMING OPERATION FOR: \"ONE MED PER LINE - SINGLE DOCUMENT\"");
        try
        {
            Stream<Path> countHelperPath = Files.walk(Paths.get(_config.getInputSourcePath()));
            _numberOfDocs = countHelperPath.filter(Files::isRegularFile).count();
            _currentDocNumber = 1;
            countHelperPath.close();

            String saveDirectory = _config.getOutputPath() + "\\"+ "medData_combined.txt";
            File outputFile = FileHelper.createFileAndDirectory(saveDirectory);

            try(Stream<Path> paths = Files.walk(Paths.get(_config.getInputSourcePath()));
                FileWriter fw = new FileWriter(outputFile);
                BufferedWriter writer = new BufferedWriter(fw))
            {
                paths.filter(Files::isRegularFile).forEach(x -> preprocessOneMedPerLineSingleDocument(x, writer));
            }
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in runOneMedPerLineSingleDocument.", ex);
        }
    }

    //endregion

    //endregion
}