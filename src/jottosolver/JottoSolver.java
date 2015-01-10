package jottosolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.log4j.Logger;
import static org.apache.log4j.Level.*;

public class JottoSolver {

	private static final String HIDDEN_WORD = "LOVE";
	private static final int LOW_PROB_WORDS_TO_REMOVE = 0;
	private static int totalGuesses = 0;
	private static int[] letterCounts = new int[26];
	private static float[] letterProbs = new float[26];
	public static LinkedList<Word> words = new LinkedList<Word>();
	//////////////////////////////////////////
	// -1 - No
	// 0 - Not sure
	// 1 - Yes
	public static int[] letters = new int[26];
	//////////////////////////////////////////
	private static ConflictManager conflictManager = new ConflictManager();
	
	private static Logger logger = Logger.getLogger(JottoSolver.class);
	

	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(DEBUG);
		
		int i;
		for (i = 0; i < letterCounts.length; i++) {
			letterCounts[i] = 0;
			letters[i] = 0;
		}
		
		Scanner input;
		try {
			input = new Scanner(new File("filteredwords"));
			int totalChars = 0;

			while (input.hasNext()) {
				String word = input.next();
				words.add(new Word(word));
				for (i = 0; i < 4; i++) {
					totalChars++;
					letterCounts[((int) word.charAt(i)) - 65]++;
				}
			}
			for (i = 0; i < letterCounts.length; i++) {
				logger.log(DEBUG, String.format("%c: %4d\n", (char) i+65, letterCounts[i]));
				letterProbs[i] = (float) letterCounts[i] / totalChars;
				logger.log(DEBUG, String.format("%c: %.4f\n", (char) i+65, letterProbs[i]));
			}

			input.close();
		} catch (FileNotFoundException e) {
			logger.log(FATAL, "Filtered words file not found", e);
			System.exit(1);
		} catch (Exception e) {
			logger.log(FATAL, "", e);
			System.exit(1);
		}

		for (Word w: words) {
			w.prob = letterProbs[(int)w.word.charAt(0)-65];
			w.prob += letterProbs[(int)w.word.charAt(1)-65];
			w.prob += letterProbs[(int)w.word.charAt(2)-65];
			w.prob += letterProbs[(int)w.word.charAt(3)-65];
		}
		Collections.sort(words);

		
		for (i = 0; i < 25; i++) {
			logger.log(DEBUG, String.format("%s: %.4f\n", words.get(i).word, words.get(i).prob));
		}
		logger.log(DEBUG, String.format("%s: %.4f\n", words.get(words.size()-1).word, words.get(words.size()-1).prob));
		
		
		for (i = 0; i < LOW_PROB_WORDS_TO_REMOVE; i++) {
			words.removeFirst();
		}

		int curGuessAmt;
		ArrayList<Character> conflictChars = new ArrayList<Character>(4);
		int numConfirmedChars;
		Word word;
		int lettersFound = 0;
		
