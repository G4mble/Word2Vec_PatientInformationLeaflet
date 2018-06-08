package com.TSNE;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSNERoot
{
//    private static Logger log = LoggerFactory.getLogger(TSNERoot.class);
//
//    public static void main(String[] args) throws Exception  {
//
//        try(MemoryWorkspace scopedOut = Nd4j.getWorkspaceManager().scopeOutOfWorkspaces())
//        {
//            //create an n-dimensional array of doubles
//            Nd4j.setDataType(DataBuffer.Type.DOUBLE);
//            List<String> cacheList = new ArrayList<>(); //cacheList is a dynamic array of strings used to hold all words
//
//            //STEP 2: Turn text input into a list of words
//            log.info("Load & Vectorize data....");
////            File wordFile = new ClassPathResource("weightedWords.txt").getFile();   //Open the file
////
////            //Get the data of all unique word vectors
////            Pair<InMemoryLookupTable,VocabCache> vectors = WordVectorSerializer.loadTxt(wordFile);
////            VocabCache cache = vectors.getSecond();
////            INDArray weights = vectors.getFirst().getSyn0();    //seperate weights of unique words into their own list
//
//            File modelFile = new File(new ClassPathResource("model_output/M_003_model_output_2018-05-31_02-18-43.cmf").getFile().getAbsolutePath());
//            Word2Vec model = WordVectorSerializer.readWord2VecModel(modelFile);
//            VocabCache cache2 = model.getVocab();
//            InMemoryLookupTable table = (InMemoryLookupTable)model.lookupTable();
//            INDArray weights2 = table.getSyn0();
//
//            for(int i = 0; i < cache2.numWords(); i++)   //seperate strings of words into their own list
//                cacheList.add(cache2.wordAtIndex(i));
//
//            //STEP 3: build a dual-tree tsne to use later
//            log.info("Build model....");
//            BarnesHutTsne tsne = new BarnesHutTsne.Builder()
//                    .setMaxIter(100).theta(0.5)
//                    .normalize(false)
//                    .learningRate(500)
//                    .useAdaGrad(false)
//                    //                .usePca(false)
//                    .build();
//
//            //STEP 4: establish the tsne values and save them to a file
//            log.info("Store TSNE Coordinates for Plotting....");
//            String outputFile = "target/archive-tmp/tsne-standard-coords.csv";
//            (new File(outputFile)).getParentFile().mkdirs();
//
//            tsne.fit(weights2);
//            tsne.saveAsFile(cacheList, outputFile);
//
//            //This tsne will use the weights of the vectors as its matrix, have two dimensions, use the words strings as
//            //labels, and be written to the outputFile created on the previous line
//            // Plot Data with gnuplot
//            //    set datafile separator ","
//            //    plot 'tsne-standard-coords.csv' using 1:2:3 with labels font "Times,8"
//            //!!! Possible error: plot was recently deprecated. Might need to re-do the last line
//            //
//            // If you use nDims=3 in the call to tsne.plot above, you can use the following gnuplot commands to
//            // generate a 3d visualization of the word vectors:
//            //    set datafile separator ","
//            //    splot 'tsne-standard-coords.csv' using 1:2:3:4 with labels font "Times,8"
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
}
