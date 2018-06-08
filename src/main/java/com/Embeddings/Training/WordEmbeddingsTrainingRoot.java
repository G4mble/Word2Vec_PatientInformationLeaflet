package com.Embeddings.Training;

import com.Embeddings.Configuration.ProcessConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WordEmbeddingsTrainingRoot
{
    private static final String _configFilePath = "configuration/model_config.mdc";

    private static Logger _log = LoggerFactory.getLogger(WordEmbeddingsTrainingRoot.class);

    public static void main( String[] args )
    {
        ProcessConfiguration config = new ProcessConfiguration(_configFilePath, _log);
        if(config.getConfigureUptraining())
            runUptraining(config);
        else
            runSingleStageModelTraining(config);
    }

    private static void runUptraining(ProcessConfiguration config)
    {
        ModelTrainingController trainingController = new ModelTrainingController(config, _log);
        trainingController.beginUptraining();
        trainingController.saveModelToFile();
        trainingController.saveVocabularyToFile();
    }

    private static void runSingleStageModelTraining(ProcessConfiguration config)
    {
        ModelTrainingController trainingController = new ModelTrainingController(config, _log);
        trainingController.trainModel();
        trainingController.saveModelToFile();
        trainingController.saveVocabularyToFile();
    }
}