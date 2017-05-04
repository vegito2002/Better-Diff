package main;

public class Diff {
	private String[] files;
	
	public Diff(String[] files) {
		this.files = files;
	}
	
	private void processParagraph(String[] lines, int start, int end) {
		String paragraph = "";
		for (int i = start; i < end; i++) {
			paragraph += lines[i] + "\n";
		}
		
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
						processParagraph(lines, start, j);
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
				processParagraph(lines, start, lines.length);
			}
		}
	}
}
