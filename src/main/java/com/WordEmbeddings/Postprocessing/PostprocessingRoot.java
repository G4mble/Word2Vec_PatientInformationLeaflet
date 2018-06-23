package com.WordEmbeddings.Postprocessing;

import org.apache.commons.io.FileUtils;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PostprocessingRoot
{
    private static final Logger _log = LoggerFactory.getLogger(PostprocessingRoot.class);

    //TODO change filename --> you can put your models in the "reources/postprocessing/model_input" folder
    private static final String _filename = "postprocessing/model_input/M_003_model_output_2018-05-31_02-18-43.cmf";

    public static void main(String[] args)
    {
        _log.info("Running postprocessing...");

        File modelFile;
        try
        {
            modelFile = new File(new ClassPathResource(_filename).getFile().getAbsolutePath());
        }
        catch (FileNotFoundException ex)
        {
            _log.error("Model file does not exist at: " + _filename, ex);
            _log.info("Terminating...");
            return;
        }
        _log.info("Attempting to load Training Model from file: \"" + _filename + "\" ...");
        Word2Vec model = WordVectorSerializer.readWord2VecModel(modelFile);
        _log.info("Loading complete.");

        Map<String, float[]> similarityMap = computeSimilarityMatrix(model);
        saveSimilarityMatrix(similarityMap);
    }

    private static String floatToOutputRepresentation(float[] input)
    {
        StringBuilder builder = new StringBuilder();
        int itemCount = input.length;
        int currentIndex = 1;
        builder.append("{");
        for(float current : input)
        {
            builder.append(current);
            if(currentIndex < itemCount)
                builder.append(";");
            currentIndex++;
        }
        builder.append("}");
        return builder.toString();
    }

    private static void saveSimilarityMatrix(Map<String, float[]> similarityMap)
    {
        _log.info("Composing output matrix...");
        Set<String> keySet = similarityMap.keySet();
        int dimensions = keySet.size();
        int count = 1;

        StringBuilder builder = new StringBuilder();
        for(String key : keySet)
        {
            builder.append("[");
            builder.append(key).append(":");
            builder.append(floatToOutputRepresentation(similarityMap.get(key)));
            builder.append("]");
            if(count < dimensions)
                builder.append("\n");
            count++;
        }

        try
        {
            _log.info("Saving matrix...");
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
            FileUtils.write(new File("wordSimilarityMatrix_" + dateFormat.format(new Date()) + ".mat"), builder.toString());
        }
        catch (IOException ex)
        {
            _log.error("Unexpected error in saveSimilarityMatrix.", ex);
        }
    }

    private static Map<String, float[]> computeSimilarityMatrix(Word2Vec model)
    {
        Map<String, float[]> similarityMap = new HashMap<>();

        _log.info("Computing similarity matrix...");
        VocabCache<VocabWord> vocab = model.vocab();
//        int numWords = vocab.numWords();
        int numWords = 500;

        _log.info("Number of words: " + numWords);

        for(int i = 0; i < numWords; i++)
        {
            _log.info("d1: " + i);
            String currentWord = vocab.wordAtIndex(i);
            float[] currentSimilarityVector = new float[numWords];
            for(int j = 0; j < numWords; j++)
            {
                if(i == j)
                    continue;

                currentSimilarityVector[j] = (float)model.similarity(currentWord, vocab.wordAtIndex(j));
            }
            similarityMap.put(currentWord, currentSimilarityVector);
        }
        return similarityMap;
    }
}