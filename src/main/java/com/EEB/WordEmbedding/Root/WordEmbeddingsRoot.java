package com.EEB.WordEmbedding.Root;

import com.EEB.WordEmbedding.Configuration.ProcessConfiguration;
import com.EEB.WordEmbedding.Word2Vec.ModelTrainingController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordEmbeddingsRoot
{
    private static final String _configFilePath = "configuration/model_config.mdc";

    private static Logger _log = LoggerFactory.getLogger(WordEmbeddingsRoot.class);

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
        //TODO TS eval
        trainingController.saveVocabularyToFile();
//
//        //TODO TS just some evaluation remove for final commit
//        Word2Vec model = trainingController.getWord2VecModel();
//        String ihkStem = GermanLanguageStemmer.stem("ihk");
//        String handelskammerStem = GermanLanguageStemmer.stem("handelskammer");
////        String ihkStem = "ihk";
////        String handelskammerStem = "handelskammer";
//        System.out.println(model.hasWord(ihkStem));
//        System.out.println(model.hasWord(handelskammerStem));
//        System.out.println(model.similarity(ihkStem, handelskammerStem));
//        System.out.println(model.similarWordsInVocabTo(ihkStem, .5d));
//        System.out.println(model.similarWordsInVocabTo(handelskammerStem, .5d));
//        System.out.println(model.wordsNearest(ihkStem, 10));
//        System.out.println(model.wordsNearest(handelskammerStem, 10));
    }
}