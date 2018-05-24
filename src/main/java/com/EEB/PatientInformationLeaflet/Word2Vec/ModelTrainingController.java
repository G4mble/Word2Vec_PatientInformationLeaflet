package com.EEB.PatientInformationLeaflet.Word2Vec;

import com.EEB.PatientInformationLeaflet.Configuration.ProcessConfiguration;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelTrainingController
{
    private final ProcessConfiguration _processConfig;
    private final DateFormat _dateFormat;
    private final Logger _log;

    private Word2Vec w2vModel;

    public ModelTrainingController(ProcessConfiguration config, Logger log)
    {
        _dateFormat = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
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

    public void trainModel()
    {
        _log.info("Building model...");
        w2vModel = initializeModel();
        _log.info("Start model training...");
        w2vModel.fit();
        _log.info("Model training complete.");
    }

    public boolean saveModelToFile()
    {
        try
        {
            _log.info("Saving model...");
            WordVectorSerializer.writeWord2VecModel(w2vModel, "model_output_" + _dateFormat.format(new Date()) + "_test.cmf");
            return true;
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in saveModelToFile", ex);
        }
        return false;
    }

    public boolean saveVocabularyToFile()
    {
        try
        {
            _log.info("Saving vocabulary...");
            FileUtils.writeLines(new File("vocabulary_" + _dateFormat.format(new Date()) + ".txt"), w2vModel.getVocab().words());
            return true;
        }
        catch (IOException ex)
        {
            _log.error("Unexpected error in saveVocabularyToFile", ex);
        }
        return false;
    }

    public Word2Vec getWord2VecModel()
    {
        return this.w2vModel;
    }
}