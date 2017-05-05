package main;
import org.codelibs.minhash.MinHash;

public class Paragraph {
	private int file, start, end;
	private byte[] hash;
	private Paragraph origin;
	
	public Paragraph(int file, int start, int end, byte[] hash) {
		this.file = file;
		this.start = start;
		this.end = end;
		this.hash = hash;
	}
	
	public int getFile() {
		return this.file;
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
