package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

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
	public static void main(String[] args) throws IOException {
		// open input and output streams
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		// Get input from user
		String filename;
		System.out.println("Enter name of input file: ");
		filename = in.readLine();

		// get number of words from user
		int numWords;
		System.out.println("How many words in outputed file: ");
		numWords = Integer.parseInt(in.readLine());
		Reporter.assertElseFatalError(numWords >= 1, "Number of words must be greater than 0");

		// Read from file to list
		System.out.print("Beginning reading from file...");

		ArrayList<String> pRawWordList = CWordCounter.separateWordsFromFile(filename, " \t\n\r,-.!?[]';:/()");
		Reporter.assertElseFatalError(pRawWordList.length() > 0, "Text file must not be empty of words");

		// Sort the words
		Map<String, Integer> pWordCounts = CWordCounter.wordCountMap(pRawWordList);
		ArrayList<String> pWordList = sortWordListAlphabeticalMostPop(pWordCounts, numWords);
		System.out.println("Finished!");

		// Calculate results & write to file
		// Top 100 words in data/importance.txt
		String title = "Top " + numWords + " words in " + filename;
		System.out.println("Enter name of output file: ");
		filename = in.readLine();
		CWordCounter.outputToHtml(pWordList, pWordCounts, filename, title);

		System.out.println("Finished writing to " + filename);

		// close input and output streams
		in.close();
		System.out.close();

	}

	/**
	 * Given a list of words, puts it into alphabetical order, whic A, B C etc.
	 * coming at the beginning of the sequence
	 *
	 * @param pWordList
	 *            - the sequence to sort
	 */
	public static void sortWordList(ArrayList<String> pWordList) {
		// just use the sorting machine... ughh
		Comparator<String> pOrder = String.CASE_INSENSITIVE_ORDER;
		pWordList.sort(pOrder);
	}

	/**
	 * Compares the integer values of a Pair<String, Integer> .
	 *
	 * @author Michael Trunk
	 */
	static class CPopularityOrder implements Comparator<Map.Entry<String, Integer>> {

		@Override
		public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
			if (o1.getKey().equals(o2.getKey())) {
				return 0;
			} else {
				return o1.getValue() < o2.getValue() ? 1 : -1;
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
	public static ArrayList<String> sortWordListAlphabeticalMostPop(Map<String, Integer> pWordCounts,
			int amountFirstWords) {
		ArrayList<Map.Entry<String, Integer>> mapList = new ArrayList<>();
		// dump it into the black box
		Set<Map.Entry<String, Integer>> entries = pWordCounts.entrySet();
		for (Map.Entry<String, Integer> currPair : entries) {
			mapList.add(currPair);
		}
		mapList.sort(POPULARITY_ORDER);
		ArrayList<String> pWordList = new ArrayList<>();
		// now take it all back out
		int i = 0;
		while (mapList.size() > 0 & i < amountFirstWords) {
			pWordList.add(pWordList.size(), mapList.remove(0).getKey());
			i++;
		}

		// now sort our result alphabetically
		sortWordList(pWordList);

		return pWordList;
	}

}
