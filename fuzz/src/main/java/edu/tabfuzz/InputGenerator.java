package edu.tabfuzz;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputGenerator {

    DataFormat[] inputSpecification;
    String generatedInputFilesFolder = "fuzz/src/main/java/edu/tabfuzz/generatedInputFiles/";
    private static final int INPUT_FILE_AMOUNT_OF_LINES = 2;
    private static final int ARRAY_SIZE = 5;
    private final WriterSettings ws;

    public InputGenerator(DataFormat[] inputSpecification, WriterSettings ws) {
        this.inputSpecification = inputSpecification;
        this.ws = ws;
    }

    /**
     * Generates an inputfile (CSV format) based on the inputSpecification generated in the InputManager.
     * @return the filePath of the generated file
     */
    public String generateInputFile() {
        String fileName = "InputFile";
        fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
        String filePath = generatedInputFilesFolder + fileName + ".csv";
        try {

            CSVWriter writer = new CSVWriter(new FileWriter(filePath), ws.getSeparator(), ws.getQuoteChar(), ws.getEscapeChar(), ws.getLineEnd());
            for (int i = 0; i < INPUT_FILE_AMOUNT_OF_LINES; i++) { //TODO: i is the amount of lines; BigFuzz-> generates 1 to 20 lines (randomly chosen)
                String[] inputData = generateInputData();
                writer.writeNext(inputData);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * Generates a row of data in the form of an array.
     * @return a row of data in the form of an array.
     */
    private String[] generateInputData() {
        String[] dataFile = new String[inputSpecification.length];
        for(int i = 0; i < inputSpecification.length; i++) {
            if (inputSpecification[i].getDataType().contains("array")) {
                dataFile[i] = inputSpecification[i].generateArrayInputInRange(ARRAY_SIZE); //TODO: Find a way to get the correct array size/ Build support for arrays within arrays?
            } else {
                dataFile[i] = inputSpecification[i].generateInputInRange();
            }
        }

        return dataFile;
    }

}
