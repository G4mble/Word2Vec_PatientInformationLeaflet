package com.EEB.Postprocessing;

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
import java.util.Date;

public class PostprocessingRoot
{
    private static Logger _log = LoggerFactory.getLogger(PostprocessingRoot.class);

    //TODO change filename --> you can put your models in the "reources/postprocessing/model_input" folder
    private static final String _filename = "postprocessing/model_input/model_output_2018-05-26_10-45-06.cmf";

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
        _log.info("Attempting to load Word2Vec Model from file: \"" + _filename + "\" ...");
        Word2Vec model = WordVectorSerializer.readWord2VecModel(modelFile);
        _log.info("Loading complete.");

        double[][] similarityMatrix = computeSimilarityMatrix(model);
        saveSimilarityMatrix(similarityMatrix);
    }

    private static void saveSimilarityMatrix(double[][] matrix)
    {
        _log.info("Composing output matrix...");
        int d1 = matrix.length;
        int d2 = matrix[0].length;

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < d1; i++)
        {
            builder.append("{ ");
            for(int j = 0; j < d2; j++)
            {
                builder.append(matrix[i][j]);
                if(j < (d2 - 1))
                    builder.append(", ");
            }
            builder.append(" }");
            if(i < (d1 - 1))
                builder.append("\n");
        }

        try
        {
            _log.info("Saving model...");
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
            FileUtils.write(new File("wordSimilarityMatrix_" + dateFormat.format(new Date()) + ".mat"), builder.toString());
        }
        catch (IOException ex)
        {
            _log.error("Unexpected error in saveSimilarityMatrix.", ex);
        }
    }

    private static double[][] computeSimilarityMatrix(Word2Vec model)
    {
        _log.info("Computing similarity matrix...");
        VocabCache<VocabWord> vocab = model.vocab();
        int numWords = vocab.numWords();

        _log.info("Number of words: " + numWords);

        double[][] wordSimilarityMatrix = new double[numWords][numWords];

        for(int i = 0; i < numWords; i++)
        {
            _log.info("d1: " + i);
            String firstWord = vocab.wordAtIndex(i);
            for(int j = 0; j < numWords; j++)
            {
                if(i == j)
                    continue;

                wordSimilarityMatrix[i][j] = model.similarity(firstWord, vocab.wordAtIndex(j));
            }
        }

        return wordSimilarityMatrix;
    }
}