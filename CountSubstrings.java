/*Dominik Schmidtlein February 11th, 2015*/

/* Compare same search algorithm when base datatype is ArrayList vs LinkedList.
The arraylist is consistently faster because .get is a constant time, O(1) operation 
for an arraylist, however it is O(n) for a linked list because you have to iterate through
the linked list until n, instead of using the effective address. */

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.util.LinkedList;


public class CountSubstrings
{
    String fileLocation; //input of file name
    String searchWord; //word to search text for
    int count; //the number of hits found in text
    
    public CountSubstrings(String fileLocation, String searchWord){ //contructor, initialize the class variable
        this.fileLocation = fileLocation;
        this.searchWord = searchWord;
    }
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Please enter the path for the input file: ");
        String fileName = sc.nextLine(); // get user input for file name
        System.out.print("Enter the pattern to look for: ");
        String searchString = sc.nextLine(); //get user input for word to be searched for
        
        CountSubstrings cs = new CountSubstrings(fileName, searchString); //create instance of class
        cs.countSubstringArray(); //call counter using arrays
        cs.countSubstringLinked(); //call counter using linked lists
    }
    
    public void countSubstringArray(){
        double startTime = System.currentTimeMillis(); //record start time of calculation
        count = 0; //initialize number of hits to 0
        ArrayList<Character> word = this.toArrayList(searchWord); //create char arraylist of search word
        Scanner fileScanner = new Scanner(System.in); //initiate scanner to avoid "may not have been initialized" error
        try{
            fileScanner = new Scanner(new File(fileLocation)); //update scanner to look at file
        }catch(Exception e){
            System.out.println("File not found"); //if inputted file name is invalid, quit
            return;
        }
        while(fileScanner.hasNext()){ //iterate through entire document while more is available
            String s = fileScanner.nextLine(); //extract next line of file as string
            int index = findBrute(this.toArrayList(s), word); //look for first occurence of the word in the line, line is converted to arraylist
            while(index != -1){ //find multiple locations of word in one line
                count +=1; //increment count for every hit
                s = s.substring(index + searchWord.length()); //continue searching line for hits without the first hit
                index = findBrute(this.toArrayList(s), word); //look for hits in remaining string
            }
        }
        double endTime = System.currentTimeMillis(); //record finish time 
        System.out.println("Using ArrayLists: " + count + " matches, derived in " + (endTime - startTime) + " milliseconds.");
    }
    
    public void countSubstringLinked(){
        double startTime = System.currentTimeMillis();//record start time of calculation
        count = 0;
        LinkedList<Character> word = this.toLinkedList(searchWord);
        Scanner fileScanner = new Scanner(System.in);        
         try{
            fileScanner = new Scanner(new File(fileLocation));
        }catch(Exception e){
            System.out.println("File not found");
            return;
        }
        while(fileScanner.hasNext()){
            String s = fileScanner.nextLine();
            int index = findBrute(this.toLinkedList(s), word);
            while(index != -1){
                count +=1;
                s = s.substring(index + searchWord.length());
                index = findBrute(this.toLinkedList(s), word);
            }
        }
        double endTime = System.currentTimeMillis();
        System.out.println("Using LinkedLists: " + count + " matches, derived in " + (endTime - startTime) + " milliseconds.");
    }
    
    private ArrayList<Character> toArrayList(String s){ //convert string into arraylist of chars
        ArrayList<Character> aL = new ArrayList<Character>(s.length());
        for(int i = 0; i < s.length(); i++){
            aL.add(s.charAt(i));
        }
        return aL;
    }
    
    private LinkedList<Character> toLinkedList(String s){ //convert string into linked list of chars
        LinkedList<Character> lL = new LinkedList<Character>();
        for(int i = 0; i < s.length(); i++){
            lL.add(s.charAt(i));
        }
        return lL;
    }
    
    private int findBrute(List<Character> text, List<Character> pattern) {
        int n = text.size(); //size of text that will be searched
        int m = pattern.size(); //size of word that is being searched for
        for (int i = 0; i <= n - m; i++) { //loop through text, stopping when the number of characters left is too small to contain the pattern
            int k = 0; //offset from stop of word
            while (k < m && text.get(i + k) == pattern.get(k)) // loop through characters after starting character
                k++; //increment offset from start of pattern
            if (k == m) //if 'm' chars match, then the pattern has been found
                return i; //return the index of the start of the first pattern found in the text
        }
        return -1; // return -1 if there is no match
    }

}
