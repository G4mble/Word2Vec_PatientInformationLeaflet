package com.Utility.Converter;

import org.apache.commons.io.FileUtils;
import org.datavec.api.util.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

public class BinaryToPdfConverterRoot
{
    private static Logger _log = LoggerFactory.getLogger(BinaryToPdfConverterRoot.class);

    private static int count;

    public static void main(String[] args) throws Exception
    {
//        preprocessBinaries();
        Stream<Path> paths = Files.walk(Paths.get("H:\\Daten\\Uni\\Master\\2.Semester\\EEB\\%Project\\_FULL_DATA_BACKUP\\MED_PDF\\binary\\single"));
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
            byte[] outputBytes = new byte[0];
            outputBytes = Base64.getDecoder().decode(fileContent);
            DataOutputStream os = new DataOutputStream(new FileOutputStream("medData_" + count + ".pdf"));
            os.write(outputBytes);
            os.close();
            count++;
        }
        catch(Exception ex)
        {
            _log.error("Unexpected error in convertBinaryToPdf.", ex);
        }
    }

    private static void preprocessBinaries() throws Exception
    {
        File file = new File("H:\\Daten\\Uni\\Master\\2.Semester\\EEB\\%Project\\_FULL_DATA_BACKUP\\MED_PDF\\binary\\raw\\binaryData.dat");
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
