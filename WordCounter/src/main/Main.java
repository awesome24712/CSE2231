package main;

import java.util.Comparator;

import components.map.Map;
import components.map.Map.Pair;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine1L;
import components.utilities.Reporter;

/**
 * Utility class contains main function and sorting functions for generating a
 * tag cloud from a given text.
 *
 * @author Michael Trunk, Khalid Musa, Milt Levy
 *
 */
public final class Main {

    /**
     * Private default constructor prevents instantiation.
     */
    private Main() {
    }

    /**
     * Main function manages user input and dispatches jobs.
     *
     * @param args
     *            - command line arguments.
     */
    public static void main(String[] args) {
        //open input and output streams
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        //Get input from user
        String filename;
        out.println("Enter name of input file: ");
        filename = in.nextLine();

        //get number of words from user
        int numWords;
        out.println("How many words in outputed file: ");
        numWords = in.nextInteger();
        Reporter.assertElseFatalError(numWords >= 1,
                "Number of words must be greater than 0");

        //Read from file to list
        out.print("Beginning reading from file...");
        Sequence<String> pRawWordList = CWordCounter
                .separateWordsFromFile(filename, " \t\n\r,-.!?[]';:/()");
        Reporter.assertElseFatalError(pRawWordList.length() > 0,
                "Text file must not be empty of words");

        //Sort the words
        Map<String, Integer> pWordCounts = CWordCounter
                .wordCountMap(pRawWordList);
        Sequence<String> pWordList = sortWordListAlphabeticalMostPop(
                pWordCounts, numWords);
        out.println("Finished!");

        //Calculate results & write to file
        // Top 100 words in data/importance.txt
        String title = "Top " + numWords + " words in " + filename;
        out.println("Enter name of output file: ");
        filename = in.nextLine();
        CWordCounter.outputToHtml(pWordList, pWordCounts, filename, title);

        out.println("Finished writing to " + filename);

        //close input and output streams
        in.close();
        out.close();
    }

    /**
     * Given a list of words, puts it into alphabetical order, whic A, B C etc.
     * coming at the beginning of the sequence
     *
     * @param pWordList
     *            - the sequence to sort
     */
    public static void sortWordList(Sequence<String> pWordList) {
        //just use the sorting machine... ughh
        Comparator<String> pOrder = String.CASE_INSENSITIVE_ORDER;
        SortingMachine<String> pSorter = new SortingMachine1L<String>(pOrder);

        //dump it into the black box
        while (pWordList.length() > 0) {
            pSorter.add(pWordList.remove(0));
        }

        pSorter.changeToExtractionMode();

        //now take it all back out
        while (pSorter.size() > 0) {
            pWordList.add(pWordList.length(), pSorter.removeFirst());
        }
    }

    /**
     * Compares the integer values of a Pair<String, Integer> .
     *
     * @author Michael Trunk
     */
    static class CPopularityOrder
            implements Comparator<Map.Pair<String, Integer>> {

        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            if (o1.key().equals(o2.key())) {
                return 0;
            } else {
                return o1.value() < o2.value() ? 1 : -1;
            }
        }

    }

    /**
     * Global ordering object.
     */
    public static final CPopularityOrder POPULARITY_ORDER = new CPopularityOrder();

    /**
     * Given a map, returns an alphabetically ordered list of the given length
     * whose elements are the most common strings appearing in the given map.
     *
     * @param pWordCounts
     *            - map of words to their counts
     * @param amountFirstWords
     *            - the amount of most popular words to put into the returned
     *            Sequence
     * @return - the sequence of most common words
     */
    public static Sequence<String> sortWordListAlphabeticalMostPop(
            Map<String, Integer> pWordCounts, int amountFirstWords) {

        SortingMachine<Map.Pair<String, Integer>> pSorter = new SortingMachine1L<>(
                POPULARITY_ORDER);

        //dump it into the black box
        for (Map.Pair<String, Integer> currPair : pWordCounts) {
            pSorter.add(currPair);
        }
        pSorter.changeToExtractionMode();
        Sequence<String> pWordList = new Sequence1L<>();
        //now take it all back out
        int i = 0;
        while (pSorter.size() > 0 & i < amountFirstWords) {
            pWordList.add(pWordList.length(), pSorter.removeFirst().key());
            i++;
        }

        //now sort our result alphabetically
        sortWordList(pWordList);

        return pWordList;
    }

}
