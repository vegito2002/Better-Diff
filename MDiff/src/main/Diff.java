package main;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
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
				String str1 = getParagraph(similar), str2 = getParagraph(p);
				Pair<boolean[], boolean[]> diff = Distance.processCharacter(str1, str2);
				if (diff == null) {
					p.setSame();
				} else {
					p.setDelete(diff.getLeft());
					p.setAdd(diff.getRight());
				}
			}
			
			paragraphs.addFirst(p);
		} catch (Exception e) {}
	}
	
	private String getParagraph(Paragraph p) {
		String[] lines = filelines[p.getFile()];
		String s = "";
		for (int i = p.getStart(); i < p.getEnd(); i++) {
			s += lines[i] + "\n";
		}
		return s;
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
	
	private String generateParagraphHTML(Paragraph p, boolean[] diff) {
		int f = p.getFile(), pos = 0;
		Paragraph o = p.getOrigin();
		String h = "";
		if (o != null) h += "<h6>From " + filenames[o.getFile()] + ":" + o.getStart() + "</h6>";
		h += "<pre id=\"mdiff" + f + "-" + p.getStart() + "\" class=\"mdiffp";
		if (o != null) {
			h += "\" data-origin-file=\"" + o.getFile() + "\" data-origin-line=\"" + o.getStart() + "\">";
		} else {
			h += " mdiffadded\">";
		}
		for (int i = p.getStart(); i < p.getEnd(); i++) {
			int last = 0;
			boolean current = true;
			for (int j = 0; j < filelines[f][i].length(); j++) {
				if (diff != null && current != diff[pos + j]) {
					String part = filelines[f][i].substring(last, j);
					h += StringEscapeUtils.escapeHtml4(part);
					if (current) {
						h += "<span>";
					} else {
						h += "</span>";
					}
					last = j;
					current = !current;
				}
			}
			String part = filelines[f][i].substring(last);
			h += StringEscapeUtils.escapeHtml4(part);
			if (!current) h += "</span>";
			h += "\n";
			pos += filelines[f][i].length() + 1;
		}
		return h + "</pre>";
	}
	
	public void outputHTML() {
		Map<Integer, List<Pair<Integer, String>>> output = new HashMap<Integer, List<Pair<Integer, String>>>();
		List<Map<Integer, Paragraph>> files = new ArrayList<Map<Integer, Paragraph>>();
		Iterator<Paragraph> iter = paragraphs.descendingIterator();
		while (iter.hasNext()) {
			Paragraph p = iter.next();
			int f = p.getFile();
			if (files.size() <= f) files.add(new HashMap<Integer, Paragraph>());
			files.get(f).put(p.getStart(), p);
			if (!output.containsKey(f)) output.put(f, new ArrayList<Pair<Integer, String>>());
			output.get(f).add(Pair.of(p.getStart(), generateParagraphHTML(p, p.getAdd())));
		}
		for (int i = 0; i < files.size(); i++) {
			Map<Integer, Map<Integer, boolean[]>> origin = new HashMap<Integer, Map<Integer, boolean[]>>();
			for (Paragraph p : files.get(i).values()) {
				Paragraph o = p.getOrigin();
				if (o == null) continue;
				int f = o.getFile();
				if (!origin.containsKey(f)) origin.put(f, new HashMap<Integer, boolean[]>());
				origin.get(f).put(o.getStart(), p.getDelete());
			}
			
			String path = filenames[i] + ".html";
			try {
				Files.copy(new File("template.html").toPath(), new File(path).toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {}
			try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
				for (Map.Entry<Integer, Map<Integer, boolean[]>> e : origin.entrySet()) {
					w.print("<div id=\"mdiff" + e.getKey() + "\" class=\"mdiffleft\", style=\"display: none;\"><h3>" + filenames[e.getKey()] + "</h3><div class=\"mdiffinner\">");
					for (Pair<Integer, String> p : output.get(e.getKey())) {
						boolean[] diff = e.getValue().get(p.getLeft());
						if (diff == null) {
							w.print(p.getRight());
						} else {
							Paragraph o = files.get(e.getKey()).get(p.getLeft());
							w.print(generateParagraphHTML(o, diff));
						}
					}
					w.print("</div></div>");
				}
				w.print("</div><div class=\"mdiffcolumn\"><div class=\"mdiffright\"><h3>" + filenames[i] + "</h3><div class=\"mdiffinner\">");
				for (Pair<Integer, String> p : output.get(i)) {
					w.print(p.getRight());
				}
				w.print("</div></div></div></body></html>");
			} catch (Exception e) {}
		}
	}
	
	public void outputMDiff() {
		Iterator<Paragraph> iter = paragraphs.descendingIterator();
		PrintWriter w = null;
		int current = -1, line = 0;
		while (iter.hasNext()) {
			Paragraph p = iter.next();
			int f = p.getFile();
			if (current != f) {
				String path = filenames[f] + ".mpatch";
				try {
					if (w != null) w.close();
					w = new PrintWriter(new BufferedWriter(new FileWriter(path)));
				} catch (Exception e) {}
				current = f;
				line = 0;
			}
			for (int i = line; i < p.getStart(); i++) {
				w.println("  " + filelines[f][i]);
			}
			Paragraph o = p.getOrigin();
			if (o == null) {
				for (int i = p.getStart(); i < p.getEnd(); i++) {
					w.println("  " + filelines[f][i]);
				}
			} else {
				int of = o.getFile();
				if (p.isSame()) {
					w.println("= " + filenames[of] + ":" + o.getStart());
					for (int i = p.getStart(); i < p.getEnd(); i++) {
						w.println("  " + filelines[f][i]);
					}
					w.println("---");
				} else {
					boolean[] d1 = p.getDelete(), d2 = p.getAdd();
					int pos = 0;
					w.println("< " + filenames[of] + ":" + o.getStart());
					for (int i = o.getStart(); i < o.getEnd(); i++) {
						w.println("  " + filelines[of][i]);
						w.print("  ");
						for (int j = 0; j < filelines[of][i].length(); j++) {
							w.print(d1[pos + j] ? ' ' : '-');
						}
						pos += filelines[of][i].length() + 1;
						w.println();
					}
					pos = 0;
					w.println("> " + p.getStart());
					for (int i = p.getStart(); i < p.getEnd(); i++) {
						w.println("  " + filelines[f][i]);
						w.print("  ");
						for (int j = 0; j < filelines[f][i].length(); j++) {
							w.print(d2[pos + j] ? ' ' : '+');
						}
						pos += filelines[f][i].length() + 1;
						w.println();
					}
					w.println("---");
				}
			}
			line = p.getEnd();
		}
		if (w != null) w.close();
	}
}
