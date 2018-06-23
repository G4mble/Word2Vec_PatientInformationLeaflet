package com.Configuration;

import org.slf4j.Logger;

public class ModelAccessConfiguration extends ConfigurationBase
{
    //region Fields

    private String inputSource;

    //endregion

    //region Constructors

    public ModelAccessConfiguration(String localResourcePath, Logger log)
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
                case "inputSource":
                    inputSource = rightHandSide;
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

    public String getInputSource()
    {
        return inputSource;
    }

    //endregion
}
