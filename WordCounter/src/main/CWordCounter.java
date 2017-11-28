package main;

import components.map.Map;
import components.map.Map1L;
import components.sequence.Sequence;
import components.sequence.Sequence1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

/**
 * Contains simple utilities for separating and counting words.
 *
 * @author Michael Trunk, Khalid Musa, Milt Levy
 *
 */
public final class CWordCounter {

    /**
     * Default constructor prevents instantiation.
     */
    private CWordCounter() {
    }

    /**
     * Counts the number of time a String appears in a given sequence.
     *
     * @param pWordList
     *            - the sequence of Strings.
     * @return - a String-to-Integer map.
     */
    public static Map<String, Integer> wordCountMap(
            Sequence<String> pWordList) {
        assert pWordList != null : "pWordList is non-null";

        //Declare result
        Map<String, Integer> pResultMap = new Map1L<String, Integer>();

        //Iterate through all words
        for (int i = 0; i < pWordList.length(); i++) {
            String curWord = pWordList.entry(i);
            //Add to the count if we already saw the word,
            //count its appearances and register it otherwise
            if (!pResultMap.hasKey(curWord)) {
                int count = countWord(pWordList, curWord);
                pResultMap.add(curWord, count);
            }
        }

        return pResultMap;
    }

    /**
     * Counts the number of times the given word appears in the list.
     *
     * @param pWordList
     *            - the list
     * @param pWord
     *            - the word to look for
     * @return - the count
     */
    public static int countWord(Sequence<String> pWordList, String pWord) {
        int count = 0;

        for (int i = 0; i < pWordList.length(); i++) {
            if (pWordList.entry(i).equals(pWord)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Given a filename, splits tokens from the file into the returned sequence.
     *
     * @param filename
     *            - the name of the file to open
     * @param separators
     *            - characters which separate tokens. These are excluded from
     *            the output.
     * @return - the sequence of tokens
     */
    public static Sequence<String> separateWordsFromFile(String filename,
            String separators) {
        Sequence<String> pResultSequence = new Sequence1L<String>();
        SimpleReader pFileReader = new SimpleReader1L(filename);

        while (!pFileReader.atEOS()) {
            StringBuffer nextWord = new StringBuffer();

            //Go one character at a time, checking for separators
            while (!pFileReader.atEOS()
                    && separators.indexOf(pFileReader.peek()) == -1) {
                nextWord.append(Character.toLowerCase(pFileReader.read()));
            }

            //remove the separator we reached
            if (!pFileReader.atEOS()) {
                pFileReader.read();
            }

            //this catches multiple separators in a row
            if (nextWord.length() > 0) {
                pResultSequence.add(0, nextWord.toString());
            }
        }

        pFileReader.close();

        return pResultSequence;
    }

    /**
     * Given a sorted list of words, a filename, and a title, outputs results of
     * word counts to file.
     *
     * @param pWordList
     *            - the list of words
     * @param pWordCounts
     *            - map of words to their counts
     * @param filename
     *            - the fully-defined path and name of file
     * @param title
     *            - the title of the HTML document, as it's placed in the
     *            {@code <title>} tag and in the text itself.
     */
    public static void outputToHtml(Sequence<String> pWordList,
            Map<String, Integer> pWordCounts, String filename, String title) {
        //Open the CHtmlWriter
        //Ignore the warning, it's in fact closed by closeBodyAndStream()
        @SuppressWarnings("resource")
        CHtmlWriter pOut = new CHtmlWriter(filename, title);

        //Add an on-screen title
        pOut.printlnNested(title, "h2");
        pOut.printHorizontalLine();

        pOut.println("<div class = \"cdiv\">");
        pOut.println("<p class = \"cbox\">");

        //Use a map to keep track of what words we've done already...
        //I tried to iterate through the map but it doesn't maintain order! >:(
        //And sequence doesn't have any contains(...) method, so...
        Map<String, Boolean> pSeenWords = new Map1L<>();
        int maxCount = 0;
        for (Map.Pair<String, Integer> wordPair : pWordCounts) {
            if (wordPair.value() > maxCount) {
                maxCount = wordPair.value();
            }
        }
        int minCount = maxCount;
        for (Map.Pair<String, Integer> wordPair : pWordCounts) {
            if (wordPair.value() < minCount) {
                minCount = wordPair.value();
            }
        }

        final float MAX_SIZE = 7.0f;
        for (String pWord : pWordList) {
            if (!pSeenWords.hasKey(pWord)) {
                pOut.printSized(pWord,
                        (int) (MAX_SIZE * (pWordCounts.value(pWord) - minCount)
                                / (maxCount - minCount) + 1));
                pSeenWords.add(pWord, true);
            }
        }

        pOut.println("</p>");
        pOut.println("</div>");

        pOut.closeBodyAndStream();
    }
}
