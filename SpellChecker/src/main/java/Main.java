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
            System.out.println("Вы ввели слово правильно");
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
            System.out.println("Либо введенного слова нет в словаре, либо количество ошибок превышает половину букв в слове");
        }
        else {
            printSimilarWords(similarWords);
        }
    }

    public static List<String> filter(Map<Integer, String> map, int lengthOfEnteredWord, int numberOfPossibleMistakes) {
        return map.entrySet().stream()
                .flatMap(s -> Stream.of(s.getValue()))
                .filter(s -> Math.abs(s.length() - lengthOfEnteredWord) <= numberOfPossibleMistakes)
                .collect(Collectors.toList());
    }

    public static Scanner chooseDictionary(String word) throws FileNotFoundException {
        if (word.matches(".*[a-z].*")) {
            return new Scanner(new File("Dictionary.txt"));
        }
        return new Scanner(new File("RussianDictionary.txt"));
    }

    public static void readWordsFromDictionary(Map<Integer, String> dictionary, Scanner scanner) {
        while (scanner.hasNextLine()) {
            String wordFromDictionary = scanner.nextLine();
            dictionary.put(wordFromDictionary.hashCode(), wordFromDictionary);
        }
    }

    public static boolean isRightWord(Map<Integer, String> dictionary, String word) {
        int hashCode = word.hashCode();
        return dictionary.containsKey(hashCode) && dictionary.get(hashCode).equals(word);
    }

    public static int countPossibleMistakes(String word) {
        return word.length() / 5 + 1;
    }

    public static void printSimilarWords(List<String> list) {
        System.out.println("Похожие слова : ");
        for (String word : list) {
            System.out.println(word);
        }
    }

    public static void findSimilarWords(List<String> dictionary, List<String> similarWords, String word, int numberOfPossibleMistakes) {
        for (String wordFromDictionary : dictionary) {
            if (DamerauLevenshtein.calculateDistance(wordFromDictionary, word) <= numberOfPossibleMistakes) {
                similarWords.add(wordFromDictionary);
            }
        }
    }
}
