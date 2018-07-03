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

import javafx.util.Pair;
import org.deeplearning4j.models.word2vec.Word2Vec;
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
        word = PreprocessingHelper.preprocessSingleElement(word, _preprocessingUtils);
        return _modelAccessor.hasWord(word);
    }

    /***
     * @return if one of the words does not exist: 0.0d --> their respective cosine similarity otherwise
     */
    public double calculateSimilarity(String firstWord, String secondWord)
    {
        firstWord = PreprocessingHelper.preprocessSingleElement(firstWord, _preprocessingUtils);
        secondWord = PreprocessingHelper.preprocessSingleElement(secondWord, _preprocessingUtils);
        if(!hasWordCore(firstWord) || !hasWordCore(secondWord))
            return 0.0d;
        return calculateSimilarityCore(firstWord, secondWord);
    }

    public List<String> findOrthographicallyCloseWordsTo(String word, double accuracy)
    {
        word = PreprocessingHelper.preprocessSingleElement(word, _preprocessingUtils);
        if(!hasWordCore(word))
            return null;
        return findOrthographicallyCloseWordsCore(word, accuracy);
    }

    public Collection<String> findSemanticallySimilarWordsTo(String word, int numberOfCloseWords)
    {
        word = PreprocessingHelper.preprocessSingleElement(word, _preprocessingUtils);
        if(!hasWordCore(word))
            return null;
        return findSemanticallySimilarWordsCore(word, numberOfCloseWords);
    }

    public Collection<String> findSemanticallySimilarWordsTo(Collection<String> positiveWords, Collection<String> negativeWords, int numberOfCloseWords)
    {
        positiveWords = PreprocessingHelper.preprocessElementsFromCollection(positiveWords, _preprocessingUtils);
        if(positiveWords == null) return null;
        for(String word:positiveWords)
            if(!hasWordCore(word))
                return null;

        negativeWords = PreprocessingHelper.preprocessElementsFromCollection(negativeWords, _preprocessingUtils);
        if(negativeWords == null) return null;
        for(String word:negativeWords)
            if(!hasWordCore(word))
                return null;

        return findSemanticallySimilarWordsCore(positiveWords, negativeWords, numberOfCloseWords);
    }

    public Collection<String> findSemanticallySimilarWordsUsingVectorMean(Collection<String> words, int getTopXWords)
    {
        words = PreprocessingHelper.preprocessElementsFromCollection(words, _preprocessingUtils);
        if(words == null) return null;
        for(String word:words)
            if(!hasWordCore(word))
                return null;
        return findSemanticallySimilarWordsUsingVectorMeanCore(words, getTopXWords);
    }

    //endregion

    //region Med-Specific Methods

    /***
     *  THIS IS FOR TEST PURPOSES -- DO NOT USE
     */
    private Collection<String> getSimilarMedicamentsWithSimilarityBound(String word, float similarityLowerBound)
    {
        List<String> inputSplit = PreprocessingHelper.preprocessElementFromStringToList(word, _preprocessingUtils);
        if(inputSplit == null)
            return null;

        List<String> duplicateList = new ArrayList<>(inputSplit);
        duplicateList.retainAll(_nonPrescriptionMeds);

        if(duplicateList.size() == 0)
            duplicateList = new ArrayList<>(inputSplit);

        inputSplit.removeAll(duplicateList);
        duplicateList.addAll(inputSplit);

        int count = 0;
        int max = duplicateList.size();
        Collection<String> w2vSimList = new ArrayList<>();
        do
        {
            String currentWord = duplicateList.get(count);
            count++;
            if(!_modelAccessor.hasWord(currentWord))
                continue;

            w2vSimList = _modelAccessor.getWordsNearestWithSimilarityThreshold(currentWord, similarityLowerBound, 500);
            w2vSimList.retainAll(_nonPrescriptionMeds);
        }while(w2vSimList.size() == 0 && count < max);

        return w2vSimList.size() > 0 ? w2vSimList : null;
    }

    /***
     * NULL CHECK ON RETURN VALUE IS ADVISED
     * Takes a SINGLE word and looks for matching medicaments until the amount specified is reached
     * @return NULL if an error occured or the input word is not in the vocabulary
     */
    public Collection<String> getSimilarMedicamentsToNonMedInput(String nonMedWord, int exactNumberOfOutputWords)
    {
        String[] split = nonMedWord.split(" ");
        nonMedWord = split[0];
        nonMedWord = PreprocessingHelper.preprocessSingleElement(nonMedWord, _preprocessingUtils);
        if(nonMedWord.length() < 3 || !hasWordCore(nonMedWord))
            return null;

        int stepSize = 20;
        // subtract stepSize once to account for addition in the loop
        int internalCount = exactNumberOfOutputWords - stepSize;

        Collection<String> w2vSimList;
        do
        {
            internalCount += stepSize;
            w2vSimList = findSemanticallySimilarWordsCore(nonMedWord, internalCount);
            w2vSimList.retainAll(_nonPrescriptionMeds);
        } while (w2vSimList.size() < exactNumberOfOutputWords);

        return w2vSimList.size() > 0 ? w2vSimList : null;
    }

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
        List<String> inputSplit = PreprocessingHelper.preprocessElementFromStringToList(word, _preprocessingUtils);
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
            if(!hasWordCore(currentWord))
                continue;

            w2vSimList = findSemanticallySimilarWordsCore(currentWord, maxNumberOfOutputWords);
            w2vSimList.retainAll(_nonPrescriptionMeds);
        }while(w2vSimList.size() == 0 && index < maxIndex);

        return w2vSimList.size() > 0 ? w2vSimList : null;
    }

    public Pair<List<String>, List<String>> getMostAndLeastSimilarWordsTo(String word, int topX, int botX)
    {
        return getMostAndLeastSimilarWordsCore(word, topX, botX);
    }

    //endregion

    //endregion

    //region Private Methods

    private Collection<String> findSemanticallySimilarWordsUsingVectorMeanCore(Collection<String> words, int getTopXWords)
    {
        return _modelAccessor.findSemanticallySimilarWordsToUsingVectorMean(words, getTopXWords);
    }

    private Collection<String> findSemanticallySimilarWordsCore(Collection<String> positiveWords, Collection<String> negativeWords, int numberOfCloseWords)
    {
        return _modelAccessor.findSemanticallySimilarWordsTo(positiveWords, negativeWords, numberOfCloseWords);
    }

    private List<String> findOrthographicallyCloseWordsCore(String word, double accuracy)
    {
        return _modelAccessor.findOrthographicallyCloseWordsTo(word, accuracy);
    }

    private boolean hasWordCore(String word)
    {
        return _modelAccessor.hasWord(word);
    }

    private double calculateSimilarityCore(String firstWord, String secondWord)
    {
        return _modelAccessor.calculateSimilarity(firstWord, secondWord);
    }

    private Collection<String> findSemanticallySimilarWordsCore(String word, int numberOfCloseWords)
    {
        return _modelAccessor.findSemanticallySimilarWordsTo(word, numberOfCloseWords);
    }

    private Pair<List<String>, List<String>> getMostAndLeastSimilarWordsCore(String word, int topX, int botX)
    {
        return _modelAccessor.getMostAndLeastSimilarWordsTo(word, topX, botX);
    }

    //endregion

    //region Protected Methods

    public Word2Vec getModel()
    {
        return _modelAccessor.getModel();
    }

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