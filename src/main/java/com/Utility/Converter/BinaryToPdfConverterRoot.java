package com.Utility.Converter;

import org.apache.commons.io.FileUtils;
import org.datavec.api.util.ClassPathResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

public class BinaryToPdfConverterRoot
{
    private static int count;

    public static void main(String[] args) throws Exception
    {
//        preprocessBinaries();
        Stream<Path> paths = Files.walk(Paths.get(new ClassPathResource("binary/single").getFile().getAbsolutePath()));
        count = 1001;
        paths.filter(Files::isRegularFile).forEach(BinaryToPdfConverterRoot::convertBinaryToPdf);
    }

    private static void convertBinaryToPdf(Path path)
    {
        File file = path.toFile();
        try(FileInputStream fis = new FileInputStream(file))
        {
            byte[] fileContent = new byte[(int)file.length()];
            fis.read(fileContent);
            byte[] outputBytes = Base64.getDecoder().decode(fileContent);
            DataOutputStream os = new DataOutputStream(new FileOutputStream("medData_" + count + ".pdf"));
            os.write(outputBytes);
            os.close();
            count++;
        }
        catch(Exception ex)
        {
            System.out.println("log some error");
        }
    }

    private static void preprocessBinaries() throws Exception
    {
        File file = new File(new ClassPathResource("binary/binaryData.dat").getFile().getAbsolutePath());
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader))
        {
            int count = 1001;
            String currentLine;
            while((currentLine = br.readLine()) != null)
            {
                FileUtils.write(new File("binary_" + count + ".dat"), currentLine);
                count++;
            }
        }
    }
}
