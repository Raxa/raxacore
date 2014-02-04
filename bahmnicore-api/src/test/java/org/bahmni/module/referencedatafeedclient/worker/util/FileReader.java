package org.bahmni.module.referencedatafeedclient.worker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileReader {
    private String fileName;

    public FileReader(String fileName) {
        this.fileName = fileName;
    }

    public String readFile() throws IOException {
        BufferedReader fileReader = null;
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
            fileReader = new BufferedReader(new InputStreamReader(resourceAsStream));
            StringBuilder fileContents = new StringBuilder();
            String aLine;
            while ((aLine = fileReader.readLine()) != null) {
                fileContents.append(aLine);
            }
            return fileContents.toString();
        } finally {
            if (fileReader != null ) fileReader.close();
        }
    }
}
