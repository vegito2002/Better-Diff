package main;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.*;
import org.codelibs.minhash.MinHash;

public class Diff {
	private String[] filenames;
	private String[] files;
	private boolean html;
	private List<Paragraph> paragraphs = new ArrayList<Paragraph>();
	
	private Tokenizer tokenizer = new WhitespaceTokenizer();
	private Analyzer analyzer;
	
	public Diff(String[] filenames, String[] files, boolean html) {
		this.filenames = filenames;
		this.files = files;
		this.html = html;
		
		int hashBit = 1;
		int seed = 0;
		int num = 128;
		analyzer = MinHash.createAnalyzer(tokenizer, hashBit, seed, num);
	}
	
	private void processParagraph(String filename, String[] lines, int start, int end) {
		String paragraph = "";
		for (int i = start; i < end; i++) {
			paragraph += lines[i] + "\n";
		}
		
		try {
			byte[] hash = MinHash.calculate(analyzer, paragraph);
			Paragraph p = new Paragraph(filename, start, end, hash);
			
			float[] similarity = new float[paragraphs.size()];
			int i = 0;
			for (Paragraph p0 : paragraphs) {
				similarity[i] = p.compare(p0);
				i++;
			}
			
			int index = -1;
			float max = 0;
			for (i = paragraphs.size() - 1; i >= 0; i--) {
				if (similarity[i] > max) {
					max = similarity[i];
					index = i;
				}
			}
			if (max > 0.8) {
				p.setOrigin(paragraphs.get(index));
			}
			
			paragraphs.add(p);
		} catch (Exception e) {}
	}
	
	public void process() {
		for (int i = 0; i < files.length; i++) {
			String[] lines = files[i].split("\\r?\\n");
			if (lines.length == 0) continue;
			for (int j = 0; j < lines.length; j++) {
				lines[j] = lines[j].trim();
			}
			int start = 0;
			boolean paragraph = !lines[0].equals("");
			for (int j = 1; j < lines.length; j++) {
				if (lines[j].equals("")) {
					if (paragraph) {
						processParagraph(filenames[i], lines, start, j);
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
				processParagraph(filenames[i], lines, start, lines.length);
			}
		}
	}
}
