package com.EEB.PatientInformationLeaflet.Root;

import com.EEB.PatientInformationLeaflet.Configuration.ProcessConfiguration;
import com.EEB.PatientInformationLeaflet.Word2Vec.ModelTrainingController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordEmbeddingsRoot
{
    private static final String _configFilePath = "configuration/model_config.mdc";

    private static Logger _log = LoggerFactory.getLogger(WordEmbeddingsRoot.class);

    public static void main( String[] args )
    {
        runSingleStageModelTraining();
    }

    private static void runSingleStageModelTraining()
    {
        ProcessConfiguration config = new ProcessConfiguration(_configFilePath, _log);
//        ModelTrainingController trainingController = new ModelTrainingController(config, _log);
//        trainingController.startTraining();
    }
}