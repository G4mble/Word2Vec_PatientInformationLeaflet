package com.Utility.TextPreprocessing.DataSourcePreprocessor;

import com.Configuration.PreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.Utility.Helper.CollectionHelper;
import com.Utility.Helper.FileHelper;
import com.Utility.Helper.ResourceProvider;
import org.slf4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class WikipediaPreprocessor
{
    //region Fields

    private long _linesSkipped;

    private final IPreprocessingUtility _preprocessingUtils;
    private final PreprocessingConfiguration _config;
    private final Logger _log;

    //endregion

    //region Constructors

    public WikipediaPreprocessor(IPreprocessingUtility preprocessingUtils, PreprocessingConfiguration config, Logger log)
    {
        _preprocessingUtils = preprocessingUtils;
        _config = config;
        _log = log;
    }

    //endregion

    //region Public Methods

    public void runProcess()
    {
        try
        {
            File parentDir = ResourceProvider.getLocalResource(_config.getInputSourcePath()).toFile();
            File[] subDirs = parentDir.listFiles();
            if(subDirs == null)
            {
                _log.error("Error in WikiPreprocessing. No Sub-Directories found.");
                return;
            }
            int directoryIndex = 1001;
            for (File directory:subDirs)
            {
                if(!directory.isDirectory())
                    continue;
                processDirectory(directory.toPath(), directoryIndex);
                directoryIndex++;
            }
            _log.info("Total lines skipped: " + _linesSkipped);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in runProcess.", ex);
        }
    }

    //endregion

    //region Private Methods

    private void processLine(String input, StringBuilder fileBuilder)
    {
        input = _preprocessingUtils.fixWhiteSpaces(input);
        try
        {
            input = _preprocessingUtils.unescapeText(input);
        }
        catch (Exception ex)
        {
            _log.warn("Unable to unescape text. Skip line: " + input);
            _linesSkipped++;
            return;
        }
        input =_preprocessingUtils.transformToLowerCaseTrim(input);
        input = _preprocessingUtils.replaceUmlauts(input);
        input = _preprocessingUtils.replaceDates(input);
        input = _preprocessingUtils.replaceSpecialCharactersExceptDot(input);

        if(_config.getPerformPerWordProcesses())
        {
            List<String> lineSplit = new ArrayList<>(Arrays.asList(input.split(" ")));
            if(_config.getRemoveAbbreviations())
                lineSplit = _preprocessingUtils.removeAbbreviations(lineSplit);
            if(_config.getRemoveStopwords())
                lineSplit = _preprocessingUtils.removeStopwords(lineSplit);

            input = CollectionHelper.collectionToString(lineSplit);
        }

        input = _preprocessingUtils.replaceAllSentenceEndingWithDot(input);
        String[] lines = input.split("\\.");
        StringBuilder outputBuilder = new StringBuilder();
        for(String currentLine:lines)
        {
            int wordCount = 0;
            currentLine = currentLine.replace(".", " ");
            String[] wordsInLine = currentLine.split(" ");
            StringBuilder lineBuilder = new StringBuilder();
            for(String word:wordsInLine)
            {
                if(_config.getCheckTokenMinLength() && (word.length() < _config.getTokenMinLength()))
                    continue;
                lineBuilder.append(word).append(" ");
                wordCount++;
            }
            currentLine = lineBuilder.toString().trim();

            if(currentLine.length() < 15 || wordCount < 3)
                continue;
            outputBuilder.append(currentLine).append(System.lineSeparator());
        }
        input = outputBuilder.toString().trim();

        if(input.length() < 15)
            return;

        input = _preprocessingUtils.normalizeText(input);
        input = _preprocessingUtils.replaceNonAsciiCharacters(input);
        input = _preprocessingUtils.normalizeWhitespaces(input);

        if(input.length() < 15)
            return;

        fileBuilder.append(input.trim()).append(System.lineSeparator());
    }

    private void concatenateFile(Path path, StringBuilder fileBuilder)
    {
        _log.info("Processing " + path + " ...");
        try
        {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            lines.forEach(x -> processLine(x, fileBuilder));
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in concatenateFile.", ex);
        }
    }

    private void processDirectory(Path directory, int directoryIndex)
    {
        try (Stream<Path> paths = Files.walk(directory))
        {
            String fullFileName = _config.getOutputPath() + "\\wiki-combined_" + directoryIndex + _config.getDataFileExtension();
            File outputFile = FileHelper.createFileAndDirectory(fullFileName);
            StringBuilder fileBuilder = new StringBuilder();
            paths.filter(Files::isRegularFile).forEach(x -> concatenateFile(x, fileBuilder));
            FileHelper.writeContentToExistingFile(fileBuilder.toString().trim(), outputFile);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in processDirectory", ex);
        }
    }

    //endregion
}
