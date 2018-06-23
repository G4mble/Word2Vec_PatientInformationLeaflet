package com.Contracts;

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
}