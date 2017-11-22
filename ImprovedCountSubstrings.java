/*
 * Dominik Schmidtlein
 * 100946295
 * March 29, 2015
 */
 
 /*Building off of the original countsubstrings class. Design 2 new search algorithms
 for better performance in comparisson to the findBrute approach. The new approaches are based
 off of the boyer-moore search and the KMP search algorithms. */
 
import java.io.*;
import java.util.*;

public class ImprovedCountSubstrings {

    /*
     * Returns the lowest index at which substring pattern begins in text (or
     * else -1).
     */

    private static int findBrute(List<Character> text, List<Character> pattern) {
        int n = text.size();
        int m = pattern.size();
        for (int i = 0; i <= n - m; i++) { // try every starting index within
                                            // text
            int k = 0; // k is index into pattern
            while (k < m && text.get(i + k) == pattern.get(k))
                // kth character of pattern matches
                k++;
            if (k == m) // if we reach the end of the pattern,
                return i; // substring text[i..i+m-1] is a match
        }
        return -1; // search failed
    }
    
    private static int findBoyerMoore(List<Character> text, List<Character> pattern) {
        HashMap<Character, Integer> table = new HashMap<Character, Integer>(pattern.size() + 1, 1);
        for(int i = 0; i < pattern.size() - 1; i++){
            table.put(pattern.get(i), pattern.size() - i - 1);
        }
        if(!table.containsKey(pattern.get(pattern.size() - 1))){
            table.put(pattern.get(pattern.size() - 1), pattern.size());
        }
        // Table is complete
        for(int i = 0; i <= text.size() - pattern.size(); i ++){
            for(int j = 0; j < pattern.size() ; j++){
                if(text.get(i + pattern.size() - j - 1).equals(pattern.get(pattern.size() - j - 1))){
                    if(j == pattern.size() - 1){
                        return i;
                    } 
                }
                else{
                    if(table.get(text.get(i + pattern.size() - 1)) != null){
                        i += table.get(text.get(i + pattern.size() - 1));
                    }
                    else{
                        i += pattern.size();    
                    }
                    i--;
                    break;
                }
            }
        }
        return -1;
    }
    
    public static int findKMP(List<Character> text, List<Character> pattern) {
        ArrayList<Integer> failureFunction = new ArrayList<Integer>(pattern.size());
        failureFunction.add(0);
        int j = 0;
        for(int i = 1; i < pattern.size(); i++){
            if(pattern.get(i).equals(pattern.get(j))){
                  j++;
                  failureFunction.add(j);
            }
            else{
                if(j > 0){
                    j = failureFunction.get(j-1);
                    i--;
                }
                else{
                    failureFunction.add(0);
                }
            }
        }
        //failure function complete
        j = 0;
        for(int i = 0; i < text.size(); i++){
            if(text.get(i).equals(pattern.get(j))){
                if(j == pattern.size() - 1){
                    return i - j;
                }
                else{
                    j++;
                }
            }
            else{
                if(j > 0){
                    j = failureFunction.get(j-1);
                    i--;
                }
            }
        }
        return -1;
    }

    /*
     * Repeatedly prompt user for filename until a file with such a name exists
     * and can be opened.
     */

    private static String openFile() {

        BufferedReader keyboardReader = new BufferedReader(
                new InputStreamReader(System.in));

        String inFilePath = "";
        BufferedReader inFileReader;
        boolean pathsOK = false;

        while (!pathsOK) {
            try {
                System.out.print("Please enter the path for the input file: ");
                inFilePath = keyboardReader.readLine();
                inFileReader = new BufferedReader(new FileReader(inFilePath));
                pathsOK = true;
                inFileReader.close();
            } // try
            catch (IOException e) {
                System.out.println(e);
            } // catch I/O exception
        } // while
        return inFilePath;
    } // method openFiles

    /*
     * Helper method to convert a string to a List. Loops over all characters in
     * the string and may not be all that efficient - may be better to read in
     * the file character by character until we hit whitespace.
     */

    private static void convertStringToList(String in, List<Character> out) {
        char[] input_chars = in.toCharArray();
        out.clear();
        for (int i = 0; i < input_chars.length; i++) {
            out.add(input_chars[i]);
        }
    }

    /*
     * Iterate over all strings in input file to determine whether the input
     * string is a substring in any of these strings. Returns the number of
     * times such a match exists.
     */

