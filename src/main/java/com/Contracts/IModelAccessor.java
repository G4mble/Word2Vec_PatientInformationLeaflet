package com.Contracts;

import javafx.util.Pair;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IModelAccessor
{
    CompletableFuture<Boolean> initializeAsync();
    boolean hasWord(String word);
    double calculateSimilarity(String firstWord, String secondWord);
    List<String> findOrthographicallyCloseWordsTo(String word, double accuracy);
    Collection<String> findSemanticallySimilarWordsTo(String word,  int numberOfCloseWords);
    Collection<String> findSemanticallySimilarWordsTo(Collection<String> positiveWords, Collection<String> negativeWords,  int numberOfCloseWords);
    Collection<String> findSemanticallySimilarWordsToUsingVectorMean(Collection<String> words, int getTopXWords);
    Word2Vec getModel();
    Pair<List<String>, List<String>> getMostAndLeastSimilarWordsTo(String word, int topX, int botX);
}