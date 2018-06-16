package com.Configuration;

import com.Utility.Helper.ResourceProvider;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public abstract class ConfigurationBase
{
    //region Fields

    protected Logger _log;

    //endregion

    //region Abstract Methods

    protected abstract boolean processLineContent(String line);

    //endregion

    protected void loadConfigurationFromLocalResourceFile(String localResourcePath) throws IOException
    {
        try (Stream<String> lineStream = Files.lines(ResourceProvider.getLocalResource(localResourcePath)))
        {
            for (String line : (Iterable<String>) lineStream::iterator)
            {
                if (!processLineContent(line))
                {
                    System.out.println("ERROR: Invalid configuration detected.");
                    return;
                }
            }
        }
    }

    protected void initializeInternal(String path) throws IOException
    {
        loadConfigurationFromLocalResourceFile(path);
    }
}