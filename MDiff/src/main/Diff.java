package main;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codelibs.minhash.MinHash;

public class Diff {
	private String[] filenames;
	private String[] files;
	private String[][] filelines;
	private LinkedList<Paragraph> paragraphs = new LinkedList<Paragraph>();
	
	private Tokenizer tokenizer = new WhitespaceTokenizer();
	private Analyzer analyzer;
	
	public Diff(String[] filenames, String[] files) {
		this.filenames = filenames;
		this.files = files;
		
		int hashBit = 1;
		int seed = 0;
		int num = 128;
		analyzer = MinHash.createAnalyzer(tokenizer, hashBit, seed, num);
	}
	
	private void processParagraph(int file, String[] lines, int start, int end) {
		String paragraph = "";
		for (int i = start; i < end; i++) {
			paragraph += lines[i] + "\n";
		}
		try {
			byte[] hash = MinHash.calculate(analyzer, paragraph);
			Paragraph p = new Paragraph(file, start, end, hash);
			
			float max = 0;
			Paragraph similar = null;
			for (Paragraph p0 : paragraphs) {
				float similarity = p.compare(p0);
				if (similarity > max) {
					max = similarity;
					similar = p0;
				}
			}
			
			if (max > 0.8) {
				p.setOrigin(similar);
			}
			
			paragraphs.addFirst(p);
		} catch (Exception e) {}
	}
	
	public void process() {
		filelines = new String[files.length][];
		for (int i = 0; i < files.length; i++) {
			filelines[i] = files[i].split("\\r?\\n");
			if (filelines.length == 0) continue;
			String[] lines = new String[filelines[i].length];
			for (int j = 0; j < lines.length; j++) {
				lines[j] = filelines[i][j].trim();
			}
			int start = 0;
			boolean paragraph = !lines[0].equals("");
			for (int j = 1; j < lines.length; j++) {
				if (lines[j].equals("")) {
					if (paragraph) {
						processParagraph(i, lines, start, j);
						paragraph = false;
					}
				} else {
					if (!paragraph) {
						start = j;
						paragraph = true;
					}
				}
			}
			if (paragraph) {
				processParagraph(i, lines, start, lines.length);
			}
		}
		files = null;
	}
	
	public void outputHTML() {
		String[] output = new String[filenames.length];
		Iterator<Paragraph> iter = paragraphs.descendingIterator();
		while (iter.hasNext()) {
			Paragraph p = iter.next();
			int f = p.getFile();
			if (output[f] == null) output[f] = "<h3>" + filenames[f] + "</h3><div class=\"mdiffinner\">";
			String h = "<pre id=\"mdiff" + f + "-" + p.getStart() + "\" class=\"mdiffp\"";
			Paragraph o = p.getOrigin();
			if (o != null) h += " data-origin-file=\"" + o.getFile() + "\" data-origin-line=\"" + o.getStart() + "\"";
			h += ">";
			for (int i = p.getStart(); i < p.getEnd(); i++) {
				h += StringEscapeUtils.escapeHtml4(filelines[f][i]) + "\n";
			}
			h += "</pre>";
			output[f] += h;
		}
		for (int i = 1; i < filenames.length; i++) {
			output[i] += "</div>";
		}
		
		for (int i = 1; i < filenames.length; i++) {
			String path = filenames[i] + ".html";
			try {
				Files.copy(new File("template.html").toPath(), new File(path).toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {}
			try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
				for (int j = 1; j < filenames.length; j++) {
					w.print("<div id=\"mdiff" + j + "\" class=\"mdiffleft\", style=\"display: none;\">");
					w.print(output[j]);
					w.print("</div>");
				}
				w.print("</div><div class=\"mdiffcolumn\"><div class=\"mdiffright\">");
				w.print(output[i]);
				w.print("</div></div></body></html>");
			} catch (Exception e) {}
		}
	}
}
