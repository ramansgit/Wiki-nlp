package com.nlp.algorithm;

import java.util.HashMap;
import java.util.Map;

public class Bitap {
	public static float Match_Threshold = 0.5f;

	private static int Match_MaxBits = 64;

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

	private static double bitapScore(int e, String pattern) {
		return (float) e / pattern.length();
	}

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

}
