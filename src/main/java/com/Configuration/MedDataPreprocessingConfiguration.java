package com.Configuration;

import org.slf4j.Logger;

public class MedDataPreprocessingConfiguration extends CommonPreprocessingConfiguration
{
    //region Fields

    private boolean configureOneMedPerLine;
    private boolean keepCategoryNamePerLine;
    private boolean keepMedNamePerLine;

    //endregion

    //region Constructors

    public MedDataPreprocessingConfiguration(String localResourceConfigFilePath, Logger log)
    {
        super(log);
        try
        {
            initializeInternal(localResourceConfigFilePath);
        }
        catch (Exception ex)
        {
            _log.error("Error on initialize: MedDataPreprocessingConfiguration.", ex);
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
                case "configureOneMedPerLine":
                    configureOneMedPerLine = Boolean.parseBoolean(rightHandSide);
                    break;
                case "keepMedNamePerLine":
                    keepMedNamePerLine = Boolean.parseBoolean(rightHandSide);
                    break;
                case "keepCategoryNamePerLine":
                    keepCategoryNamePerLine = Boolean.parseBoolean(rightHandSide);
                    break;
                default:
                    return super.processLineContent(line);
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

    public boolean getConfigureOneMedPerLine()
    {
        return configureOneMedPerLine;
    }

    public boolean getKeepCategoryNamePerLine()
    {
        return keepCategoryNamePerLine;
    }

    public boolean getKeepMedNamePerLine()
    {
        return keepMedNamePerLine;
    }

    //endregion
}
