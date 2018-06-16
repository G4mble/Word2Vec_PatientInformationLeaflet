package com.Configuration;

import com.Embeddings.Preprocessing.GermanTokenStemmingPreprocessor;
import com.Embeddings.Tokenizer.GermanNGramTokenizerFactory;
import com.Utility.Helper.FileHelper;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ModelTrainingConfiguration extends ConfigurationBase
{

    //<editor-fold desc="Private Fields">

    private int minWordFrequency;
    private int iterations;
    private int epochs;
    private int layerSize;
    private int windowSize;
    private int negativeSample;
    private int workers;
    private int ngramMin;
    private int ngramMax;
    private int batchSize;

    private long seed;

    private boolean useHierarchicSoftmax;
    private boolean allowParallelTokenization;
    private boolean configureUptraining;

    private String dataPath;
    private String startingModelPath;

    private List<String> stopWords;

    private SentenceIterator sentenceIterator;
    private TokenizerFactory tokenizer;

    private String _iteratorSource;
    private String _stopWordFilePath;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    public ModelTrainingConfiguration(String localResourceConfigFilePath, Logger log)
    {
        _log = log;

        try
        {
            initializeInternal(localResourceConfigFilePath);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

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
            switch (leftHandSide)
            {
                case "minWordFrequency":
                    minWordFrequency = Integer.parseInt(rightHandSide);
                    break;
                case "iterations":
                    iterations = Integer.parseInt(rightHandSide);
                    break;
                case "epochs":
                    epochs = Integer.parseInt(rightHandSide);
                    break;
                case "layerSize":
                    layerSize = Integer.parseInt(rightHandSide);
                    break;
                case "windowSize":
                    windowSize = Integer.parseInt(rightHandSide);
                    break;
                case "negativeSample":
                    negativeSample = Integer.parseInt(rightHandSide);
                    break;
                case "seed":
                    seed = Long.parseLong(rightHandSide);
                    break;
                case "useHierarchicSoftmax":
                    useHierarchicSoftmax = Boolean.parseBoolean(rightHandSide);
                    break;
                case "allowParallelTokenization":
                    allowParallelTokenization = Boolean.parseBoolean(rightHandSide);
                    break;
                case "iteratorSource":
                    _iteratorSource = rightHandSide;
                    break;
                case "dataPath":
                    dataPath = rightHandSide;
                    break;
                case "stopWordsPath":
                    _stopWordFilePath = rightHandSide;
                    break;
                case "ngramMin":
                    ngramMin = Integer.parseInt(rightHandSide);
                    break;
                case "ngramMax":
                    ngramMax = Integer.parseInt(rightHandSide);
                    break;
                case "workers":
                    workers = Integer.parseInt(rightHandSide);
                    break;
                case "startingModelPath":
                    startingModelPath = rightHandSide;
                    break;
                case "configureUptraining":
                    configureUptraining = Boolean.parseBoolean(rightHandSide);
                    break;
                case "batchSize":
                    batchSize = Integer.parseInt(rightHandSide);
                    break;
                default:
                    return false;
            }
            return true;
        }
        catch (NumberFormatException ex)
        {
            _log.error("Unexpected error in processLineContent", ex);
            return false;
        }
    }

    private void initializeWord2VecComponents()
    {
        //initialize dataset and sentenceIterator
        try
        {
            File dataset = new File(new ClassPathResource(dataPath).getFile().getAbsolutePath());
            switch (_iteratorSource)
            {
                case "singleFile":
                    sentenceIterator = new BasicLineIterator(dataset);
                    break;
                case "directory":
                    sentenceIterator = new FileSentenceIterator(dataset);
                    break;
                default:
                    _log.error("Invalid iteratorSource-Type in config file!");
                    return;
            }
        }
        catch (FileNotFoundException ex)
        {
            _log.error("Unexpected Error in initializeWord2VecComponents", ex);
        }

        //initialize tokenizer
        TokenizerFactory defaultTokenizerFactory = new DefaultTokenizerFactory();
        tokenizer = new GermanNGramTokenizerFactory(defaultTokenizerFactory, ngramMin, ngramMax);
        tokenizer.setTokenPreProcessor(new GermanTokenStemmingPreprocessor());
    }

    private void loadStopWordsFromFile(String filePath)
    {
        try
        {
            stopWords = FileHelper.loadDocumentLinesToListFromLocalResource(filePath, StandardCharsets.UTF_8);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected void initializeInternal(String configFilePath) throws IOException
    {
        super.initializeInternal(configFilePath);

        loadStopWordsFromFile(_stopWordFilePath);
        if(!configureUptraining)
            initializeWord2VecComponents();
    }

    //</editor-fold>

    //<editor-fold desc="Getter/Setter">

    public int getMinWordFrequency()
    {
        return minWordFrequency;
    }

    public int getIterations()
    {
        return iterations;
    }

    public int getEpochs()
    {
        return epochs;
    }

    public int getLayerSize()
    {
        return layerSize;
    }

    public int getWindowSize()
    {
        return windowSize;
    }

    public int getNegativeSample()
    {
        return negativeSample;
    }

    public long getSeed()
    {
        return seed;
    }

    public boolean useHierarchicSoftmax()
    {
        return useHierarchicSoftmax;
    }

    public String getDataPath()
    {
        return dataPath;
    }

    public SentenceIterator getSentenceIterator()
    {
        return sentenceIterator;
    }

    public TokenizerFactory getTokenizer()
    {
        return tokenizer;
    }

    public List<String> getStopWords()
    {
        return stopWords;
    }

    public boolean allowParallelTokenization()
    {
        return allowParallelTokenization;
    }

    public int getWorkers()
    {
        return workers;
    }

    public String getStartingModelPath()
    {
        return startingModelPath;
    }

    public int getNgramMin()
    {
        return ngramMin;
    }

    public int getNgramMax()
    {
        return ngramMax;
    }

    public boolean getConfigureUptraining()
    {
        return configureUptraining;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    //</editor-fold>
}