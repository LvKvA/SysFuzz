package edu.tabfuzz;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import edu.ucla.cs.jqf.bigfuzz.BigFuzzMutation;
import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TabFuzzMutation implements BigFuzzMutation {

    private static final int MUTATIONS_AMOUNT = 6;
    private final DataFormat[] dataSpecification;
    private final WriterSettings ws;

    /**
     * String fileName = "InputFile";
     *         fileName += new SimpleDateFormat("yyyyMMddHHmmssSS").format(Calendar.getInstance().getTime());
     *         String filePath = GENERATED_INPUT_FILES_FOLDER + fileName + ".csv";
     */
    public TabFuzzMutation(DataFormat[] dataSpecification, WriterSettings ws) {
        this.dataSpecification = dataSpecification;
        this.ws = ws;
    }

    public void mutateFile(String fileName, String newFileName) {
        String currentFile = "";
        try {
            Scanner sc = new Scanner(new File(fileName));
            currentFile = sc.nextLine().trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String newFilePath = configurationFileGenerator(newFileName);

        List<String[]> data;
        try {
            CSVReader reader = new CSVReader(new FileReader(currentFile));
            data = reader.readAll();
            performRandomMutation(data, newFilePath);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public String configurationFileGenerator(String filePath) {
        String actualFile = "fuzz/src/main/java/edu/tabfuzz/generatedInputFiles/" + filePath.substring(filePath.lastIndexOf('/')+1);
        try {
            FileWriter fw = new FileWriter(filePath);
            fw.write(actualFile);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actualFile;
    }

    public void performRandomMutation(List<String[]> data, String currentFile) {
        int r = (int) (Math.random() * MUTATIONS_AMOUNT);
//        r = 5;
        List<String[]> newData = data;
        switch (r) {
            case 0:
                System.out.println("Mutation performed: dataDistributionMutation");
                newData = dataDistributionMutation(data);
                break;
            case 1:
                ArrayList<Integer> nonStringDataTypeIndices = findNonStringDataTypes();
                if (nonStringDataTypeIndices.size() == 0) {
                    performRandomMutation(data, currentFile);
                    break;
                }
                System.out.println("Mutation performed: dataTypeMutation");
                newData = dataTypeMutation(data, nonStringDataTypeIndices);
                break;
            case 2:
                System.out.println("Mutation performed: dataColumnMutation");
                newData = dataColumnMutation(data);
                break;
            case 3:
                System.out.println("Mutation performed: nullDataMutation");
                newData = nullDataMutation(data);
                break;
            case 4:
                System.out.println("Mutation performed: emptyDataMutation");
                newData = emptyDataMutation(data);
                break;
            case 5:
                ArrayList<Integer> specialValueIndices = findSpecialValueIndices();
                if (specialValueIndices.isEmpty()) {
                    System.out.println("No special values found: Picked another mutation");
                    performRandomMutation(data, currentFile);
                    break;
                }
                System.out.println("Mutation performed: specialValueMutation");
                newData = specialValueMutation(data, specialValueIndices);
                break;
        }

        writeMutation(newData, currentFile);
    }

    /**
     * Generates with a 50/50 chance data thats either in the range or not in the range.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> dataDistributionMutation(List<String[]> data) {
        int randomRow = (int) (Math.random() * data.size());
        int randomColumn = (int) (Math.random() * data.get(randomRow).length);
        int r = (int) (Math.random() * 2);
        if (r == 0) {
            data.get(randomRow)[randomColumn] = dataSpecification[randomColumn].generateInputInRange();
        } else {
            data.get(randomRow)[randomColumn] = dataSpecification[randomColumn].generateInputOutsideRange();
        }
        return data;
    }

    /**
     * Picks a random cell and changes the datatype of the element.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> dataTypeMutation(List<String[]> data, ArrayList<Integer> nonStringDataTypeIndices) {
        int randomRow = (int) (Math.random() * data.size());
        int randomColumn = (int) (Math.random() * nonStringDataTypeIndices.size());
        data.get(randomRow)[nonStringDataTypeIndices.get(randomColumn)] = dataSpecification[nonStringDataTypeIndices.get(randomColumn)].changeDataType(data.get(randomRow)[nonStringDataTypeIndices.get(randomColumn)]);
        return data;
    }

    /**
     * Picks a random random and adds an extra column at a random index.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> dataColumnMutation(List<String[]> data) {
        int randomRow = (int) (Math.random() * data.size());
        int randomColumn = (int) (Math.random() * (data.get(randomRow).length + 1));
        RgxGen generator = new RgxGen(".{1,5}");
        String[] updatedColumn = data.get(randomRow);
        updatedColumn = (String[]) ArrayUtils.add(updatedColumn, randomColumn, generator.generate());
        data.set(randomRow, updatedColumn);
        return data;
    }

    /**
     * Picks a random cell and removes the cell.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> nullDataMutation(List<String[]> data) {
        int randomRow = (int) (Math.random() * data.size());
        int randomColumn = (int) (Math.random() * data.get(randomRow).length);
        String[] updatedColumn = data.get(randomRow);
        updatedColumn = (String[]) ArrayUtils.remove(updatedColumn, randomColumn);
        data.set(randomRow, updatedColumn);
        return data;
    }

    /**
     * Picks a random cell and removes its data.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> emptyDataMutation(List<String[]> data) {
        int randomRow = (int) (Math.random() * data.size());
        int randomColumn = (int) (Math.random() * data.get(randomRow).length);
        data.get(randomRow)[randomColumn] = "";
        return data;
    }

    /**
     * Picks a random cell and replaces it with a programmer-defined special value.
     * @param data dataset to mutate.
     * @return the mutated dataset.
     */
    private List<String[]> specialValueMutation(List<String[]> data, ArrayList<Integer> specialValueIndices) {
        int randomRow = (int) (Math.random() * data.size());
        int randomColumn = (int) (Math.random() * specialValueIndices.size());

        String[] specialValues = dataSpecification[specialValueIndices.get(randomColumn)].getSpecialValues();
        int r = (int) (Math.random() * specialValues.length);

        data.get(randomRow)[specialValueIndices.get(randomColumn)] = specialValues[r];
        return data;
    }

    /**
     * Finds indices of the columns that actually specified special values.
     * @return list of indices.
     */
    private ArrayList<Integer> findSpecialValueIndices() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < dataSpecification.length; i++) {
            if (dataSpecification[i].getSpecialValues().length > 0) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * Find the indices of the columns that are not String datatypes
     * @return list of indices.
     */
    private ArrayList<Integer> findNonStringDataTypes() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < dataSpecification.length; i++) {
            if (!dataSpecification[i].getDataType().equals("String")) {
                result.add(i);
            }
        }
        return result;
    }

    private void writeMutation(List<String[]> data, String newFilePath) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(newFilePath), ws.getSeparator(), ws.getQuoteChar(), ws.getEscapeChar(), ws.getLineEnd());
            System.out.println(newFilePath);
            for (String[] dataRow : data) {
                writer.writeNext(dataRow);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mutate(String inputFile, String nextInputFile) {
        mutateFile(inputFile, nextInputFile);
    }

    public void mutateFile(String inputFile, int index) {
        System.err.println("Don't think this is used?");
    }

    @Override
    public void mutate(ArrayList<String> rows) {
        System.err.println("Don't think this is used?");

    }

    public void randomDuplicateRows(ArrayList<String> rows) {
        System.err.println("This should never be run");
    }

    public void randomGenerateRows(ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    public void randomGenerateOneColumn(int columnID, int minV, int maxV, ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    public void randomDuplacteOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    public void improveOneColumn(int columnID, int intV, int maxV, ArrayList<String> rows) {
        System.err.println("This should never be run");

    }

    @Override
    public void writeFile(String outputFile) {

    }

    @Override
    public void deleteFile(String currentFile) {

    }
}
