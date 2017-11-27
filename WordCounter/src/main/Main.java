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

public class Main {

    public static void main(String[] args) {
        //open input and output streams
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        //Get input from user
        String filename;
        out.println("Enter name of input file: ");
        filename = in.nextLine();

        //Read from file to list
        out.print("Beginning reading from file...");
        Sequence<String> pRawWordList = CWordCounter
                .separateWordsFromFile(filename, " ,.-\n\t");

        //Sort the words
        sortWordList(pRawWordList);
        out.println("Finished!");

        //Calculate results & write to file
        String title = "Words Counted in " + filename;
        out.println("Enter name of output file: ");
        filename = in.nextLine();
        CWordCounter.outputToHtml(pRawWordList, filename, title);

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

    static class CPopularityOrder
            implements Comparator<Map.Pair<String, Integer>> {

        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            if (o1.key().equals(o2.key())) {
                return 0;
            } else {
                return o1.value() < o2.value() ? -1 : 1;
            }
        }

    }

    public static final CPopularityOrder POPULARITY_ORDER = new CPopularityOrder();

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

        sortWordList(pWordList);

        return pWordList;
    }

}
