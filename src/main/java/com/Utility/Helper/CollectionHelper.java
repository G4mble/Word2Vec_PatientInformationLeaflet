package com.Utility.Helper;

import java.util.Collection;

public class CollectionHelper
{
    public static String collectionToString(Collection<String> input, Object elementSeparator)
    {
        StringBuilder builder = new StringBuilder();
        for(String element:input)
            builder.append(element).append(elementSeparator);
        return builder.toString().trim();
    }
}