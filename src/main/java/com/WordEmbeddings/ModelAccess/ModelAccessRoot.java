package com.WordEmbeddings.ModelAccess;

import com.Configuration.ModelAccessConfiguration;
import com.Utility.Helper.ResourceProvider;
import com.WordEmbeddings.ModelAccess.Provider.ModelAccessProvider;
import com.WordEmbeddings.ModelAccess.TSNE.TSNEPlotter;
import com.ea.async.instrumentation.InitializeAsync;
import javafx.util.Pair;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static com.ea.async.Async.await;

class ModelAccessRoot
{
    private static final Logger _log = LoggerFactory.getLogger(ModelAccessRoot.class);
    private static final String MODEL_ACCESS_CONFIG_FILE_PATH = "configuration/modelAccess_config.cfg";

    public static void main(String[] args)
    {
        init();
        new ModelAccessRoot().run();
    }

    private static void init()
    {
        InitializeAsync.init();
    }

    private void run()
    {
        ModelAccessConfiguration config = new ModelAccessConfiguration(MODEL_ACCESS_CONFIG_FILE_PATH, _log);
        ModelAccessProvider modelAccessProvider = await(ModelAccessProvider.getNewInstanceAsync(config, _log));
        if(modelAccessProvider == null)
        {
            _log.error("ModelAccessProvider null. Terminating...");
            return;
        }
        //TODO do some stuff with modelAccessProvider here

        try
        {
            Word2Vec model = modelAccessProvider.getModel();

//            List<String> etilefrin = (ArrayList<String>)modelAccessProvider.getSimilarMedicaments("effortil", 50);
//            etilefrin.add("effortil");
//            List<String> ilja = (ArrayList<String>)modelAccessProvider.getSimilarMedicaments("aciclovir", 50);
//            ilja.add("aciclovir");
//            etilefrin.addAll(ilja);
//            INDArray wordVectors = model.getWordVectors(etilefrin);

            List<String> nonPresMeds = Files.readAllLines(ResourceProvider.getLocalResource("data/non_prespricption_meds.txt"), StandardCharsets.UTF_8);
            INDArray wordVectors = model.getWordVectors(nonPresMeds);

            TSNEPlotter tsnePlotter = new TSNEPlotter();
            tsnePlotter.plotInput(wordVectors, nonPresMeds, "G:\\IntelliJIdea\\Word2Vec_PatientInformationLeaflet\\tsne-coords.csv");
        }
        catch (Exception ex)
        {
            _log.error("Unexpected error in main.", ex);
        }


//        Collection<String> list = modelAccessProvider.getSimilarMedicaments("aspirin", 20);
//        if(list != null)
//            System.out.println(CollectionHelper.collectionToString(list, " "));
//        list = modelAccessProvider.getSimilarMedicaments("ibuprofen", 20);
//        if(list != null)
//            System.out.println(CollectionHelper.collectionToString(list, " "));
//
//        double v = modelAccessProvider.calculateSimilarity("grün", "röt");
//        System.out.println("\n");
//        List<String> roteBeete = modelAccessProvider.findOrthographicallyCloseWordsTo("roteBeete", 0.99d);
//        if(roteBeete == null) System.out.println("Rote Beete null");
//        else System.out.println("Rote Beete: " + roteBeete);
//        System.out.println("\n");
//        List<String> words = modelAccessProvider.findOrthographicallyCloseWordsTo("aspirin", 0.85d);
//        if(words == null) System.out.println("apsirin words null");
//        else System.out.println("ASPIRIN: " + words);
//        System.out.println("\n");
//        Collection<String> semanticallySimilarWordsTo = modelAccessProvider.findSemanticallySimilarWordsTo(Arrays.asList("aspirin", "rückenschmerzen"), Arrays.asList("KoPFschmerzen"), 20);
//        if(semanticallySimilarWordsTo == null) System.out.println("Semantically similar words NULL");
//        else System.out.println("semanticall similar words: " + semanticallySimilarWordsTo);
//        System.out.println("\n");
//        Collection<String> semanticallySimilarWordsTo2 = modelAccessProvider.findSemanticallySimilarWordsTo(Arrays.asList("word", "embeddings"), Arrays.asList("root"), 20);
//        if(semanticallySimilarWordsTo2 == null) System.out.println("Semantically similar words _2_ NULL");
//        else System.out.println("semanticall similar words  _2_: " + semanticallySimilarWordsTo2);
//        System.out.println("\n");
//        Collection<String> someWord = modelAccessProvider.findSemanticallySimilarWordsTo("someWord", 10);
//        if(someWord == null) System.out.println("someWord NULL");
//        else System.out.println("SomeWord: " + someWord);
//        System.out.println("\n");
//        Collection<String> someWord2 = modelAccessProvider.findSemanticallySimilarWordsTo("übelKEIT", 10);
//        if(someWord2 == null) System.out.println("someWord _2_ NULL");
//        else System.out.println("SomeWord _2_: " + someWord2);
//        System.out.println("\n");
//        Collection<String> aspirin_rueckenschmerZEN = modelAccessProvider.findSemanticallySimilarWordsUsingVectorMean(Arrays.asList("aspirin", "rückenschmerZEN"), 10);
//        if(aspirin_rueckenschmerZEN == null) System.out.println("AspRueck NULL");
//        else System.out.println("AspRueck: " + aspirin_rueckenschmerZEN);
//        System.out.println("\n");
//        Collection<String> aspirin_rueckenschmerZEN2 = modelAccessProvider.findSemanticallySimilarWordsUsingVectorMean(Arrays.asList("grmblbtz", "someOtherWordThatIsNotInTheVocab"), 10);
//        if(aspirin_rueckenschmerZEN2 == null) System.out.println("AspRueck _2_ NULL");
//        else System.out.println("AspRueck _2_: " + aspirin_rueckenschmerZEN2);
//        System.out.println("\n");

//        list = modelAccessProvider.getSimilarMedicaments("ass", 20);
//        if(list != null)
//            System.out.println(CollectionHelper.collectionToString(list, " "));
//        list = modelAccessProvider.getSimilarMedicaments("hustensaft", 20);
//        if(list != null)
//            System.out.println(CollectionHelper.collectionToString(list, " "));
//        list = modelAccessProvider.getSimilarMedicaments("kopfschmerzen", 20);
//        if(list != null)
//            System.out.println(CollectionHelper.collectionToString(list, " "));
    }
}