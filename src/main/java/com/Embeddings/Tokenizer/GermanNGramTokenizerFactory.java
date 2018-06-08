package com.Embeddings.Tokenizer;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.InputStream;

/*
    THIS CLASS IS BASED ON:
        org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizerFactory

    SEE "com._LICENSE.deeplearning4j_LICENSE" FOR FURTHER INFORMATION.
 */
public class GermanNGramTokenizerFactory implements TokenizerFactory
{
    private TokenPreProcess preProcess;
    private Integer minN;
    private Integer maxN;
    private TokenizerFactory tokenizerFactory;

    public GermanNGramTokenizerFactory(TokenizerFactory tokenizerFactory, Integer minN, Integer maxN)
    {
        this.tokenizerFactory = tokenizerFactory;
        this.minN = minN;
        this.maxN = maxN;
    }

    public Tokenizer create(String toTokenize)
    {
        if (toTokenize != null && !toTokenize.isEmpty())
        {
            Tokenizer t1 = this.tokenizerFactory.create(toTokenize);
            t1.setTokenPreProcessor(this.preProcess);
            return new GermanNGramTokenizer(t1, this.minN, this.maxN);
        } else
        {
            throw new IllegalArgumentException("Unable to proceed; no sentence to tokenize");
        }
    }

    public Tokenizer create(InputStream toTokenize)
    {
        throw new UnsupportedOperationException();
    }

    public void setTokenPreProcessor(TokenPreProcess preProcessor)
    {
        this.preProcess = preProcessor;
    }

    public TokenPreProcess getTokenPreProcessor()
    {
        return this.preProcess;
    }
}
