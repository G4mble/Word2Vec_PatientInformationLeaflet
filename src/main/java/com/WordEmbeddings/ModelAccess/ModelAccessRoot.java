package com.WordEmbeddings.ModelAccess;

import com.Configuration.ModelAccessConfiguration;
import com.Utility.Helper.CollectionHelper;
import com.WordEmbeddings.ModelAccess.Provider.ModelAccessProvider;
import com.ea.async.instrumentation.InitializeAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
        Collection<String> list = modelAccessProvider.getSimilarMedicaments("aspirin c orange", 20);
        if(list != null)
            System.out.println(CollectionHelper.collectionToString(list, " "));
        System.out.println("\n");
        list = modelAccessProvider.getSimilarMedicaments("ibuprofen wirkstoff aspirin", 20);
        if(list != null)
            System.out.println(CollectionHelper.collectionToString(list, " "));
        System.out.println("\n");
        list = modelAccessProvider.getSimilarMedicaments("ass", 20);
        if(list != null)
            System.out.println(CollectionHelper.collectionToString(list, " "));
        System.out.println("\n");
        list = modelAccessProvider.getSimilarMedicaments("hustensaft", 20);
        if(list != null)
            System.out.println(CollectionHelper.collectionToString(list, " "));
        System.out.println("\n");
        list = modelAccessProvider.getSimilarMedicaments("kopfschmerzen", 20);
        if(list != null)
            System.out.println(CollectionHelper.collectionToString(list, " "));
    }
}