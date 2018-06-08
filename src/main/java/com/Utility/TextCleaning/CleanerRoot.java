package com.Utility.TextCleaning;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CleanerRoot
{
    private static final List<String> _abbreviations = new ArrayList<>();
    private static final List<String> _stopwords = new ArrayList<>();
    private static int _count = 1001;
    private static long _numberOfDocs;
    private static long _currentDocNumber;

    public static void main(String[] args)
    {
        readAbbreviations();
        readStopwords();

        //TODO use this if "one med per line"
//        try
//        {
//            String saveDirectory = "G:\\__EEB\\liveData\\preprocessed\\";
//
//            File f = new File(saveDirectory + "medData_combined.txt");
//            f.getParentFile().mkdirs();
//            f.createNewFile();
//
//            FileWriter writer = new FileWriter(f);
//            BufferedWriter out = new BufferedWriter(writer);
//
//            String medDataPath = "G:\\__EEB\\liveData\\raw";
//
//            Stream<Path> countHelper = Files.walk(Paths.get(medDataPath));
//            _numberOfDocs = countHelper.filter(Files::isRegularFile).count();
//            _currentDocNumber = 1;
//            countHelper.close();
//
//            Stream<Path> paths = Files.walk(Paths.get(medDataPath));
//            paths.filter(Files::isRegularFile).forEach(x -> modifyFile(x, out));
//            paths.close();
//
//            out.close();
//            writer.close();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        //TODO use this if "one sentence per line - one med per document"
        try
        {
            String saveDirectory = "G:\\__EEB\\liveData\\preprocessed\\";
            String medDataPath = "G:\\__EEB\\liveData\\raw";

            Stream<Path> paths = Files.walk(Paths.get(medDataPath));
            paths.filter(Files::isRegularFile).forEach(x -> preprocessOneSentecePerLineOneMedPerDocument(x, saveDirectory));
            paths.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //TODO TS this is wikipedia combine/string cleaning
//        File parentDir = new File("G:\\IntelliJIdea\\Word2Vec_PatientInformationLeaflet\\src\\main\\resources\\data\\wikidump");
//        File[] subDirs = parentDir.listFiles();
//        assert subDirs != null;
//        for (File directory:subDirs)
//        {
//            if(!directory.isDirectory())
//                continue;
//            processDirectory(directory.toPath());
//        }
    }

    private static void preprocessOneSentecePerLineOneMedPerDocument(Path path, String saveDirectory)
    {
        System.out.println("Processing " + path + " ...");
        File inputFile;
        File outputFile;
        try
        {
            inputFile = path.toFile();
            outputFile = new File(saveDirectory + inputFile.getName().replace(".txt", "") + "_preprocessed.txt");
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println("ERROR --> terminating...");
            return;
        }

        String currentLine;
        int lineCount = 1;
        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(inputFile);
             BufferedReader br = new BufferedReader(reader);
             FileWriter writer = new FileWriter(outputFile);
             BufferedWriter out = new BufferedWriter(writer))
        {
            while ((currentLine = br.readLine()) != null)
            {
                currentLine = currentLine.toLowerCase().trim();
                if(lineCount == 1)
                {
                    String[] firstLineItems = currentLine.split(":");
                    currentLine = firstLineItems[2];
                }
//                else
//                {
//                    currentLine = currentLine.substring(currentLine.indexOf(":") + 1, currentLine.length()).trim();
//                }

                currentLine = currentLine.replaceAll("\\u00A0", " ");
                currentLine = currentLine.replace("ü", "ue");
                currentLine = currentLine.replace("ä", "ae");
                currentLine = currentLine.replace("ö", "oe");
                currentLine = currentLine.replace("ß", "ss");
                currentLine = currentLine.replace("?", ".");
                currentLine = currentLine.replace("!", ".");

                currentLine = currentLine.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
                currentLine = currentLine.replace("-", " ");
                currentLine = currentLine.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'_:,;<>|^°@€\\uFFFD]+", " ");
                currentLine = currentLine.replace(".", " ");
                currentLine = currentLine.replaceAll("[^\\x00-\\x7F]", " ");

                String[] wordsInLine = currentLine.split(" ");
                StringBuilder newLine = new StringBuilder();

                for (String element:wordsInLine)
                {
                    //TODO TS eval stopWord removal
                    if(_abbreviations.contains(element) || _stopwords.contains(element) || element.length() < 3)
                        continue;
                    newLine.append(element.trim()).append(" ");
                }

                builder.append(newLine.toString().trim()).append("\n");

                lineCount++;
            }
            out.write(builder.toString().trim());
            out.flush();
        }
        catch(Exception ex)
        {
            System.out.println("Some more exceptions. Over here!");
        }
    }

    private static void readStopwords()
    {
        try
        {
            List<String> lines = Files.readAllLines(Paths.get("G:\\IntelliJIdea\\Word2Vec_PatientInformationLeaflet\\src\\main\\resources\\configuration\\stopWords.txt"), StandardCharsets.UTF_8);
            _stopwords.addAll(lines);
        }
        catch (IOException e)
        {
            System.out.println("new error");
        }
    }
    private static void readAbbreviations()
    {
        try
        {
            List<String> lines = Files.readAllLines(Paths.get("H:\\Daten\\Uni\\Master\\2.Semester\\EEB\\%Project\\005_German Abbreviations\\abbreviations.txt"), StandardCharsets.UTF_8);
            _abbreviations.addAll(lines);
        }
        catch (IOException e)
        {
            System.out.println("new error");
        }
    }
    private static void processLine(String line, PrintWriter output)
    {
        line = line.toLowerCase();

        for (String element:_abbreviations)
        {
            line = line.replace(element, "");
        }

        line = line.replaceAll("\\u00A0", " ");
        line = line.replace("ü", "ue");
        line = line.replace("ä", "ae");
        line = line.replace("ö", "oe");
        line = line.replace("ß", "ss");
        line = line.replace("?", ".");
        line = line.replace("!", ".");

        //remove all dates separated by dot, hyphen or slash [dd.MM.YY{YY} OR MM.dd.YY{YY} OR d.M.YY{YY} OR M.d.YY{YY}]
        line = line.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", "");
        line = line.replace("-", " ");
        // leave the \\. in the line
        // ? and ! and - have already been replaced
        line = line.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'_:,;<>|^°@€\\uFFFD]+", "");
        //replace all non-ascii chars
        line = line.replaceAll("[^\\x00-\\x7F]", "");
        String[] lines = line.split("\\.");
        for(String currentLine:lines)
        {
            currentLine = currentLine.trim();
            currentLine = currentLine.replace(".", "");
            String[] wordsInLine = currentLine.split(" ");
            StringBuilder lineBuilder = new StringBuilder();
            for(String word:wordsInLine)
            {
                //TODO TS eval stopWord removal
//                if(_stopwords.contains(word))
//                    continue;
                lineBuilder.append(" ");
                lineBuilder.append(word);
            }
            currentLine = lineBuilder.toString().trim();
            if(currentLine.length() > 15)
                output.println(currentLine);
        }
    }
    private static void concatenateFile(Path path, PrintWriter output)
    {
        System.out.println("Processing " + path + " ...");
        try
        {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            lines.forEach(x -> processLine(x, output));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private static void processDirectory(Path directory)
    {
        try (Stream<Path> paths = Files.walk(directory))
        {
            String saveDirectory = "G:\\IntelliJIdea\\Word2Vec_PatientInformationLeaflet\\src\\main\\resources\\data\\wikidump_combined_stopwords\\";
            File f = new File(saveDirectory + "combined_stopwords_" + _count);
            f.getParentFile().mkdirs();
            f.createNewFile();
            try(FileWriter fw = new FileWriter(f, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                paths.filter(Files::isRegularFile).forEach(x -> concatenateFile(x, out));
            }
            _count++;
        }
        catch (Exception e)
        {
            System.out.println("ERROR!!");
        }
    }
    private static void modifyFile(Path path, BufferedWriter out)
    {
        System.out.println("Processing " + path + " ...");
        File currentFile = path.toFile();
        String currentLine;
        int lineCount = 1;
        StringBuilder builder = new StringBuilder();
        try (FileReader reader = new FileReader(currentFile);
             BufferedReader br = new BufferedReader(reader))
        {
            while ((currentLine = br.readLine()) != null)
            {
                currentLine = currentLine.toLowerCase().trim();
                if(lineCount == 1)
                {
                    String[] firstLineItems = currentLine.split(":");
                    currentLine = firstLineItems[2];
                }
                else
                {
                    currentLine = currentLine.substring(currentLine.indexOf(":") + 1, currentLine.length()).trim();
                }

                currentLine = currentLine.replaceAll("\\u00A0", " ");
                currentLine = currentLine.replace("ü", "ue");
                currentLine = currentLine.replace("ä", "ae");
                currentLine = currentLine.replace("ö", "oe");
                currentLine = currentLine.replace("ß", "ss");
                currentLine = currentLine.replace("?", ".");
                currentLine = currentLine.replace("!", ".");

                currentLine = currentLine.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
                currentLine = currentLine.replace("-", " ");
                currentLine = currentLine.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'_:,;<>|^°@€\\uFFFD]+", " ");
                currentLine = currentLine.replace(".", " ");
                currentLine = currentLine.replaceAll("[^\\x00-\\x7F]", " ");

                String[] wordsInLine = currentLine.split(" ");
                StringBuilder newLine = new StringBuilder();

                for (String element:wordsInLine)
                {
                    //TODO TS eval stopWord removal
                    if(_abbreviations.contains(element) || _stopwords.contains(element) || element.length() < 3)
                        continue;
                    newLine.append(element.trim()).append(" ");
                }

                builder.append(newLine.toString());

                lineCount++;
            }
            out.write(builder.toString().trim());

            if(_currentDocNumber != _numberOfDocs)
                out.newLine();

            out.flush();

            _currentDocNumber++;
        }
        catch(Exception ex)
        {
            System.out.println("Some more exceptions. Over here!");
        }
    }
    private static void processFile(Path path)
    {
        System.out.println("Processing " + path + " ...");
        File currentFile = path.toFile();
        String currentLine;
        List<String> lines = new ArrayList<>();
        try (FileReader reader = new FileReader(currentFile);
             BufferedReader br = new BufferedReader(reader))
        {
            while ((currentLine = br.readLine()) != null)
            {
                String[] words = currentLine.split(" ");
                //TODO TS edit words.length to match the context window size
                if(words.length < 5)
                    continue;
                lines.add(currentLine);
            }
            FileWriter writer = new FileWriter(currentFile);
            BufferedWriter out = new BufferedWriter(writer);
            int count = lines.size();
            int currentElement = 1;
            for (String s : lines)
            {
                out.write(s);
                if(currentElement != count)
                    out.newLine();
                currentElement++;
            }
            out.flush();
            out.close();
            writer.close();
        }
        catch (Exception ex)
        {
            System.out.println("Another error");
        }
    }
}