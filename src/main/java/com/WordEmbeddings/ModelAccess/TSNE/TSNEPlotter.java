package com.WordEmbeddings.ModelAccess.TSNE;

import org.deeplearning4j.plot.BarnesHutTsne;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.util.List;

public class TSNEPlotter
{
    public void plotInput(INDArray wordVectors, List<String> wordList, String outputFileName)
    {
        try
        {
            BarnesHutTsne tsne = new BarnesHutTsne.Builder()
                    .setMaxIter(1000)
                    .numDimension(2)
                    .theta(0.5)
                    .learningRate(10)
                    .perplexity(5)
                    .normalize(false)
                    .useAdaGrad(false)
                    .build();

            File outputFile = new File(outputFileName);
            outputFile.getParentFile().mkdirs();

            tsne.fit(wordVectors);
            tsne.saveAsFile(wordList, outputFileName);

            // Plot Data with gnuplot
            //    set datafile separator ","
            //    plot 'tsne-standard-coords.csv' using 1:2:3 with labels font "Times,8"
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}