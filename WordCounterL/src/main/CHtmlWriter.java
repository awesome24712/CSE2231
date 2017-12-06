package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class extends the functionality of PrintWriter by providing extra
 * functions and utilities for opening, closing, and writing to new HTML
 * streams.
 *
 * @author Michael Trunk, Milt Levy, Khalid Musa
 *
 */
public class CHtmlWriter extends PrintWriter {

	/**
	 * Constructor shorthands the encapsulation of other writers and streams. Opens
	 * {@code this} to output to the given file path.
	 *
	 * @param filename
	 *            - the name of the file the stream is to be saved to
	 * @throws IOException
	 *             if the root of the source is not found
	 */
	public CHtmlWriter(String filename) throws IOException {
		super(new BufferedWriter(new FileWriter(filename)));
	}

	/**
	 * Tertiary constructor prepares for writing to file. Implementation only calls
	 * parent constructor and then writes a valid header into the HTML document.
	 * Also opens the {@code <body>} tag.
	 *
	 * @param filename
	 *            - the absolute or relative file path, including file name, to
	 *            write to
	 * @param title
	 *            - the title of the page
	 * @throws IOException
	 *             if the root of the source is not found
	 */
	public CHtmlWriter(String filename, String title) throws IOException {
		super(new BufferedWriter(new FileWriter(filename)));
		this.printHeader(title);
	}

	/**
	 * This is a helper function for printing out a string which is wrapped by
	 * another string formatted into correct HTML tags.
	 *
	 * @param nested
	 *            - the inside text
	 * @param wrapper
	 *            - the outside tag
	 * @requires |wrapper| /= 0 and out.is_open
	 * @updates this.content
	 * @ensures this.content = #this.content * < * wrapper * > * nested * </ *
	 *          wrapper * >
	 */
	public void printlnNested(String nested, String wrapper) {
		this.println('<' + wrapper + '>' + nested + "</" + wrapper + '>');
	}

	/**
	 * This is a helper function for printing out a a body of text which is a link
	 * to an external URL.
	 *
	 * @param text
	 *            - the link's label text
	 * @param url
	 *            - the absolute or relative filepath
	 * @requires |url| /= 0 and |url| /= 0 and out.is_open
	 * @updates this.content
	 * @ensures this.content = #this.content * <a href=" * url * "> * text * </a>
	 */
	public void printLink(String text, String url) {
		this.println(formatLink(text, url));
	}

	/**
	 * This is a helper function which formats a string into a link tag, returning
	 * the result.
	 *
	 * @param text
	 *            - the link's label text
	 * @param url
	 *            - the
	 * @requires |url| /= 0 and |url| /= 0 and out.is_open
	 * @ensures formatLink = <a href=" * url * "> * text * </a>
	 * @return a formatted HTML link
	 */
	public static String formatLink(String text, String url) {
		return "<a href=\"" + url + "\">" + text + "</a>";
	}

	/**
	 * Prints out a valid HTML header into the output stream. The end of this header
	 * opens the {@code <body>} tag.
	 *
	 * @param title
	 *            - the title of the HTML page
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures out.content = #out.content * [a valid HTML header which also opens
	 *          the body tag]
	 */
	public void printHeader(String title) {
		this.println("<DOCTYPE html>\n<html>\n<head>");
		this.printlnNested(title, "title");
		this.println(
				"<link href=\"http://cse.osu.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
		this.println("</head>\n<body>");
	}

	/**
	 * Prints a horizontal line.
	 *
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures this.content = #this.content * {@code <hr/>}
	 */
	public void printHorizontalLine() {
		this.println("<hr/>");
	}

	/**
	 * Opens a table in preparation for printing table rows.
	 *
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures this.content = #this.content * {@code <table border="1">\n<tbody>}
	 */
	public void openTable() {
		this.println("<table border=\"1\">\n<tbody>");
	}

	/**
	 * Prints a table row with the implicitly given cells.
	 *
	 * @param cells
	 *            - the strings to be printed into each cell.
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures this.content = #this.content *
	 *          {@code <tr>\n <td>cells[0]<td> ...  </tr>\n}
	 */
	public void printTableRow(String... cells) {
		this.println("<tr>");
		for (int i = 0; i < cells.length; i++) {
			this.printlnNested(cells[i], "td");
		}
		this.println("</tr>");
	}

	/**
	 * Closes a table in preparation for printing other content.
	 *
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures this.content = #this.content * {@code </tbody>\n</table>}
	 */
	public void closeTable() {
		this.println("</tbody>\n</table>");
	}

	/**
	 * Prints a word with given a size.
	 *
	 * @param word
	 *            The word to be printed
	 * @param size
	 *            The size of the word to be printed
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures this.content = #this.content * (@code <font size = \"" + s + "\">" +
	 *          word + "</font>). The printed size is within the bounds of 1 and 7
	 *          inclusive.
	 */
	public void printSized(String word, int size) {
		int s = size;
		final int MAX_SIZE = 7;
		if (s > MAX_SIZE) {
			s = MAX_SIZE;
		} else if (s < 1) {
			s = 1;
		}
		this.println("<font size = \"" + s + "\">" + word + "</font>");
	}

	/**
	 * Prints out styled text with the given class. On hovering ocer the text, a
	 * message appears.
	 *
	 * @param text
	 *            - the formatted text
	 * @param className
	 *            - the CSS class name
	 * @param hoverText
	 *            - the text which appears when hovering over the text.
	 */
	public void printSpan(String text, String className, String hoverText) {
		this.println("<span style=\"default\" class = \"" + className + "\"" + " title = \"" + hoverText + "\">" + text
				+ "</span>");
	}

	/**
	 * Closes the HTML's {@code <body>} and {@code <html>} tags before closing the
	 * output stream.
	 *
	 * @updates this.content
	 * @requires this.is_open
	 * @ensures [the body and file have been closed] and NOT(out.is_open)
	 */
	public void closeBodyAndStream() {
		this.println("</body>\n</html>");
		this.close();
	}
}
