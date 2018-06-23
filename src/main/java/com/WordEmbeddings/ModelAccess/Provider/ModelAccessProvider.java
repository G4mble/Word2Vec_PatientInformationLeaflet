package com.WordEmbeddings.ModelAccess.Provider;

import com.Configuration.ModelAccessConfiguration;
import com.Contracts.IModelAccessor;
import com.WordEmbeddings.ModelAccess.Accessor.Word2VecModelAccessor;

import static com.ea.async.Async.await;
import static java.util.concurrent.CompletableFuture.completedFuture;

import org.slf4j.Logger;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModelAccessProvider
{
    //region Fields

    private final ModelAccessConfiguration _config;
    private final Logger _log;
    private IModelAccessor _modelAccessor;

    //endregion

    //region Constructors

    private ModelAccessProvider(ModelAccessConfiguration config, Logger log)
    {
        _config = config;
        _log = log;
    }

    //endregion

    //region Public Methods

    public boolean hasWord(String word)
    {
        return _modelAccessor.hasWord(word);
    }

    public double calculateSimilarity(String firstWord, String secondWord)
    {
        return _modelAccessor.calculateSimilarity(firstWord, secondWord);
    }

    public List<String> findOrthographicallyCloseWordsTo(String word, double accuracy)
    {
        return _modelAccessor.findOrthographicallyCloseWordsTo(word, accuracy);
    }

    public Collection<String> findSemanticallySimilarWordsTo(String word, int numberOfCloseWords)
    {
        return _modelAccessor.findSemanticallySimilarWordsTo(word, numberOfCloseWords);
    }

    public Collection<String> findSemanticallySimilarWordsTo(Collection<String> positiveWords, Collection<String> negativeWords, int numberOfCloseWords)
    {
        return _modelAccessor.findSemanticallySimilarWordsTo(positiveWords, negativeWords, numberOfCloseWords);
    }

    //endregion

    //region Initialization

    private CompletableFuture<Boolean> initializeInternalAsync()
    {
        _modelAccessor = new Word2VecModelAccessor(_config, _log);
        return completedFuture(await(_modelAccessor.initializeAsync()));
    }

    public static CompletableFuture<ModelAccessProvider> getNewInstanceAsync(ModelAccessConfiguration config, Logger log)
    {
        ModelAccessProvider newInstance = new ModelAccessProvider(config, log);
        return await(newInstance.initializeInternalAsync()) ? completedFuture(newInstance) : completedFuture(null);
    }

    //endregion
}