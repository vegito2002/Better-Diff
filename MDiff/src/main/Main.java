package main;
import java.util.Arrays;
import java.nio.file.*;

public class Main {
	public static void main(String[] args) {
		if (args.length < 2 || (args[0].equals("--html") && args.length < 3)) {
			System.err.println("Usage: mdiff [--html] file1 file2 ...");
			System.exit(1);
		}
		String[] filenames;
		boolean html = false;
		if (args[0].equals("--html")) {
			filenames = Arrays.copyOfRange(args, 1, args.length);
			html = true;
		} else {
			filenames = args;
		}
		
		String[] files = new String[filenames.length];
		try {
			for (int i = 0; i < filenames.length; i++) {
				files[i] = new String(Files.readAllBytes(Paths.get(filenames[i])));
			}
		} catch (Exception e) {
			System.err.println("Unable to read file.");
			System.exit(1);
		}
		
		Diff diff = new Diff(filenames, files);
		diff.process();
		if (html) {
			diff.outputHTML();
		} else {
			diff.outputMPatch();
		}
	}
}
