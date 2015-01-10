package jottosolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConflictManager {
	private static Set<Conflict> conflicts = new HashSet<Conflict>();
	
	public void add(Conflict c) {
		System.out.println("Adding " + c);
		Iterator<Conflict> iter = conflicts.iterator();
		if (iter.hasNext()) System.out.println("When first is " + iter.next());
		conflicts.add(c);
	}
	
	public void check() {
		Iterator<Conflict> iter;
		boolean hasConflictResolved = true;
		Conflict c;
		while (hasConflictResolved) {
			iter = conflicts.iterator();
			conflicts = new HashSet<Conflict>();
			hasConflictResolved = false;
			while (iter.hasNext()) {
				c = iter.next();
				if (c.check()) {
					hasConflictResolved = true;
				} else {
					conflicts.add(c);
				}
			}
		}
	}
	
	public Word getNextGuess() {
		Iterator<Word> iter = JottoSolver.words.iterator();
		Word w;
		boolean found, hasConflictLetter;
		
		if (conflicts.size() != 0) {
			Conflict c = null;
			for (Conflict temp: conflicts) {
				if (temp.getNumCorrect() == 1 && temp.getOptions().size() == 2) {
					c = temp;
					break;
				}
			}
			if (c != null) {
				System.out.println("FOUND A CONFLICT!: " + c);
				iter = JottoSolver.words.iterator();
				while (iter.hasNext()) {
					w = iter.next();
					found = true;
					hasConflictLetter = false;
					for (int i = 0; i < w.word.length(); i++) {
						if (JottoSolver.letters[(int)w.word.charAt(i)-65] == 0 && !c.getOptions().contains(w.word.charAt(i))) {
							found = false;
							break;
						}
						if (c.getOptions().contains(w.word.charAt(i))) {
							if (hasConflictLetter) {
								found = false;
								break;
							}
							hasConflictLetter = true;
						}
					}
					if (found && hasConflictLetter) {
						System.out.println("FOUND A GOOD WORD!");
						iter.remove();
						return w;
					}
				}
			}
		}
		Word firstWord = null;
		int i;
		int index = JottoSolver.words.size();
		int numUnknownChars = 0;
		while (numUnknownChars != 2 && index > 0) {
			numUnknownChars = 0;
			index--;
			firstWord = JottoSolver.words.get(index);
			for (i = 0; i < firstWord.word.length(); i++) {
				if (JottoSolver.letters[(int) firstWord.word.charAt(i)-65] == 0) {
					numUnknownChars++;
				}
			}
		}
		if (numUnknownChars != 2) {
			numUnknownChars = 0;
			index = -1;
			while (numUnknownChars < 1 && index < JottoSolver.words.size() - 1) {
				numUnknownChars = 0;
				index++;
				firstWord = JottoSolver.words.get(index);
				for (i = 0; i < firstWord.word.length(); i++) {
					if (JottoSolver.letters[(int) firstWord.word.charAt(i)-65] == 0) {
						numUnknownChars++;
					}
				}
			}
			System.out.println("JUST GETTING FIRST WORD WITH AT LEAST 1 UNKNOWN.");
			/*
			if (numUnknownChars != 1) {
				System.out.println("JUST GETTING FIRST WORD WITH AT LEAST 1 UNKNOWN.");
				return JottoSolver.words.removeFirst();
			} else {
				System.out.println("FOUND WORD WITH 1 UNKNOWN.");
			}
			*/
		} else {
			System.out.println("FOUND WORD WITH 2 UNKNOWNS.");
		}
		JottoSolver.words.remove(firstWord);
		return firstWord;
		/*
		Iterator<Word> iter = JottoSolver.words.iterator();
		Word w;
		Word safetyWord = null;
		boolean found;
		while (iter.hasNext()) {
			found = true;
			safetyWord = iter.next();
			for (int i = 0; i < safetyWord.word.length(); i++) {
				if (JottoSolver.letters[(int)safetyWord.word.charAt(i)-65] != 0) {
					found = false;
					break;
				}
			}
			
			if (found) {
				iter.remove();
				break;
			}
		}
		if (safetyWord == null) {
			safetyWord = JottoSolver.words.removeFirst();
		}
		
		if (conflicts.size() == 0) {
			return safetyWord;
		} else {
			Conflict c = null;
			for (Conflict temp: conflicts) {
				if (temp.getNumCorrect() == 1 && temp.getOptions().size() == 2) {
					c = temp;
					break;
				}
			}
			if (c != null) {
				iter = JottoSolver.words.iterator();
				while (iter.hasNext()) {
					w = iter.next();
					found = true;
					for (int i = 0; i < w.word.length(); i++) {
						if (JottoSolver.letters[(int)w.word.charAt(i)-65] != -1 && !c.getOptions().contains(w.word.charAt(i))) {
							found = false;
							break;
						}
					}
					if (found) {
						iter.remove();
						return w;
					}
				}
				return safetyWord;
			} else {
				return safetyWord;
			}
		}
		*/
	}
}


