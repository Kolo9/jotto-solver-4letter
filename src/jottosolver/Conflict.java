package jottosolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import static org.apache.log4j.Level.*;

public class Conflict {
	private int numCorrect;
	private List<Character> options = new ArrayList<Character>(4);
	private Iterator<Character> iter;
	private char c;
	
	private static Logger logger = Logger.getLogger(Conflict.class);

	Conflict(int numCorrect, List<Character> options) {
		if (options.size() <= numCorrect) {
			logger.log(FATAL, "Options size lte numCorrect in Conflict constructor");
			System.exit(1);
		}
		this.numCorrect = numCorrect;
		this.options = new ArrayList<Character>(options);
		Collections.sort(this.options);
	}

	boolean check() {
		logger.log(DEBUG, "Checking conflict, " + numCorrect + " correct from " + options.size() + " letters: ");
		iter = options.iterator();
		while (iter.hasNext()) {
			c = iter.next();
			logger.log(DEBUG, c + "-");
			logger.log(DEBUG, JottoSolver.letters[(int) c-65] + " ");
			if (JottoSolver.letters[(int) c-65] == -1) {
				iter.remove();
			} else if (JottoSolver.letters[(int) c-65] == 1) {
				iter.remove();
				numCorrect--;
			}
		}
		logger.log(DEBUG, "\n"); 
		if (options.size() < numCorrect) {
			logger.log(FATAL, "Options size less than numCorrect in Conflict check");
			System.exit(1);
		}
		if (numCorrect == 0) {
			for (Character goodChar: options) {
				JottoSolver.letters[(int) goodChar-65] = -1;
			}
			return true;
		}
		if (options.size() == numCorrect) {
			for (Character goodChar: options) {
				JottoSolver.letters[(int) goodChar-65] = 1;
			}
			return true;
		}
		return false;
	}
	
	public int getNumCorrect() {
		return numCorrect;
	}
	
	public List<Character> getOptions() {
		return new ArrayList<Character>(options);
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numCorrect;
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		Conflict other = (Conflict) obj;
		if (numCorrect != other.numCorrect) return false;
		if (options.size() != other.getOptions().size()) return false;
		for (char c: options) {
			if (!other.getOptions().contains(c)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Conflict " + numCorrect + " correct from " + options.size() + " letters.";
	}
	
	public static void main (String[] args) {
		Set<Conflict> set = new HashSet<Conflict>();
		
		ArrayList<Character> chars = new ArrayList<Character>(2);
		chars.add('A');
		chars.add('B');
		Conflict c = new Conflict(1, chars);
		set.add(c);
		
		chars = new ArrayList<Character>(2);
		chars.add('B');
		chars.add('A');
		c = new Conflict(1, chars);
		set.add(c);
		JottoSolver.letters[0] = -1;
		c.check();
		set.remove(c);
	}
}


