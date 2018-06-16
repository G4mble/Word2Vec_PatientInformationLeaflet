package com.Utility.Helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileHelper
{
    public static void writeContentToExistingFile(String content, File file) throws IOException
    {
        try(FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw))
        {
            writer.write(content);
            writer.flush();
        }
    }

    public static File createFileAndDirectory(String fullName) throws IOException
    {
        File file = new File(fullName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }

    public static Set<String> loadDocumentLinesToSetFromLocalResource(String localResourcePath, Charset charset) throws IOException
    {
        List<String> lines = Files.readAllLines(ResourceProvider.getLocalResource(localResourcePath), charset);
        return new HashSet<>(lines);
    }

    public static List<String> loadDocumentLinesToListFromLocalResource(String localResourcePath, Charset charset) throws IOException
    {
        return Files.readAllLines(ResourceProvider.getLocalResource(localResourcePath), charset);
    }
}