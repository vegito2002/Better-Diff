package main;
import org.codelibs.minhash.MinHash;

/*
	Separate class to handle the MinHash Calculation component imported.
*/

public class Paragraph {
	private int file, start, end;
	private byte[] hash;
	private Paragraph origin;
	/**
	 * Whether this paragraph is identical to its origin.
	 */
	private boolean same;
	/**
	 * Characters deleted from origin.
	 */
	private boolean[] delete;
	/**
	 * Characters added to this paragraph.
	 */
	private boolean[] add;
	
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
	
	public void setSame() {
		same = true;
	}
	
	public boolean isSame() {
		return same;
	}

	public boolean[] getDelete() {
		return delete;
	}

	public void setDelete(boolean[] delete) {
		this.delete = delete;
	}

	public boolean[] getAdd() {
		return add;
	}

	public void setAdd(boolean[] add) {
		this.add = add;
	}
}
