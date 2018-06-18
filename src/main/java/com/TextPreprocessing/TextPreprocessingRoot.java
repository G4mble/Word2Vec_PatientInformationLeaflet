package com.TextPreprocessing;

import com.Configuration.CommonPreprocessingConfiguration;
import com.Configuration.GlobalPreprocessingConfiguration;
import com.Configuration.MedDataPreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.Contracts.ITextPreprocessor;
import com.TextPreprocessing.DataSourcePreprocessor.MedDataPreprocessor;
import com.TextPreprocessing.DataSourcePreprocessor.MedPDFPreprocessor;
import com.TextPreprocessing.DataSourcePreprocessor.WikipediaPreprocessor;
import com.TextPreprocessing.PreprocessingCore.CommonPreprocessingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextPreprocessingRoot
{
    private static Logger _log = LoggerFactory.getLogger(TextPreprocessingRoot.class);

    private static final String GLOBAL_PREPROCESSING_CONFIG_PATH = "configuration/globalPreprocessing_config.cfg";

    public static void main(String[] args)
    {
        GlobalPreprocessingConfiguration globalConfig = new GlobalPreprocessingConfiguration(GLOBAL_PREPROCESSING_CONFIG_PATH,_log);
        ITextPreprocessor preprocessor;
        CommonPreprocessingConfiguration config;

        if(globalConfig.getRunWikipediaPreprocessing())
        {
            config = new CommonPreprocessingConfiguration(_log);
            config.initialize(globalConfig.getWikipediaPreprocessingConfig());
            preprocessor = new WikipediaPreprocessor(config, _log);
        }
        else
        {
            config = new MedDataPreprocessingConfiguration(globalConfig.getMedDataPreprocessingConfig(), _log);
            preprocessor = new MedDataPreprocessor((MedDataPreprocessingConfiguration)config, _log);
        }

//        {
//            config = new MedDataPreprocessingConfiguration(globalConfig.getMedDataPreprocessingConfig(), _log);
//            preprocessor = new MedPDFPreprocessor((MedDataPreprocessingConfiguration)config, _log);
//        }

        IPreprocessingUtility preprocessingUtils = new CommonPreprocessingUtils(config, _log);
        preprocessor.runProcess(preprocessingUtils);
    }
}