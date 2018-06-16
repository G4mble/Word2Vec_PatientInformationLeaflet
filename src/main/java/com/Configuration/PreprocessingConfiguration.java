package com.Configuration;

import org.slf4j.Logger;

public class PreprocessingConfiguration extends ConfigurationBase
{
    //region Fields

    private boolean removeStopwords;
    private boolean removeAbbreviations;
    private boolean checkTokenMinLength;

    private int tokenMinLength;

    private String dataFileExtension;
    private String stopwordsFilePath;
    private String abbreviationsFilePath;
    private String inputSourcePath;
    private String outputPath;

    //endregion

    //region Constructors

    public PreprocessingConfiguration(String localResourceConfigFilePath, Logger log)
    {
        _log = log;
        try
        {
            initializeInternal(localResourceConfigFilePath);
        }
        catch (Exception ex)
        {
            _log.error("Error on initialize: PreprocessingConfiguration", ex);
        }
    }

    //endregion

    //region Private / Protected Methods

    @Override
    protected boolean processLineContent(String line)
    {
        line = line.replaceAll("\\%.*?\\%", "");
        if (line.length() == 0)
            return true; //config still valid as we just removed a commented line or hit an empty line

        String[] elements = line.split("=");
        if (elements.length != 2)
            return false;

        String leftHandSide = elements[0].trim();
        String rightHandSide = elements[1].trim();

        try
        {
            switch(leftHandSide)
            {
                case "removeStopwords":
                    removeStopwords = Boolean.parseBoolean(rightHandSide);
                    break;
                case "removeAbbreviations":
                    removeAbbreviations = Boolean.parseBoolean(rightHandSide);
                    break;
                case "checkTokenMinLength":
                    checkTokenMinLength = Boolean.parseBoolean(rightHandSide);
                    break;
                case "tokenMinLength":
                    tokenMinLength = Integer.parseInt(rightHandSide);
                    break;
                case "dataFileExtension":
                    dataFileExtension = rightHandSide;
                    break;
                case "stopwordsFilePath":
                    stopwordsFilePath = rightHandSide;
                    break;
                case "abbreviationsFilePath":
                    abbreviationsFilePath = rightHandSide;
                    break;
                case "inputSourcePath":
                    inputSourcePath = rightHandSide;
                    break;
                case "outputPath":
                    outputPath = rightHandSide;
                    break;
                default:
                    return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in processLineContent.", ex);
        }
        return false;
    }

    //endregion

    //region Getter / Setter

    public boolean getRemoveStopwords()
    {
        return removeStopwords;
    }

    public boolean getRemoveAbbreviations()
    {
        return removeAbbreviations;
    }

    public boolean getCheckTokenMinLength()
    {
        return checkTokenMinLength;
    }

    public int getTokenMinLength()
    {
        return tokenMinLength;
    }

    public String getDataFileExtension()
    {
        return dataFileExtension;
    }

    public boolean getPerformPerWordProcesses()
    {
        return removeStopwords || removeAbbreviations;
    }

    public String getStopwordsFilePath()
    {
        return stopwordsFilePath;
    }

    public String getAbbreviationsFilePath()
    {
        return abbreviationsFilePath;
    }

    public String getInputSourcePath()
    {
        return inputSourcePath;
    }

    public String getOutputPath()
    {
        return outputPath;
    }

    //endregion
}