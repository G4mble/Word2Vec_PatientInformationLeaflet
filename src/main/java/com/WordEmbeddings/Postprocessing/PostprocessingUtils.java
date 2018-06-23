package com.WordEmbeddings.Postprocessing;

import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;

import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public class PostprocessingUtils
{
    public static List<Pair<String, Float>> computeSimilarityInfo(Word2Vec model, String word)
    {
        VocabCache<VocabWord> vocab = model.vocab();
        int numWords = vocab.numWords();

        List<Pair<String, Float>> similarityInfo = new ArrayList<>();
        int wordIndex = model.indexOf(word);

        for(int i = 0; i < numWords; i++)
        {
            if(i == wordIndex)
                continue;

            String currentWord = vocab.wordAtIndex(i);
            float similarity = (float)model.similarity(word, currentWord);
            similarityInfo.add(new Pair<>(currentWord, similarity));
        }
        return similarityInfo;
    }
}
