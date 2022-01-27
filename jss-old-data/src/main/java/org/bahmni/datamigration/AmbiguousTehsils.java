package org.bahmni.datamigration;

import org.apache.log4j.Logger;

import java.util.HashSet;

public class AmbiguousTehsils {
    private static Logger logger = Logger.getLogger(CorrectedTehsils.class);
    private HashSet tehsils = new HashSet();

    public AmbiguousTehsils(String fileLocation, String fileName) throws IOException {
        File file = new File(fileLocation, fileName);
        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;

                tehsils.add(line);
            }
            logger.info(String.format("Found %d ambiguous tehsils", tehsils.size()));
        } finally {
            bufferedReader.close();
        }
    }

    public boolean contains(String tehsil) {
        return tehsils.contains(tehsil);
    }
}