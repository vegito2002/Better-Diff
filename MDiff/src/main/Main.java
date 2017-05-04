package main;
import java.util.Arrays;
import java.nio.file.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.*;
import org.codelibs.minhash.*;;

public class Main {
	private static void wrongArgs() {
		System.err.println("Usage: mdiff [--html] file1 file2 ...");
		System.exit(1);
	}
	
	private static void readError() {
		System.err.println("Unable to read file.");
		System.exit(1);
	}
	
	public static void main(String[] args) {
		if (args.length < 2 || (args[0].equals("--html") && args.length < 3)) {
			wrongArgs();
		}
		String files[];
		if (args[0].equals("--html")) {
			files = Arrays.copyOfRange(args, 1, args.length);
			if (files.length < 2) {
				wrongArgs();
			}
		} else {
			files = args;
		}
		
		try {
			for (int i = 0; i < files.length; i++) {
				files[i] = new String(Files.readAllBytes(Paths.get(files[i])));
			}
		} catch (Exception e) {
			readError();
		}
		
		Diff diff = new Diff(files);
		diff.process();
		
		Tokenizer tokenizer = new WhitespaceTokenizer();
		int hashBit = 1;
		int seed = 0;
		int num = 128;
		Analyzer analyzer = MinHash.createAnalyzer(tokenizer, hashBit, seed, num);
		String text = "Fess is very powerful and easily deployable Enterprise Search Server.";
		String text1 = "Fess is very powerful and easily deployable Search Server.";
		try {
			byte[] minhash = MinHash.calculate(analyzer, text);
			byte[] minhash1 = MinHash.calculate(analyzer, text1);
			System.out.println(MinHash.compare(minhash, minhash1));
		} catch (Exception e) {
			
		}
	}
}
