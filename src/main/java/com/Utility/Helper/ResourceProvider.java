package com.Utility.Helper;

import org.datavec.api.util.ClassPathResource;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceProvider
{
    //region Public Methods

    public static Path getLocalResource(String localResourcePath) throws FileNotFoundException
    {
        return Paths.get(new ClassPathResource(localResourcePath).getFile().getAbsolutePath());
    }

    //endregion
}
