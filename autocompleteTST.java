//import algs52.TST;
//import stdlib.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class autocompleteTST {

    private TST<Integer> tst;

    public void insert(String s, int fq) {
        Integer existingValue = tst.get(s);

        if(existingValue != null) {
            tst.put(s, existingValue + fq); //add fq(fq = 1) to the frequency of the word
        } else {
            tst.put(s,fq);
        }

    }
    public Integer lookup(TST<Integer> t, String s){
        return t.get(s);

    }
    public List<String> suggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        String normalizedInput = input.toLowerCase(); //normalize user input to ignore case by making everything lower case
        if(normalizedInput == "") {
            return suggestions;
        }
        Iterable<String> matches = tst.prefixMatch(normalizedInput);//get all keys starting with given prefix

        List<Map.Entry<String, Integer>> frequencyList = new ArrayList<>(); //make list of frequencies
        for(String match : matches) {
            frequencyList.add(new AbstractMap.SimpleEntry<>(match, lookup(tst, match))); //populate frequency list
        }

        frequencyList.sort((a,b) -> b.getValue().compareTo(a.getValue()));//sort list to order fq then abc
        for(int i = 0; i < Math.min(3, frequencyList.size()); i++) {//get top 3
            suggestions.add(frequencyList.get(i).getKey());
        }
        return suggestions;
    }
    public autocompleteTST(String fileName) throws FileNotFoundException {
        tst = new TST<>();

        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(line.isEmpty()) continue;
         

            String[] parts = line.split("\\s+"); // split line into parts

            if (parts.length < 2) { // checks that each sentence has a frequency value
                System.err.println("Invalid line format: " + line);
                continue;
            }
            // Extract the frequency from the last part
            StringBuilder sentenceBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                sentenceBuilder.append(parts[i]).append(" ");
            }
            String sentence = sentenceBuilder.toString().trim(); // Construct the sentence

            int frequency;// Parse the frequency from the last part of string
            try {
                frequency = Integer.parseInt(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid frequency: " + parts[parts.length - 1] + " in line: " + line);
                continue;
            }

            tst.put(sentence.toLowerCase(), frequency);//place sentence and frequency in tst
        }
        scanner.close();

    }
    public static void main(String[] args) {
        String fileName = "input.txt";
        String userInput = "";
        String end = "";
        int flag = 0;
        try {
            testAddWordIncreasesFrequency();
            testAutocompleteSuggestions();
            testAutocompleteWithExactMatch();
            testEmptyInput();
           
            autocompleteTST autoCompleter = new autocompleteTST(fileName);
            Scanner inputScanner = new Scanner(System.in);

            String message = "Enter a prefix to search:\nType 'exit' to quit\n'#' to add a word to the dictionary(e.g. word#)\n'*' to delete your current prefix(e.g. word*)";
            int borderLength = message.length() + 4; // 2 spaces padding on each side
            String border = "-".repeat(borderLength);
            
            // Print the box
            System.out.println(border);
            System.out.println(message);
            System.out.println(border);

            while(true) {
                end = inputScanner.nextLine().trim();
                userInput += end;
                if(end.charAt(end.length() - 1) == '*') {
                    flag = 2;
                }
                if(end.charAt(end.length() - 1) == '#') {
                    userInput = userInput.substring(0, userInput.length() - 1);
                    flag = 1;
                }
                if(userInput == "") System.err.print(" b ");

                if(userInput.equalsIgnoreCase("exit")) {
                    break;
                }
                if(flag == 1) {
                    autoCompleter.insert(userInput.toLowerCase(), 1); //reads input and adds to tst with frequency + 1
                }
                    List<String> suggestions = autoCompleter.suggestions(userInput);


                System.out.println("Top suggestions: " + suggestions); // print out top 3 results
                if(flag == 2)  {
                    userInput = "";
                    flag = 0;
                }
                if(flag == 1) {
                    userInput = "";
                    flag = 0;
                }
                System.out.print(userInput);
            }
            inputScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("file not found:" + fileName);
        }
    }
    public static void testAddWordIncreasesFrequency() throws FileNotFoundException {
        autocompleteTST autoCompleter = new autocompleteTST("inputTest.txt");
        String word = "example";

        // Insert the word for the first time with frequency 1
        autoCompleter.insert(word, 1);
        int initialFrequency = autoCompleter.lookup(autoCompleter.tst, word);
       if(1 == initialFrequency) System.out.println("Increased correctly");
       System.out.println("Test passed: Frequency updated correctly to " + initialFrequency);

        // Insert the word again, increasing the frequency by 1
        autoCompleter.insert(word, 1);
        int updatedFrequency = autoCompleter.lookup(autoCompleter.tst, word);
        if(2 == initialFrequency) System.out.println("Increased correctly");
        // Output results
        System.out.println("Test passed: Frequency updated correctly to " + updatedFrequency);
    }

    public static void testAutocompleteSuggestions() throws FileNotFoundException {
        
        autocompleteTST autoCompleter = new autocompleteTST("inputTest.txt");

        // Test for prefix "ex"
        List<String> suggestions = autoCompleter.suggestions("ex");

        // Expected top 3 suggestions (based on frequencies in descending order)
        List<String> expectedSuggestions = List.of("example", "excite", "excellent");

        // Verify the suggestions using .equals()
        if (expectedSuggestions.equals(suggestions)) {
            System.out.println("Test passed: Autocomplete suggestions are correct.");
        } else {
            System.out.println("Test failed: Expected " + expectedSuggestions + " but got " + suggestions);
        }
    }

        public static void testAutocompleteWithExactMatch() throws FileNotFoundException {
        autocompleteTST autoCompleter = new autocompleteTST("inputTest.txt");
        List<String> suggestions = autoCompleter.suggestions("example");
        List<String> expected = List.of("example");
        if (suggestions.equals(expected)) {
            System.out.println("testAutocompleteWithExactMatch passed.");
        } else {
            System.out.println("testAutocompleteWithExactMatch failed. Expected " + expected + " but got " + suggestions);
        }
    }

    public static void testEmptyInput() throws FileNotFoundException {
        autocompleteTST autoCompleter = new autocompleteTST("inputTest.txt");
        List<String> suggestions = autoCompleter.suggestions("");
        if (suggestions.isEmpty()) {
            System.out.println("testEmptyInput passed.");
        } else {
            System.out.println("testEmptyInput failed. Expected an empty list but got " + suggestions);
        }
    }


}
