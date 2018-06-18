package com.Utility.Helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static List<String> loadDocumentLinesToList(Path path, Charset charset) throws IOException
    {
        return Files.readAllLines(path, charset);
    }

    public static List<String> loadDocumentLinesToList(String path, Charset charset) throws IOException
    {
        return loadDocumentLinesToList(Paths.get(path), charset);
    }

    public static List<String> loadDocumentLinesToListFromLocalResource(String localResourcePath, Charset charset) throws IOException
    {
        return loadDocumentLinesToList(ResourceProvider.getLocalResource(localResourcePath), charset);
    }

    public static Set<String> loadDocumentLinesToSet(Path path, Charset charset) throws IOException
    {
        return new HashSet<>(Files.readAllLines(path, charset));
    }

    public static Set<String> loadDocumentLinesToSetFromLocalResource(String localResourcePath, Charset charset) throws IOException
    {
        return loadDocumentLinesToSet(ResourceProvider.getLocalResource(localResourcePath), charset);
    }

    public static Set<String> loadDocumentLinesToSet(String path, Charset charset) throws IOException
    {
        return loadDocumentLinesToSet(Paths.get(path), charset);
    }
}