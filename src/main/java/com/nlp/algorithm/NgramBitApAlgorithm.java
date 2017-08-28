package com.nlp.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import com.google.common.base.Splitter;
/**
 * NgramBitApAlgorithm
 * @author ramans
 *
 */
public class NgramBitApAlgorithm {
	public static float Match_Threshold = 0.5f;
	private final static Splitter wordSplitter = Splitter.on(' ').omitEmptyStrings().trimResults();

	private static final Map<Map.Entry<String, String>, Integer> lookup = new HashMap<Map.Entry<String, String>, Integer>();


	private static int Match_MaxBits = 64;

	/**
	 * match 
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static int match(String text, String pattern) {
		int loc = 0;

		assert (Match_MaxBits == 0 || pattern.length() <= Match_MaxBits) : "Pattern too long for this application.";

		Map<Character, Integer> s = initialize(pattern);

		double score_threshold = Match_Threshold;

		int best_loc = text.indexOf(pattern, loc);
		if (best_loc != -1) {
			score_threshold = Math.min(bitapScore(0, pattern), score_threshold);

			best_loc = text.lastIndexOf(pattern, loc + pattern.length());
			if (best_loc != -1) {
				score_threshold = Math.min(bitapScore(0, pattern), score_threshold);
			}
		}

		int matchmask = 1 << (pattern.length() - 1);
		best_loc = -1;
		int bin_min, bin_mid;
		int bin_max = pattern.length() + text.length();

		int[] last_rd = new int[0];
		for (int d = 0; d < pattern.length(); d++) {
			bin_min = 0;
			bin_mid = bin_max;
			while (bin_min < bin_mid) {
				if (bitapScore(d, pattern) <= score_threshold) {
					bin_min = bin_mid;
				} else {
					bin_max = bin_mid;
				}
				bin_mid = (bin_max - bin_min) / 2 + bin_min;
			}
			bin_max = bin_mid;
			int start = Math.max(1, loc - bin_mid + 1);
			int finish = Math.min(loc + bin_mid, text.length()) + pattern.length();
			int[] rd = new int[finish + 2];
			rd[finish + 1] = (1 << d) - 1;
			for (int j = finish; j >= start; j--) {
				int charMatch;
				if (text.length() <= j - 1 || !s.containsKey(text.charAt(j - 1))) {
					charMatch = 0;
				} else {
					charMatch = s.get(text.charAt(j - 1));
				}
				if (d == 0) {
					rd[j] = ((rd[j + 1] << 1) | 1) & charMatch;
				} else {
					rd[j] = ((rd[j + 1] << 1) | 1) & charMatch | (((last_rd[j + 1] | last_rd[j]) << 1) | 1)
							| last_rd[j + 1];
				}
				if ((rd[j] & matchmask) != 0) {
					double score = bitapScore(d, pattern);
					if (score <= score_threshold) {
						score_threshold = score;
						best_loc = j - 1;
						if (best_loc > loc) {
							start = Math.max(1, 2 * loc - best_loc);
						} else {
							break;
						}
					}
				}
			}
			if (bitapScore(d + 1, pattern) > score_threshold) {
				break;
			}
			last_rd = rd;
		}
		return best_loc;
	}

	/**
	 * bitap score
	 * @param e
	 * @param pattern
	 * @return
	 */
	private static double bitapScore(int e, String pattern) {
		return (float) e / pattern.length();
	}

	/**
	 * initalize
	 * @param pattern
	 * @return
	 */
	protected static Map<Character, Integer> initialize(String pattern) {
		Map<Character, Integer> s = new HashMap<Character, Integer>();
		char[] char_pattern = pattern.toCharArray();
		for (char c : char_pattern) {
			s.put(c, 0);
		}
		int i = 0;
		for (char c : char_pattern) {
			s.put(c, s.get(c) | (1 << (pattern.length() - i - 1)));
			i++;
		}
		return s;
	}

	
	
	/**
	 * get rank
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static int rank(String text, String pattern) {
		assert text != null && pattern != null : "Text or pattern can't be null";

		if (text.equals(pattern)) {
			// lucky!
			return pattern.length();
		}

		int K = wordSplitter.splitToList(pattern).size() < 3 ? 2 : 3;

		List<String> nGramsText = generateNgram(K, text);
		List<String> nGramsPattern = generateNgram(K, pattern);

		int score = 0;
		for (String haystack : nGramsText) {
			for (String needle : nGramsPattern) {
				int loc = lookup.getOrDefault(new SimpleEntry<String, String>(haystack, needle), Integer.MIN_VALUE);

				if (loc == Integer.MIN_VALUE) {
					loc = NgramBitApAlgorithm.match(haystack, needle);
					lookup.put(new SimpleEntry<String, String>(haystack, needle), loc);
				}

				if (loc >= 0) {
					score += 1.0;
				}
			}
		}

		return score == 0 ? Integer.MIN_VALUE : score;
	}

	/**
	 * generate ngram
	 * @param k
	 * @param source
	 * @return
	 */
	private static List<String> generateNgram(int k, String source) {
		List<String> nGrams = new ArrayList<String>();
		List<String> words = wordSplitter.splitToList(source);

		for (int i = 0; i < words.size() - k + 1; i++) {
			nGrams.add(concat(words, i, i + k));
		}

		return nGrams;
	}

	/**
	 * 
	 * @param words
	 * @param start
	 * @param end
	 * @return
	 */
	private static String concat(List<String> words, int start, int end) {
		StringBuilder sb = new StringBuilder();

		for (int i = start; i < end; i++) {
			sb.append(i > start ? " " : "").append(words.get(i));
		}

		return sb.toString();
	}

	/**
	 * using brute force approach when 1 or more sentence matching with score.
	 * @param text
	 * @param pattern
	 * @return
	 */
	public static int bruteforce(String text, String pattern) {
		assert text != null && pattern != null : "Text or pattern can't be null";

		if (text.contains(pattern)) {
			return pattern.length();
		}

		final int[] score = { 0 };
		final List<String> foundWords = new ArrayList<String>();
		List<String> words = wordSplitter.splitToList(pattern);

		for (String word : words) {
			if (!foundWords.contains(word)) {
				List<String> textWords = wordSplitter.splitToList(text);

				for (String textWord : textWords) {
					if (textWord.equals(word)) {
						score[0] += 2;
						foundWords.add(word);
					} else if (textWord.contains(word) || word.contains(textWord)) {
						score[0]++;
						foundWords.add(word);
					}
				}
			}
		}

		return score[0];
	}

}
