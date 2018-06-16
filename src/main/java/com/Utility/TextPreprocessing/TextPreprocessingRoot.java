package com.Utility.TextPreprocessing;

import com.Configuration.PreprocessingConfiguration;
import com.Contracts.IPreprocessingUtility;
import com.Utility.TextPreprocessing.DataSourcePreprocessor.WikipediaPreprocessor;
import com.Utility.TextPreprocessing.PreprocessingCore.CommonPreprocessingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TextPreprocessingRoot
{
    private static Logger _log = LoggerFactory.getLogger(TextPreprocessingRoot.class);

    private static final String PREPROCESSING_CONFIG_PATH = "configuration/preprocessing_config.cfg";

    private static long _numberOfDocs; // usage of the variable depends on the currently selected method
    private static long _currentDocNumber;

    public static void main(String[] args)
    {
        PreprocessingConfiguration config = new PreprocessingConfiguration(PREPROCESSING_CONFIG_PATH, _log);
        IPreprocessingUtility preprocessingUtils = new CommonPreprocessingUtils(config, _log);

        //TODO use this if "one med per line"
//        _log.warn("PERFORMING OPERATION FOR: \"ONE MED PER LINE - SINGLE DOCUMENT\"");
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
//        catch (Exception ex)
//        {
//            _log.error("Unexpected error in main.", ex);
//        }
//
//        //TODO use this if "one sentence per line - one med per document"
//        _log.warn("PERFORMING OPERATION FOR: \"ONE SENTENCE PER LINE - ONE MED PER DOCUMENT\"");
//        try
//        {
//            String saveDirectory = "G:\\__EEB\\liveData\\preprocessed\\";
//            String medDataPath = "G:\\__EEB\\liveData\\raw";
//
//            Stream<Path> paths = Files.walk(Paths.get(medDataPath));
//            paths.filter(Files::isRegularFile).forEach(x -> preprocessOneSentecePerLineOneMedPerDocument(x, saveDirectory));
//            paths.close();
//        }
//        catch (Exception ex)
//        {
//            _log.error("Unexpected error in main.", ex);
//        }

        //TODO TS this is wikipedia combine/string cleaning
        WikipediaPreprocessor wikiPreprocessor = new WikipediaPreprocessor(preprocessingUtils, config,_log);
        wikiPreprocessor.runProcess();
    }

//    private static void preprocessOneSentecePerLineOneMedPerDocument(Path path, String saveDirectory)
//    {
//        _log.info("Processing " + path + " ...");
//        File inputFile;
//        File outputFile;
//        try
//        {
//            inputFile = path.toFile();
//            outputFile = new File(saveDirectory + inputFile.getName().replace(".txt", "") + "_preprocessed.txt");
//            outputFile.getParentFile().mkdirs();
//            outputFile.createNewFile();
//        }
//        catch (Exception ex)
//        {
//            _log.error("Unexpected error in preprocessOneSentecePerLineOneMedPerDocument.", ex);
//            _log.warn("TERMINATING...");
//            return;
//        }
//
//        String currentLine;
//        int lineCount = 1;
//        StringBuilder builder = new StringBuilder();
//        try (FileReader reader = new FileReader(inputFile);
//             BufferedReader br = new BufferedReader(reader);
//             FileWriter writer = new FileWriter(outputFile);
//             BufferedWriter out = new BufferedWriter(writer))
//        {
//            while ((currentLine = br.readLine()) != null)
//            {
//                currentLine = currentLine.toLowerCase().trim();
//                if(lineCount == 1)
//                {
//                    String[] firstLineItems = currentLine.split(":");
//                    currentLine = firstLineItems[2];
//                }
////                else
////                {
////                    currentLine = currentLine.substring(currentLine.indexOf(":") + 1, currentLine.length()).trim();
////                }
//
//                currentLine = currentLine.replaceAll("\\u00A0", " ");
//                currentLine = currentLine.replace("ü", "ue");
//                currentLine = currentLine.replace("ä", "ae");
//                currentLine = currentLine.replace("ö", "oe");
//                currentLine = currentLine.replace("ß", "ss");
//                currentLine = currentLine.replace("?", ".");
//                currentLine = currentLine.replace("!", ".");
//
//                currentLine = currentLine.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
//                currentLine = currentLine.replace("-", " ");
//                currentLine = currentLine.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'_:,;<>|^°@€\\uFFFD]+", " ");
//                currentLine = currentLine.replace(".", " ");
//                currentLine = currentLine.replaceAll("[^\\x00-\\x7F]", " ");
//
//                String[] wordsInLine = currentLine.split(" ");
//                StringBuilder newLine = new StringBuilder();
//
//                for (String element:wordsInLine)
//                {
//                    //TODO TS eval stopWord removal
//                    if(_abbreviations.contains(element) || _stopwords.contains(element) || element.length() < 3)
//                        continue;
//                    newLine.append(element.trim()).append(" ");
//                }
//
//                builder.append(newLine.toString().trim()).append("\n");
//
//                lineCount++;
//            }
//            out.write(builder.toString().trim());
//            out.flush();
//        }
//        catch(Exception ex)
//        {
//            _log.error("Unexpected error in preprocessOneSentecePerLineOneMedPerDocument.", ex);
//        }
//    }
//
//    private static void modifyFile(Path path, BufferedWriter out)
//    {
//        _log.info("Processing " + path + " ...");
//        File currentFile = path.toFile();
//        String currentLine;
//        int lineCount = 1;
//        StringBuilder builder = new StringBuilder();
//        try (FileReader reader = new FileReader(currentFile);
//             BufferedReader br = new BufferedReader(reader))
//        {
//            while ((currentLine = br.readLine()) != null)
//            {
//                currentLine = currentLine.toLowerCase().trim();
//                if(lineCount == 1)
//                {
//                    String[] firstLineItems = currentLine.split(":");
//                    currentLine = firstLineItems[2];
//                }
//                else
//                {
//                    currentLine = currentLine.substring(currentLine.indexOf(":") + 1, currentLine.length()).trim();
//                }
//
//                currentLine = currentLine.replaceAll("\\u00A0", " ");
//                currentLine = currentLine.replace("ü", "ue");
//                currentLine = currentLine.replace("ä", "ae");
//                currentLine = currentLine.replace("ö", "oe");
//                currentLine = currentLine.replace("ß", "ss");
//                currentLine = currentLine.replace("?", ".");
//                currentLine = currentLine.replace("!", ".");
//
//                currentLine = currentLine.replaceAll("(?:(?:[0-9]{1,2}[:\\-|\\.|\\/,]){2}[0-9]{2,4})", " ");
//                currentLine = currentLine.replace("-", " ");
//                currentLine = currentLine.replaceAll("[\\d\"§$%&/()=`ß´²³{\\[\\]}\\\\+*~#'_:,;<>|^°@€\\uFFFD]+", " ");
//                currentLine = currentLine.replace(".", " ");
//                currentLine = currentLine.replaceAll("[^\\x00-\\x7F]", " ");
//
//                String[] wordsInLine = currentLine.split(" ");
//                StringBuilder newLine = new StringBuilder();
//
//                for (String element:wordsInLine)
//                {
//                    //TODO TS eval stopWord removal
//                    if(_abbreviations.contains(element) || _stopwords.contains(element) || element.length() < 3)
//                        continue;
//                    newLine.append(element.trim()).append(" ");
//                }
//
//                builder.append(newLine.toString());
//
//                lineCount++;
//            }
//            out.write(builder.toString().trim());
//
//            if(_currentDocNumber != _numberOfDocs)
//                out.newLine();
//
//            out.flush();
//
//            _currentDocNumber++;
//        }
//        catch(Exception ex)
//        {
//            _log.error("Unexpected error in modifyFile.", ex);
//        }
//    }

    private static void processFile(Path path)
    {
        _log.info("Processing " + path + " ...");
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
            _log.error("Unexpected error in processFile", ex);
        }
    }
}