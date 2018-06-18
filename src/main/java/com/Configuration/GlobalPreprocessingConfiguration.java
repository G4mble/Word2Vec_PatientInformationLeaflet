package com.Configuration;

import org.slf4j.Logger;

public class GlobalPreprocessingConfiguration extends ConfigurationBase
{
    //region Fields

    private boolean runWikipediaPreprocessing;
    private boolean runMedPDFPreprocessing;
    private String medDataPreprocessingConfig;
    private String wikipediaPreprocessingConfig;

    //endregion

    //region Constructors

    public GlobalPreprocessingConfiguration(String localResourcePath, Logger log)
    {
        _log = log;
        try
        {
            initializeInternal(localResourcePath);
        }
        catch(Exception ex)
        {
            _log.error("Error on initialize. GlobalePreprocessingConfiguration.", ex);
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
                case "runWikipediaPreprocessing":
                    runWikipediaPreprocessing = Boolean.parseBoolean(rightHandSide);
                    break;
                case "wikipediaPreprocessingConfig":
                    wikipediaPreprocessingConfig = rightHandSide;
                    break;
                case "medDataPreprocessingConfig":
                    medDataPreprocessingConfig = rightHandSide;
                    break;
                case "runMedPDFPreprocessing":
                    runMedPDFPreprocessing = Boolean.parseBoolean(rightHandSide);
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

    public boolean getRunWikipediaPreprocessing()
    {
        return runWikipediaPreprocessing;
    }

    public String getMedDataPreprocessingConfig()
    {
        return medDataPreprocessingConfig;
    }

    public String getWikipediaPreprocessingConfig()
    {
        return wikipediaPreprocessingConfig;
    }

    public boolean getRunMedPDFPreprocessing()
    {
        return runMedPDFPreprocessing;
    }

    //endregion
}