    public static int readAndMatchDocument(String filename,
            List<Character> pattern, List<Character> Input)
            throws FileNotFoundException {
        StringTokenizer tokens;
        String line, textword;
        int count = 0;
        // open file anew to ensure we start at the first character
        BufferedReader inFileReader = new BufferedReader(new FileReader(
                filename));

        try {
            while (true) {
                line = inFileReader.readLine();
                if (line == null)
                    break;
                tokens = new StringTokenizer(line);
                // for all the words in the line
                while (tokens.hasMoreTokens()) {
                    textword = tokens.nextToken();
                    convertStringToList(textword, Input);
                    if (findBrute(Input, pattern) != -1)
                        count = count + 1;
                } // end while tokens
            } // end while true
        } catch (IOException e) {
            System.out.println(e);
        } // catch I/O exception}
        try {
            inFileReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return count;
    }
    
    /*
     * Iterate over all strings in input file to determine whether the input
     * string is a substring in any of these strings. Returns the number of
     * times such a match exists.
     */

    public static int readAndMatchBoyerMoore(String filename,
            List<Character> pattern, List<Character> Input)
            throws FileNotFoundException {
        StringTokenizer tokens;
        String line, textword;
        int count = 0;
        // open file anew to ensure we start at the first character
        BufferedReader inFileReader = new BufferedReader(new FileReader(
                filename));

        try {
            while (true) {
                line = inFileReader.readLine();
                if (line == null)
                    break;
                tokens = new StringTokenizer(line);
                // for all the words in the line
                while (tokens.hasMoreTokens()) {
                    textword = tokens.nextToken();
                    convertStringToList(textword, Input);
                    if (findBoyerMoore(Input, pattern) != -1)
                        count = count + 1;
                } // end while tokens
            } // end while true
        } catch (IOException e) {
            System.out.println(e);
        } // catch I/O exception}
        try {
            inFileReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return count;
    }
    
    /*
     * Iterate over all strings in input file to determine whether the input
     * string is a substring in any of these strings. Returns the number of
     * times such a match exists.
     */

    public static int readAndMatchKMP(String filename,
            List<Character> pattern, List<Character> Input)
            throws FileNotFoundException {
        StringTokenizer tokens;
        String line, textword;
        int count = 0;
        // open file anew to ensure we start at the first character
        BufferedReader inFileReader = new BufferedReader(new FileReader(
                filename));

        try {
            while (true) {
                line = inFileReader.readLine();
                if (line == null)
                    break;
                tokens = new StringTokenizer(line);
                // for all the words in the line
                while (tokens.hasMoreTokens()) {
                    textword = tokens.nextToken();
                    convertStringToList(textword, Input);
                    if (findKMP(Input, pattern) != -1)
                        count = count + 1;
                } // end while tokens
            } // end while true
        } catch (IOException e) {
            System.out.println(e);
        } // catch I/O exception}
        try {
            inFileReader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return count;
    }

    public static void main(String[] args) {
        BufferedReader keyboardReader = new BufferedReader(
                new InputStreamReader(System.in));

        ArrayList<Character> pattern1 = new ArrayList<Character>();
        LinkedList<Character> pattern2 = new LinkedList<Character>();

        ArrayList<Character> input_string1 = new ArrayList<Character>();
        LinkedList<Character> input_string2 = new LinkedList<Character>();

        String file_name = openFile();
        String input = new String();

        // read in substring pattern, catching any exceptions
        try {
            while (true) {
                System.out.print("Enter the pattern to look for: ");
                input = keyboardReader.readLine();
                break;
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        convertStringToList(input, pattern1);
        convertStringToList(input, pattern2);

        // finally run the program and measure execution time
        try {
            double startTime = System.currentTimeMillis();
            int final_count1 = readAndMatchDocument(file_name, pattern1, input_string1);
            double middleTime = System.currentTimeMillis();
            int final_count2 = readAndMatchDocument(file_name, pattern2, input_string2);
            double endTime = System.currentTimeMillis();
            System.out.println("Using ArrayLists: " + final_count1 + " matches, derived in " + (middleTime - startTime) + " milliseconds.");
            System.out.println("Using LinkedLists: " + final_count2 + " matches, derived in " + (endTime - middleTime) + " milliseconds.");
                   
            //Boyer Moore
            input_string1.clear();
            input_string2.clear();
            System.out.println("Using BoyerMoore to search for substring");
            startTime = System.currentTimeMillis();
            int boyerMooreA = readAndMatchBoyerMoore(file_name, pattern1, input_string1); 
            middleTime = System.currentTimeMillis();
            System.out.println("Using ArrayLists: " + boyerMooreA + " matches, derived in " + (middleTime - startTime) + " milliseconds.");
            int boyerMooreL = readAndMatchBoyerMoore(file_name, pattern2, input_string2);
            endTime = System.currentTimeMillis();
            System.out.println("Using LinkedLists: " + boyerMooreL + " matches, derived in " + (endTime - middleTime) + " milliseconds.");   
            
            //KMP
            input_string1.clear();
            input_string2.clear();
            System.out.println("Using KMP to search for substring");
            startTime = System.currentTimeMillis();
            int KMPA = readAndMatchKMP(file_name, pattern1, input_string1); 
            middleTime = System.currentTimeMillis();
            System.out.println("Using ArrayLists: " + KMPA + " matches, derived in " + (middleTime - startTime) + " milliseconds.");
            int KMPL = readAndMatchKMP(file_name, pattern2, input_string2);
            endTime = System.currentTimeMillis();
            System.out.println("Using LinkedLists: " + KMPL + " matches, derived in " + (endTime - middleTime) + " milliseconds.");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
