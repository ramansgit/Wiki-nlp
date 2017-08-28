package com.nlp.handlers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.nlp.algorithm.NgramBitApAlgorithm;
import com.nlp.model.MatchPojo;

/**
 * handler class responsible for performing qa finding.
 * 
 * @author ramans
 *
 */
public class QuestionAnswerHandler {

	private final static Splitter splitter = Splitter.on('.').omitEmptyStrings().trimResults();

	/**
	 * 
	 * @param paragraph
	 * @param providedAnswers
	 * @return
	 */
	public Map<String, String> matchInterestingSentenceWithAnswers(String paragraph, List<String> providedAnswers) {

		Map<String, String> map = new LinkedHashMap<String, String>();
		for (String answer : providedAnswers) {

			List<String> sentences = splitter.splitToList(paragraph);

			for (String sentence : sentences) {
				if (sentence.contains(answer)) {
					map.put(answer, sentence);
				}
			}
		}
		return map;

	}

	/**
	 * find answers for the questions
	 * 
	 * @param interestMap
	 * @param questions
	 * @return
	 */
	public Map<String, String> findAnswersForQuestions(Map<String, String> interestMap, List<String> questions) {
		Map<String, String> qaMap = new LinkedHashMap<String, String>();
		for (String question : questions) {
			qaMap.put(question, getAnwserForQuestion(interestMap, question));
		}
		return qaMap;
	}

	/**
	 * gets answer for the question
	 * 
	 * @param interestMap
	 * @param question
	 * @return
	 */
	public String getAnwserForQuestion(Map<String, String> interestMap, String question) {
		MatchPojo match = new MatchPojo();
		MatchPojo anotherMatch = new MatchPojo();

		for (String answerKey : interestMap.keySet()) {
			String sentence = interestMap.get(answerKey);
			int rank = NgramBitApAlgorithm.rank(preProcess(sentence), preProcess(question));

			if (rank > match.rank) {
				match.set(answerKey, question, sentence, rank);
				anotherMatch.clear();
			} else if (rank == match.rank && rank > Integer.MIN_VALUE) {
				anotherMatch.set(answerKey, question, sentence, rank);
			}
			if (anotherMatch.potentialAnswer != null) {
				int newScore = NgramBitApAlgorithm.bruteforce(preProcess(anotherMatch.interestingSentence),
						preProcess(question));
				int oldScore = NgramBitApAlgorithm.bruteforce(preProcess(match.interestingSentence),
						preProcess(question));

				match.potentialAnswer = newScore > oldScore ? anotherMatch.potentialAnswer : match.potentialAnswer;
			}

		}
		return match.potentialAnswer;

	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String preProcess(final String text) {
		return text.replaceAll(
				"(?i)\\b(the|to|and|for|in|a|their|is|of|do|did|does|what|why|which|who|whom|how|it|are|at|by|,)\\b",
				"").replaceAll("(\\?|;|,|\\s)+", " ").trim().toLowerCase();
	}

}
