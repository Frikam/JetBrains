import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Map dictionary = new HashMap();
        Scanner in = new Scanner(System.in);

        System.out.print("Enter word : ");
        String word = in.nextLine().toLowerCase();
        int length = word.length();

        Scanner scanner = chooseDictionary(word);

        readWordsFromDictionary(dictionary, scanner);

        if (isRightWord(dictionary, word)) {
            System.out.println("Word is in the dictionary");
            return;
        }

        int numberOfPossibleMistakes = countPossibleMistakes(word);

        List<String> filteredDictionary = filter(dictionary, word.length(), numberOfPossibleMistakes);
        List<String> similarWords = new LinkedList<>();

        while (similarWords.isEmpty() && length / 2 > numberOfPossibleMistakes) {
            findSimilarWords(filteredDictionary, similarWords, word, numberOfPossibleMistakes);
            numberOfPossibleMistakes++;
        }

        if (similarWords.isEmpty()) {
            System.out.println("Either the entered word is not in the dictionary, or the number of errors is not less than half the length of the word");
        }
        else {
            printSimilarWords(similarWords);
        }
    }

    /** A method that adds to the list of words different from the entered no more than number of possible mistakes */
    public static List<String> filter(Map<Integer, String> map, int lengthOfEnteredWord, int numberOfPossibleMistakes) {
        return map.entrySet().stream()
                .flatMap(s -> Stream.of(s.getValue()))
                .filter(s -> Math.abs(s.length() - lengthOfEnteredWord) <= numberOfPossibleMistakes)
                .collect(Collectors.toList());
    }

    /** A method that determines the language of the word and selects the desired dictionary */
    public static Scanner chooseDictionary(String word) throws FileNotFoundException {
        if (word.matches(".*[a-z].*")) {
            return new Scanner(new File("Dictionary.txt"));
        }
        return new Scanner(new File("RussianDictionary.txt"));
    }

    /** A method that adds words from dictionary to hash-table */
    public static void readWordsFromDictionary(Map<Integer, String> dictionary, Scanner scanner) {
        while (scanner.hasNextLine()) {
            String wordFromDictionary = scanner.nextLine();
            dictionary.put(wordFromDictionary.hashCode(), wordFromDictionary);
        }
    }

    /** A method that checks whether there is a word in the dictionary or not */
    public static boolean isRightWord(Map<Integer, String> dictionary, String word) {
        int hashCode = word.hashCode();
        return dictionary.containsKey(hashCode) && dictionary.get(hashCode).equals(word);
    }

    /** A method that calculates number of possible mistakes */
    public static int countPossibleMistakes(String word) {
        return word.length() / 5 + 1;
    }

    /** A method that prints similar words */
    public static void printSimilarWords(List<String> list) {
        System.out.println("Similar words : ");
        for (String word : list) {
            System.out.println(word);
        }
    }

    /** A method that calculates Damerau-Levenshtein distance and
     * if distance less that number of possible mistakes then adds word in list with similar words
     * */
    public static void findSimilarWords(List<String> dictionary, List<String> similarWords, String word, int numberOfPossibleMistakes) {
        for (String wordFromDictionary : dictionary) {
            if (DamerauLevenshtein.calculateDistance(wordFromDictionary, word) <= numberOfPossibleMistakes) {
                similarWords.add(wordFromDictionary);
            }
        }
    }
}
