package com.EEB.PatientInformationLeaflet.Configuration;

import com.EEB.PatientInformationLeaflet.Preprocessing.GermanTokenStemmingPreprocessor;
import com.EEB.Tokenizer.GermanNGramTokenizerFactory;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProcessConfiguration
{

    //<editor-fold desc="Private Fields">

    private int minWordFrequency;
    private int iterations;
    private int epochs;
    private int layerSize;
    private int windowSize;
    private int negativeSample;

    private long seed;

    private boolean useHierarchicSoftmax;
    private boolean allowParallelTokenization;

    private List<String> stopWords;

    private SentenceIterator iterator;
    private TokenizerFactory tokenizer;

    private int _ngramMin;
    private int _ngramMax;
    private String _iteratorSource;
    private String _dataPath;
    private String _stopWordFilePath;
    private final String _configFilePath;
    private final Logger _log;

    //</editor-fold>

    //<editor-fold desc="Constructors">

    public ProcessConfiguration(String configFilePath, Logger log)
    {
        _configFilePath = configFilePath;
        _log = log;

        initializeInternal();
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private boolean processLineContent(String line)
    {
        String[] elements = line.split("=");
        if (elements.length > 2)
            return false;

        try
        {
            switch (elements[0])
            {
                case "minWordFrequency":
                    minWordFrequency = Integer.parseInt(elements[1]);
                    break;
                case "iterations":
                    iterations = Integer.parseInt(elements[1]);
                    break;
                case "epochs":
                    epochs = Integer.parseInt(elements[1]);
                case "layerSize":
                    layerSize = Integer.parseInt(elements[1]);
                    break;
                case "windowSize":
                    windowSize = Integer.parseInt(elements[1]);
                    break;
                case "negativeSample":
                    negativeSample = Integer.parseInt(elements[1]);
                    break;
                case "seed":
                    seed = Long.parseLong(elements[1]);
                    break;
                case "useHierarchicSoftmax":
                    useHierarchicSoftmax = Boolean.parseBoolean(elements[1]);
                    break;
                case "allowParallelTokenization":
                    allowParallelTokenization = Boolean.parseBoolean(elements[1]);
                    break;
                case "iteratorSource":
                    _iteratorSource = elements[1];
                    break;
                case "dataPath":
                    _dataPath = elements[1];
                    break;
                case "stopWordsPath":
                    _stopWordFilePath = elements[1];
                    break;
                case "ngramMin":
                    _ngramMin = Integer.parseInt(elements[1]);
                    break;
                case "ngramMax":
                    _ngramMax = Integer.parseInt(elements[1]);
                    break;
                default:
                    return false;
            }
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }

    private void initializeWord2VecComponents()
    {
        //initialize dataset and iterator
        try
        {
            File dataset = new File (new ClassPathResource(_dataPath).getFile().getAbsolutePath());
            switch(_iteratorSource)
            {
                case "singleFile":
                    iterator = new BasicLineIterator(dataset);
                    break;
                case "directory":
                    iterator = new FileSentenceIterator(dataset);
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
        tokenizer = new GermanNGramTokenizerFactory(defaultTokenizerFactory, _ngramMin, _ngramMax);
        tokenizer.setTokenPreProcessor(new GermanTokenStemmingPreprocessor());
    }

    private void loadConfigurationFromFile(String filePath)
    {
        try (Stream<String> lineStream = Files.lines(Paths.get(new ClassPathResource(filePath).getFile().getAbsolutePath())))
        {
            for (String line : (Iterable<String>) lineStream::iterator)
            {
                if (!processLineContent(line))
                {
                    _log.error("Invalid configuration detected.");
                    return;
                }
            }
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in loadConfigurationFromFile", ex);
        }
    }

    private void loadStopWordsFromFile(String filePath)
    {
        stopWords = new ArrayList<>();
        try (Stream<String> lineStream = Files.lines(Paths.get(new ClassPathResource(filePath).getFile().getAbsolutePath())))
        {
            for (String line : (Iterable<String>) lineStream::iterator)
            {
                stopWords.add(line);
            }
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in loadStopWordsFromFile", ex);
        }
    }

    private void initializeInternal()
    {
        loadConfigurationFromFile(_configFilePath);
        loadStopWordsFromFile(_stopWordFilePath);
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

    public SentenceIterator getIterator()
    {
        return iterator;
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

    //</editor-fold>
}