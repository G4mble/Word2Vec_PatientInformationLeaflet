package com.WordEmbeddings.ModelAccess.Accessor;

import com.Configuration.ModelAccessConfiguration;
import com.Contracts.IModelAccessor;
import com.WordEmbeddings.Postprocessing.PostprocessingUtils;
import javafx.util.Pair;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class Word2VecModelAccessor implements IModelAccessor
{
    //region Fields

    private final ModelAccessConfiguration _config;
    private final Logger _log;
    private Word2Vec _model;

    //endregion

    //region Constructors

    public Word2VecModelAccessor(ModelAccessConfiguration config, Logger log)
    {
        _config = config;
        _log = log;
    }

    //endregion

    private void test(Set<String> filterTokens)
    {
        WeightLookupTable<VocabWord> inputLookupTable = _model.getLookupTable();
        VocabCache<VocabWord> vocab = _model.getVocab();
        Set<String> vocabWords = new HashSet<>();
        for(int i = 0; i < vocab.numWords(); i++)
        {
            vocabWords.add(vocab.wordAtIndex(i));
        }
        vocabWords.retainAll(filterTokens);
    }

    /**
     * As it turns out, this is exactly what the W2V library does...
     * So we currently do not profit from doing it manually as the library is probably more optimized
     *
     * KEEPING THIS FOR POSSIBLE FUTURE USE IN A DIFFERENT SETTING
     */
    private List<String> testWordsNearest(String word, int topX)
    {
        List<Pair<String, Float>> similarityInfo = PostprocessingUtils.computeSimilarityInfo(_model, word);
        similarityInfo.sort(Collections.reverseOrder((o1, o2) -> Float.compare(o1.getValue(), o2.getValue())));
        List<String> output = new ArrayList<>();
        for(int i = 0; i < topX; i++)
            output.add(similarityInfo.get(i).getKey());
        return output;
    }

    //region Interface Methods

    @Override
    public CompletableFuture<Boolean> initializeAsync()
    {
        try
        {
            File modelFile = new File(_config.getInputSource());
            _model = WordVectorSerializer.readWord2VecModel(modelFile);
            return completedFuture(true);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in intializeAsync.", ex);
            return completedFuture(false);
        }
    }

    /**
     * Determines whether the given word exists in the vocabulary.
     * @return TRUE: word is in vocabulary // FALSE: word is NOT in vocabulary
     */
    @Override
    public boolean hasWord(String word)
    {
        return _model.hasWord(word);
    }

    /**
     * Calculates cosine similarity of the given words
     * @return cosine similarity
     */
    @Override
    public double calculateSimilarity(String firstWord, String secondWord)
    {
        return _model.similarity(firstWord, secondWord);
    }

    /**
     * Searches the vocabulary for words that, orthographically, appear to be similar to the input.
     * Uses statistical similarities.
     * @param word The word to which orthographically similar words are to be found.
     * @param accuracy The minimum similarity percentage required for a word to be considered similar to the input.
     * @return A list of similar words fulfilling the accuracy requirement.
     */
    @Override
    public List<String> findOrthographicallyCloseWordsTo(String word, double accuracy)
    {
        return _model.similarWordsInVocabTo(word, accuracy);
    }

    /**
     * Searches the vocabulary for words that appear to have a similar meaning as the input word.
     * @param word The word to which semantically similar words are to be found.
     * @param getTopXWords The amount of words that should be returned at most.
     * @return A Collection of words similar to the input.
     */
    @Override
    public Collection<String> findSemanticallySimilarWordsTo(String word, int getTopXWords)
    {
        return _model.wordsNearest(word, getTopXWords);
    }

    /**
     * Searches the vocabulary for words that appear to have a similiar meaning as the word that results from the vector addition of negativeWords + positiveWords.
     * @param getTopXWords The amount of words that should be returned at most.
     * @return A Collection of words similar to the word that results from the vector addition of negativeWords + positiveWords.
     */
    @Override
    public Collection<String> findSemanticallySimilarWordsTo(Collection<String> positiveWords, Collection<String> negativeWords, int getTopXWords)
    {
        return _model.wordsNearest(positiveWords, negativeWords, getTopXWords);
    }

    //endregion
}