package jottosolver;

public class Word implements Comparable<Word> {
	String word;
	float prob;

	Word(String word) {
		this.word = word;
	}

	@Override
	public int compareTo(Word other) {
		return (prob == other.prob) ? 0 : (prob > other.prob) ? 1 : -1;
	}
}