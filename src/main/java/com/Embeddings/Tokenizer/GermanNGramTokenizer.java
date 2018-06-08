package com.Embeddings.Tokenizer;

import org.apache.commons.lang3.StringUtils;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

/*
    THIS CLASS IS BASED ON:
        org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizer

    SEE "com._LICENSE.deeplearning4j_LICENSE" FOR FURTHER INFORMATION.
 */
class GermanNGramTokenizer implements Tokenizer
{
    private List<String> tokens = new ArrayList();
    private int index;
    private TokenPreProcess preProcess;

    public GermanNGramTokenizer(Tokenizer tokenizer, Integer minN, Integer maxN)
    {
        while (tokenizer.hasMoreTokens())
        {
            String nextToken = tokenizer.nextToken();
            this.tokens.add(nextToken);
        }

        if (maxN != 1)
        {
            List<String> originalTokens = this.tokens;
            this.tokens = new ArrayList();
            Integer nOriginalTokens = originalTokens.size();
            Integer min = Math.min(maxN + 1, nOriginalTokens + 1);

            for (int i = minN; i < min; ++i)
            {
                for (int j = 0; j < nOriginalTokens - i + 1; ++j)
                {
                    List<String> originalTokensSlice = new ArrayList(originalTokens.subList(j, j + i));
                    originalTokensSlice.removeIf(x -> x == null || x.length() < 2);
                    if(!originalTokensSlice.isEmpty())
                        this.tokens.add(StringUtils.join(originalTokensSlice, "_"));
                }
            }
        }

    }

    public boolean hasMoreTokens()
    {
        return this.index < this.tokens.size();
    }

    public int countTokens()
    {
        return this.tokens.size();
    }

    public String nextToken()
    {
        String ret = (String) this.tokens.get(this.index);
        ++this.index;
        return ret;
    }

    public List<String> getTokens()
    {
        ArrayList tokens = new ArrayList();

        while (this.hasMoreTokens())
        {
            tokens.add(this.nextToken());
        }

        return tokens;
    }

    public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor)
    {
        this.preProcess = tokenPreProcessor;
    }
}
