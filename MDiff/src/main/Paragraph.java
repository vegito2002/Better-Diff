package main;
import org.codelibs.minhash.MinHash;

public class Paragraph {
	private String filename;
	private int start, end;
	private byte[] hash;
	private Paragraph origin;
	
	public Paragraph(String filename, int start, int end, byte[] hash) {
		this.filename = filename;
		this.start = start;
		this.end = end;
		this.hash = hash;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public int getStart() {
		return this.start;
	}
	
	public int getEnd() {
		return this.end;
	}
	
	public float compare(Paragraph p) {
		return MinHash.compare(hash, p.hash);
	}

	public Paragraph getOrigin() {
		return origin;
	}

	public void setOrigin(Paragraph origin) {
		this.origin = origin;
	}
}
