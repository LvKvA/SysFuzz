package P1.Wordcount.Spec_BigFuzz;

import main.java.sharedClasses.CustomArray;

import java.util.ArrayList;

public class WordCount {
public void WordCount(String inputFile) throws Exception {
ArrayList<String> results0 = CustomArray.read(inputFile);
ArrayList<String[]> results1 = WordCountCustomArray.FlatMap1(results0);
ArrayList<mapToPair2> results2 = WordCountCustomArray.MapToPair1(results1);
ArrayList<mapToPair2> results3 = WordCountCustomArray.ReduceByKey1(results2);
}}
