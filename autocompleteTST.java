//import algs52.TST;
import stdlib.*;

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

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
            autocompleteTST autoCompleter = new autocompleteTST(fileName);
            Scanner inputScanner = new Scanner(System.in);

            System.out.println("Enter a prefix to search:\nType 'exit' or 'e' to quit\n'#' to add a word to the dictionary(e.g. word#)\n'*' to delete your current prefix(e.g. word*)");
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

                if(userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("e")) {
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

}
