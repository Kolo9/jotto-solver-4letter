package jottosolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import static org.apache.log4j.Level.*;

public class ConflictManager {
	private static Set<Conflict> conflicts = new HashSet<Conflict>();
	
	private static Logger logger = Logger.getLogger(ConflictManager.class);
	
	public void add(Conflict c) {
		logger.log(INFO, "Adding " + c);
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
				logger.log(DEBUG, "FOUND A CONFLICT!: " + c);
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
						logger.log(DEBUG, "FOUND A GOOD WORD!");
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
			logger.log(DEBUG, "Just getting the first word with at least 1 unknown");
		} else {
			logger.log(DEBUG, "Found word with 2 unknowns");
		}
		JottoSolver.words.remove(firstWord);
		return firstWord;
	}
}


