# SysFuzz: Systematic exploration of higher order mutations for fuzz testing big data applications
  
This repository contains the seeds and code used for evaluating our fuzzing tool. 
It was part of a project at the TU Delft, with four other members: Melchior Oudemans, Lars Rhijnsburger, Bo van den Berg, Martijn Smits. 
The code on this repository is an extension of the BigFuzz repository: https://github.com/qianzhanghk/BigFuzz.
The seeds can be found in the dataset folder, the java files for running the benchmarks in the CustomArray folder. 
There is also a folder called benchmarks, which contains the original spark code of the benchmarks. 
Our extension can be found in the following folder: \fuzz\src\main\java\edu\tud\cs\jqf\bigfuzzplus\systematicMutation

To reproduce our research run the tool with the following configuration:
- SDK 8
- Module: bigfuzz-dfcoverage
- Main method class: BigFuzzPlusDriver 

  The following program arguments can be given:
1. Benchmark driver class.
2. Benchmark test method in driver class.
3. Mutation testing approach. Should be set to SystematicMutation, other options were made by colleagues and are not supported for this repository.  
4. Number of trials. 
5. Whether to exhaustively explore all columns.
6. Mutation tree depth.
  
Example for SalaryAnalysis benchmark P11:  
- SalaryAnalysisDriver testSalaryAnalysis SystematicMutation 10000 true 4
  
For an explanation of the parameters, read the accompanying research paper. 

Author: 
Lars van Koetsveld van Ankeren, Technical University Delft.
Supervisor:
Burcu Kulahcioglu Ozkan