		input = new Scanner(System.in);
		while (lettersFound != 4) {
			totalGuesses++; //REMOVE
			conflictChars.clear();
			numConfirmedChars = 0;
			//word = words.removeFirst();
			word = conflictManager.getNextGuess();
			System.out.println("Guessing " + word.word);
			curGuessAmt = input.nextInt();
			//curGuessAmt = guess(word);
			switch (curGuessAmt) {
			case 0:
				letters[(int) word.word.charAt(0)-65] = -1;
				letters[(int) word.word.charAt(1)-65] = -1;
				letters[(int) word.word.charAt(2)-65] = -1;
				letters[(int) word.word.charAt(3)-65] = -1;
				break;
			case 1:
				for (i = 0; i < word.word.length(); i++) {
					if (letters[(int) word.word.charAt(i)-65] == 1) {
						letters[(int) word.word.charAt((i+1)%4)-65] = -1;
						letters[(int) word.word.charAt((i+2)%4)-65] = -1;
						letters[(int) word.word.charAt((i+3)%4)-65] = -1;
						conflictChars.clear();
						break;
					} else if (letters[(int) word.word.charAt(i)-65] == 0) {
						conflictChars.add(word.word.charAt(i));
					}
				}
				if (conflictChars.size() == 1) {
					letters[(int) conflictChars.get(0)-65] = 1;
					lettersFound++;
				} else if (conflictChars.size() > 1) {
					conflictManager.add(new Conflict(1, conflictChars));
				}
				break;
			case 2:
				for (i = 0; i < word.word.length(); i++) {
					if (letters[(int) word.word.charAt(i)-65] == 1) {
						numConfirmedChars++;
						if (numConfirmedChars == 2) {
							if (letters[(int) word.word.charAt((i+1)%4)-65] != 1) letters[(int) word.word.charAt((i+1)%4)-65] = -1;
							if (letters[(int) word.word.charAt((i+2)%4)-65] != 1) letters[(int) word.word.charAt((i+2)%4)-65] = -1;
							if (letters[(int) word.word.charAt((i+3)%4)-65] != 1) letters[(int) word.word.charAt((i+3)%4)-65] = -1;
							conflictChars.clear();
							break;
						}
					} else if (letters[(int) word.word.charAt(i)-65] == 0) {
						conflictChars.add(word.word.charAt(i));
					}
				}
				if (numConfirmedChars == 1 && conflictChars.size() == 1) {
					letters[(int) conflictChars.get(0)-65] = 1;
					lettersFound++;
				} else if (numConfirmedChars == 1) {
					conflictManager.add(new Conflict(1, conflictChars));
				} else if (conflictChars.size() == 2){
					letters[(int) conflictChars.get(0)-65] = 1;
					lettersFound++;
					letters[(int) conflictChars.get(1)-65] = 1;
					lettersFound++;
				} else if (conflictChars.size() > 2) {
					conflictManager.add(new Conflict(2, conflictChars));
				}
				break;
			case 3:
				for (i = 0; i < word.word.length(); i++) {
					if (letters[(int) word.word.charAt(i)-65] == 1) {
						numConfirmedChars++;
						if (numConfirmedChars == 3) {
							if (letters[(int) word.word.charAt((i+1)%4)-65] != 1) letters[(int) word.word.charAt((i+1)%4)-65] = -1;
							if (letters[(int) word.word.charAt((i+2)%4)-65] != 1) letters[(int) word.word.charAt((i+2)%4)-65] = -1;
							if (letters[(int) word.word.charAt((i+3)%4)-65] != 1) letters[(int) word.word.charAt((i+3)%4)-65] = -1;
							conflictChars.clear();
							break;
						}
					} else if (letters[(int) word.word.charAt(i)-65] == 0) {
						conflictChars.add(word.word.charAt(i));
					}
				}
				if (numConfirmedChars == 2 && conflictChars.size() == 1) {
					letters[(int) conflictChars.get(0)-65] = 1;
					lettersFound++;
				} else if (numConfirmedChars == 2) {
					conflictManager.add(new Conflict(1, conflictChars));
				} else if (numConfirmedChars == 1 && conflictChars.size() == 2) {
					letters[(int) conflictChars.get(0)-65] = 1;
					lettersFound++;
					letters[(int) conflictChars.get(1)-65] = 1;
					lettersFound++;
				} else if (numConfirmedChars == 1) {
					conflictManager.add(new Conflict(2, conflictChars));
				} else if (conflictChars.size() == 3){
					letters[(int) conflictChars.get(0)-65] = 1;
					lettersFound++;
					letters[(int) conflictChars.get(1)-65] = 1;
					lettersFound++;
					letters[(int) conflictChars.get(2)-65] = 1;
					lettersFound++;
				} else if (conflictChars.size() > 3) {
					conflictManager.add(new Conflict(3, conflictChars));
				}
				break;
			case 4:
				for (i = 0; i < letters.length; i++) {
					letters[i] = -1;
				}
				letters[(int) word.word.charAt(0)-65] = 1;
				letters[(int) word.word.charAt(1)-65] = 1;
				letters[(int) word.word.charAt(2)-65] = 1;
				letters[(int) word.word.charAt(3)-65] = 1;
				lettersFound = 4;
				break;
			default:
				logger.log(FATAL, "More than four letters found");
				System.exit(1);
			}
			
			conflictManager.check();
			
			lettersFound = printYesAndNo(); //removethis
		}
		input.close();
		
		System.out.println("All four letters found: ");
		for (i = 0; i < letters.length; i++) {
			if (letters[i] == 1) {
				System.out.println((char) (i+65));
			}
		}
		System.out.println("Took " + totalGuesses + " guesses.");
	}

	
	private static int printYesAndNo() {
		System.out.print("YES: ");
		int lettersFound = 0; //REMOVE THIS
		int i;
		for (i = 0; i < letters.length; i++) {
			if (letters[i] == 1) {
				lettersFound++; //REMOVE THIS, DO A +1 IN CONFLICTMANAGER WHERE NEEDED.
				System.out.print((char)(i+65) + " ");
			}
		}
		System.out.println();
		System.out.print("NO: ");
		for (i = 0; i < letters.length; i++) {
			if (letters[i] == -1) {
				System.out.print((char)(i+65) + " ");
			}
		}
		System.out.println();
		if (lettersFound >= 1) {
			System.out.print("POSSIBLE WORDS(remember duplicates have been removed): "); //Take these and make guesses better using chars found in these words
			ArrayList<Character> chars;
			boolean nope = true;
			for (Word w: words) {
				nope = false;
				chars = new ArrayList<Character>(w.word.length());
				for (char c: w.word.toCharArray()) {
					chars.add(c);
				}
				for (i = 0; i < letters.length; i++) {
					if (letters[i] == 1 && !chars.contains((char)(i+65))) {
						nope = true;
						break;
					}
				}
				if (!nope) {
					System.out.print(w.word + " ");
				}
			}
			System.out.println();
		}
		System.out.println();
		return lettersFound;
	}

	private static int guess(Word word) {
		int numMatch = 0;
		for (int i = 0; i < word.word.length(); i++) {
			for (int j = 0; j < HIDDEN_WORD.length(); j++) {
				if (word.word.charAt(i) == HIDDEN_WORD.charAt(j)) {
					numMatch++;
					break;
				}
			}
		}
		return numMatch;
	}
}


