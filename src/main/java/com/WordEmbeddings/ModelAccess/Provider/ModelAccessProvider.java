package com.WordEmbeddings.ModelAccess.Provider;

import com.Configuration.CommonPreprocessingConfiguration;
import com.Configuration.GlobalPreprocessingConfiguration;
import com.Configuration.MedDataPreprocessingConfiguration;
import com.Configuration.ModelAccessConfiguration;
import com.Contracts.IModelAccessor;
import com.Contracts.IPreprocessingUtility;
import com.TextPreprocessing.PreprocessingCore.CommonPreprocessingUtils;
import com.Utility.Helper.FileHelper;
import com.Utility.Helper.PreprocessingHelper;
import com.WordEmbeddings.ModelAccess.Accessor.Word2VecModelAccessor;

import static com.ea.async.Async.await;
import static java.util.concurrent.CompletableFuture.completedFuture;

import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ModelAccessProvider
{
    //region Fields

    private final String NON_PRESCRIPTION_MED_PATH = "data/non_prespricption_meds.txt";
    private final String GLOBAL_PREPROCESSING_CONFIG_PATH = "configuration/globalPreprocessing_config.cfg";

    private final ModelAccessConfiguration _config;
    private final Logger _log;
    private IPreprocessingUtility _preprocessingUtils;
    private IModelAccessor _modelAccessor;
    private Set<String> _nonPrescriptionMeds;

    //endregion

    //region Constructors

    private ModelAccessProvider(ModelAccessConfiguration config, Logger log)
    {
        _config = config;
        _log = log;
    }

    //endregion

    //region Public Methods

    //region General Methods

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

    public Collection<String> findSemanticallySimilarWordsToUsingVectorMean(Collection<String> words, int getTopXWords)
    {
        return _modelAccessor.findSemanticallySimilarWordsToUsingVectorMean(words, getTopXWords);
    }

    //endregion

    //region Med-Specific Methods

    /**
     * NULL CHECK ON RETURN VALUE IS ADVISED
     * Looks up similar words to the input, and filters the output using a list of preprocessed medicaments
     * @param word The input word
     * @param maxNumberOfOutputWords How many words will be returned by Word2Vec. Does NOT necessarily correspond to
     *                               the number of list entries that are returned as the Word2Vec output is filtered first.
     * @return NULL if the input word is not in the vocabulary // List of strings of close words to the input
     */
    public Collection<String> getSimilarMedicaments(String word, int maxNumberOfOutputWords)
    {
        //preprocess "word" --> get list of components
        List<String> inputSplit = PreprocessingHelper.preprocessElementToList(word, _preprocessingUtils);
        if(inputSplit == null)
            return null;
        List<String> duplicateSplit = new ArrayList<>(inputSplit);

        duplicateSplit.retainAll(_nonPrescriptionMeds);

        //if there are no elements left, continue with inital list // otherwise remove all remaining elements from inital list
        if(duplicateSplit.size() == 0)
            duplicateSplit = new ArrayList<>(inputSplit);

        //yes this possible self-ref is intentional and required in the following
        inputSplit.removeAll(duplicateSplit);

        duplicateSplit.addAll(inputSplit);

        int index = 0;
        int maxIndex = duplicateSplit.size();
        Collection<String> w2vSimList = new ArrayList<>();
        do
        {
            String currentWord = duplicateSplit.get(index);
            index++;
            if(!hasWord(currentWord))
                continue;

            w2vSimList = findSemanticallySimilarWordsTo(currentWord, maxNumberOfOutputWords);
            w2vSimList.retainAll(_nonPrescriptionMeds);
        }while(w2vSimList.size() == 0 && index < maxIndex);

        if(w2vSimList.size() == 0)
            w2vSimList = null;

        return w2vSimList;
    }

    //endregion

    //endregion

    //region Initialization

    private CompletableFuture<Boolean> initializeInternalAsync()
    {
        try
        {
            _modelAccessor = new Word2VecModelAccessor(_config, _log);
            if(!await(_modelAccessor.initializeAsync()))
                return completedFuture(false);

            _nonPrescriptionMeds = FileHelper.loadDocumentLinesToSetFromLocalResource(NON_PRESCRIPTION_MED_PATH, StandardCharsets.UTF_8);

            GlobalPreprocessingConfiguration globalConfig = new GlobalPreprocessingConfiguration(GLOBAL_PREPROCESSING_CONFIG_PATH, _log);
            CommonPreprocessingConfiguration preprocessingConfig = new MedDataPreprocessingConfiguration(globalConfig.getMedDataPreprocessingConfig(), _log);
            _preprocessingUtils = new CommonPreprocessingUtils(preprocessingConfig, _log);
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in initializeInternalAsync.", ex);
            return completedFuture(false);
        }

        return completedFuture(true);
    }

    public static CompletableFuture<ModelAccessProvider> getNewInstanceAsync(ModelAccessConfiguration config, Logger log)
    {
        ModelAccessProvider newInstance = new ModelAccessProvider(config, log);
        return await(newInstance.initializeInternalAsync()) ? completedFuture(newInstance) : completedFuture(null);
    }

    //endregion
}