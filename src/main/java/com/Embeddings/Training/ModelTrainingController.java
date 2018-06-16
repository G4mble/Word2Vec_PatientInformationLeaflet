package com.Embeddings.Training;

import com.Configuration.ModelTrainingConfiguration;
import com.Embeddings.Preprocessing.GermanTokenStemmingPreprocessor;
import com.Embeddings.Tokenizer.GermanNGramTokenizerFactory;
import org.apache.commons.io.FileUtils;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

class ModelTrainingController
{
    private final ModelTrainingConfiguration _processConfig;
    private final DateFormat _dateFormat;
    private final Logger _log;

    private Word2Vec w2vModel;

    ModelTrainingController(ModelTrainingConfiguration config, Logger log)
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
                    .workers(_processConfig.getWorkers())
                    .allowParallelTokenization(_processConfig.allowParallelTokenization())
                    .useHierarchicSoftmax(_processConfig.useHierarchicSoftmax())
                    .negativeSample(_processConfig.getNegativeSample())
                    .minWordFrequency(_processConfig.getMinWordFrequency())
                    .batchSize(_processConfig.getBatchSize())
                    .iterations(_processConfig.getIterations())
                    .epochs(_processConfig.getEpochs())
                    .layerSize(_processConfig.getLayerSize())
                    .seed(_processConfig.getSeed())
                    .windowSize(_processConfig.getWindowSize())
                    .iterate(_processConfig.getSentenceIterator())
                    .sampling(1e-5)
                    .learningRate(0.025)
                    .useAdaGrad(false)
                    .tokenizerFactory(_processConfig.getTokenizer())
                    .stopWords(_processConfig.getStopWords())
                    .build();
    }

    void trainModel()
    {
        _log.info("Building model...");
        w2vModel = initializeModel();
        _log.info("Start model training...");
        long startTime = System.currentTimeMillis();
        w2vModel.fit();
        _log.info("Model training complete.");
        _log.info("Elapsed time since start: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    private void uptrainModel(Path path)
    {
        try
        {
            SentenceIterator iterator = new BasicLineIterator(path.toFile());
            TokenizerFactory defaultTokenizerFactory = new DefaultTokenizerFactory();
            TokenizerFactory tokenizer = new GermanNGramTokenizerFactory(defaultTokenizerFactory, _processConfig.getNgramMin(), _processConfig.getNgramMax());
            tokenizer.setTokenPreProcessor(new GermanTokenStemmingPreprocessor());

            w2vModel.setTokenizerFactory(tokenizer);
            w2vModel.setSentenceIterator(iterator);

            _log.info("Performing uptraining...");
            w2vModel.fit();
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in uptrainModel", ex);
        }
    }

    void beginUptraining()
    {
        try (Stream<Path> paths = Files.walk(Paths.get(new ClassPathResource(_processConfig.getDataPath()).getFile().getAbsolutePath())))
        {
            w2vModel = WordVectorSerializer.readWord2VecModel(new ClassPathResource(_processConfig.getStartingModelPath()).getFile().getAbsolutePath());
            paths.filter(Files::isRegularFile).forEach(this::uptrainModel);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in beginUptraining", ex);
        }

    }

    boolean saveModelToFile()
    {
        try
        {
            _log.info("Saving model...");
            WordVectorSerializer.writeWord2VecModel(w2vModel, "model_output_" + _dateFormat.format(new Date()) + ".cmf");
            return true;
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in saveModelToFile", ex);
        }
        return false;
    }

    boolean saveVocabularyToFile()
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

    Word2Vec getWord2VecModel()
    {
        return this.w2vModel;
    }
}