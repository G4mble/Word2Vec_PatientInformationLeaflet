package com.EEB.PatientInformationLeaflet.Word2Vec;

import com.EEB.PatientInformationLeaflet.Configuration.ProcessConfiguration;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;

public class ModelTrainingController
{
    private final ProcessConfiguration _processConfig;
    private final Logger _log;

    private Word2Vec w2vModel;

    public ModelTrainingController(ProcessConfiguration config, Logger log)
    {
        _processConfig = config;
        _log = log;
    }

    private Word2Vec initializeModel()
    {
        //TODO TS evaluate current && further parameters
        _log.info("Configuring input parameters...");
        return new Word2Vec.Builder()
                .allowParallelTokenization(_processConfig.allowParallelTokenization())
                .useHierarchicSoftmax(_processConfig.useHierarchicSoftmax())
                .negativeSample(_processConfig.getNegativeSample())
                .minWordFrequency(_processConfig.getMinWordFrequency())
                .iterations(_processConfig.getIterations())
                .epochs(_processConfig.getEpochs())
                .layerSize(_processConfig.getLayerSize())
                .seed(_processConfig.getSeed())
                .windowSize(_processConfig.getWindowSize())
                .iterate(_processConfig.getIterator())
                .tokenizerFactory(_processConfig.getTokenizer())
                .stopWords(_processConfig.getStopWords())
                .build();
    }

    public void startTraining()
    {
        _log.info("Building model...");
        w2vModel = initializeModel();
        _log.info("Start model training...");
        w2vModel.fit();
        _log.info("Model training complete.");
    }
}