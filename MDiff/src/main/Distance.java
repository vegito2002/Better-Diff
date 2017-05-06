package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public class Distance {
	private static Map<Integer, Integer> findBlocks(String s) {
		Map<Integer, Integer> endToStart = new HashMap<Integer, Integer>();
		LinkedList<Pair<Integer, Character>> stack = new LinkedList<Pair<Integer, Character>>();
		int i = 0, quote2 = -1, quote1 = -1;
		boolean escaping = false, quote2first = false;
		for (char c : s.toCharArray()) {
			switch (c) {
			case '{':
				int pos = s.substring(0, i > 0 ? i - 1 : i).lastIndexOf('\n');
				stack.push(Pair.of(pos + 1, c));
				break;
			case '[': case '(':
				stack.push(Pair.of(i, c));
				break;
			case '}': case ']': case ')':
				if (stack.isEmpty()) break;
				Pair<Integer, Character> p = stack.pop();
				if (c == '}' && p.getRight() != '{' || c == ']' && p.getRight() != '[' || c == ')' && p.getRight() != '(') {
					stack.push(p);
					break;
				}
				endToStart.put(i, p.getLeft());
				break;
			case '"':
				if (escaping) break;
				if (quote2 > 0) {
					endToStart.put(i, quote2);
					quote2 = -1;
					if (quote2first) quote1 = -1;
				} else {
					if (quote1 < 0) quote2first = true;
					quote2 = i;
				}
				break;
			case '\'':
				if (escaping) break;
				if (quote1 > 0) {
					endToStart.put(i, quote1);
					quote1 = -1;
					if (!quote2first) quote2 = -1;
				} else {
					if (quote2 < 0) quote2first = false;
					quote1 = i;
				}
			}
			if (c == '\\') {
				escaping = !escaping;
			} else {
				escaping = false;
			}
			i++;
		}
		return endToStart;
	}
	
	private enum Direction { DIAGONAL, UP, LEFT }
	
	private static boolean isGoodDown(int i, int j, Direction[][] prev, Map<Integer, Integer> b) {
		i--;
		Integer o = b.get(i);
		if (o == null) return true;
		Direction d = null;
		while (i > o) {
			d = prev[i][j];
			switch (d) {
			case DIAGONAL:
				i--;
				j--;
				break;
			case UP:
				i--;
				break;
			case LEFT:
				j--;
			}
		}
		return d == Direction.UP;
	}
	
	private static boolean isGoodRight(int i, int j, Direction[][] prev, Map<Integer, Integer> b) {
		j--;
		Integer o = b.get(j);
		if (o == null) return true;
		Direction d = null;
		while (j > o) {
			d = prev[i][j];
			switch (d) {
			case DIAGONAL:
				i--;
				j--;
				break;
			case UP:
				i--;
				break;
			case LEFT:
				j--;
			}
		}
		return d == Direction.LEFT;
	}
	
	public static Pair<boolean[], boolean[]> processCharacter(String str1, String str2) {
		if (str1.equals(str2)) return null;
		Map<Integer, Integer> b1 = findBlocks(str1), b2 = findBlocks(str2);
		char[] s1 = str1.toCharArray(), s2 = str2.toCharArray();
		int[][] f = new int[s1.length + 1][s2.length + 1];
		Direction[][] prev = new Direction[s1.length + 1][s2.length + 1];
		boolean[][] good = new boolean[s1.length + 1][s2.length + 1];
		for (int i = 0; i <= s1.length; i++) {
			f[i][0] = i;
			prev[i][0] = Direction.UP;
			good[i][0] = true;
		}
		for (int i = 0; i <= s2.length; i++) {
			f[0][i] = i;
			prev[0][i] = Direction.LEFT;
			good[0][i] = true;
		}
		for (int i = 1; i <= s1.length; i++) {
			for (int j = 1; j <= s2.length; j++) {
				if (s1[i - 1] == s2[j - 1]) {
					int diagonal = f[i - 1][j - 1];
					int up = f[i - 1][j] + 1;
					int left = f[i][j - 1] + 1;
					if (diagonal < up && diagonal < left) {
						f[i][j] = diagonal;
						prev[i][j] = Direction.DIAGONAL;
						good[i][j] = good[i - 1][j - 1];
						continue;
					}
					boolean goodDown = !good[i - 1][j] ? false : isGoodDown(i, j, prev, b1);
					boolean goodRight = !good[i][j - 1] ? false : isGoodRight(i, j, prev, b2);
					if (up < diagonal && up < left) {
						f[i][j] = up;
						prev[i][j] = Direction.UP;
						good[i][j] = goodDown;
						continue;
					}
					if (left < diagonal && left < up) {
						f[i][j] = left;
						prev[i][j] = Direction.LEFT;
						good[i][j] = goodRight;
						continue;
					}
					if (diagonal == up && diagonal == left) {
						if (good[i - 1][j - 1] || (!goodDown && !goodRight)) {
							f[i][j] = diagonal;
							prev[i][j] = Direction.DIAGONAL;
							good[i][j] = good[i - 1][j - 1];
						} else if (goodDown) {
							f[i][j] = up;
							prev[i][j] = Direction.UP;
							good[i][j] = goodDown;
						} else {
							f[i][j] = left;
							prev[i][j] = Direction.LEFT;
							good[i][j] = goodRight;
						}
						continue;
					}
					if (diagonal == up) {
						if (goodDown) {
							f[i][j] = up;
							prev[i][j] = Direction.UP;
							good[i][j] = goodDown;
						} else {
							f[i][j] = diagonal;
							prev[i][j] = Direction.DIAGONAL;
							good[i][j] = good[i - 1][j - 1];
						}
					} else {
						if (goodRight) {
							f[i][j] = left;
							prev[i][j] = Direction.LEFT;
							good[i][j] = goodRight;
						} else {
							f[i][j] = diagonal;
							prev[i][j] = Direction.DIAGONAL;
							good[i][j] = good[i - 1][j - 1];
						}
					}
				} else {
					int up = f[i - 1][j] + 1;
					int left = f[i][j - 1] + 1;
					boolean goodDown = !good[i - 1][j] ? false : isGoodDown(i, j, prev, b1);
					boolean goodRight = !good[i][j - 1] ? false : isGoodRight(i, j, prev, b2);
					if (up < left) {
						f[i][j] = up;
						prev[i][j] = Direction.UP;
						good[i][j] = goodDown;
					} else if (left < up) {
						f[i][j] = left;
						prev[i][j] = Direction.LEFT;
						good[i][j] = goodRight;
					} else {
						if (goodDown) {
							f[i][j] = up;
							prev[i][j] = Direction.UP;
							good[i][j] = goodDown;
						} else {
							f[i][j] = left;
							prev[i][j] = Direction.LEFT;
							good[i][j] = goodRight;
						}
					}
				}
			}
		}
		int i = s1.length, j = s2.length;
		boolean[] t1 = new boolean[s1.length], t2 = new boolean[s2.length];
		while (i > 0 || j > 0) {
			switch (prev[i][j]) {
			case DIAGONAL:
				i--;
				j--;
				t1[i] = true;
				t2[j] = true;
				break;
			case UP:
				i--;
				break;
			case LEFT:
				j--;
			}
		}
		return Pair.of(t1, t2);
	}
}
