package com.Embeddings.Training;

import com.Configuration.ModelTrainingConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WordEmbeddingsTrainingRoot
{
    private static final String MODEL_TRAINING_CONFIG_PATH = "configuration/model_config.cfg";

    private static Logger _log = LoggerFactory.getLogger(WordEmbeddingsTrainingRoot.class);

    public static void main( String[] args )
    {
        ModelTrainingConfiguration config = new ModelTrainingConfiguration(MODEL_TRAINING_CONFIG_PATH, _log);
        if(config.getConfigureUptraining())
            runUptraining(config);
        else
            runSingleStageModelTraining(config);
    }

    private static void runUptraining(ModelTrainingConfiguration config)
    {
        ModelTrainingController trainingController = new ModelTrainingController(config, _log);
        trainingController.beginUptraining();
        trainingController.saveModelToFile();
        trainingController.saveVocabularyToFile();
    }

    private static void runSingleStageModelTraining(ModelTrainingConfiguration config)
    {
        ModelTrainingController trainingController = new ModelTrainingController(config, _log);
        trainingController.trainModel();
        trainingController.saveModelToFile();
        trainingController.saveVocabularyToFile();
    }
}