package com.nlp.handlers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.nlp.algorithm.SentenceSimilarityRanker;
import com.nlp.model.MatchPojo;
import com.nlp.model.QAPojo;
import com.nlp.util.RequestValidator;

public class QuestionAnswerHandler {

	/**
	 * validate payload
	 * 
	 * @throws Exception
	 */

	private final static Splitter splitter = Splitter.on('.').omitEmptyStrings().trimResults();

	public void validationPhase(QAPojo request) throws Exception {
		RequestValidator validator = new RequestValidator();
		validator.payloadNullCheck(request);
	}

	public Map<String, String> matchSentenceWithAnswers(String paragraph, List<String> providedAnswers) {

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

	public Map<String, String> findAnswersForQuestions(Map<String, String> interestMap, List<String> questions) {
		Map<String, String> qaMap = new LinkedHashMap<String, String>();
		for (String question : questions) {
			qaMap.put(question, getAnwserForQuestion(interestMap, question));
		}
		return qaMap;
	}

	public String getAnwserForQuestion(Map<String, String> interestMap, String question) {
		MatchPojo match = new MatchPojo();
		MatchPojo anotherMatch = new MatchPojo();

		for (String answerKey : interestMap.keySet()) {
			String sentence = interestMap.get(answerKey);
			int rank = SentenceSimilarityRanker.rank(preProcess(sentence), preProcess(question));
			
			
			if (rank > match.rank) {
				match.set(answerKey, question, sentence, rank);
				System.out.println("match ->"+match);
				anotherMatch.clear();
			} else if (rank == match.rank && rank > Integer.MIN_VALUE) {
				anotherMatch.set(answerKey, question, sentence, rank);
				System.out.println("anothermatch->"+anotherMatch);
			}else{
				System.out.println("skipped");
			}
				
			//System.out.println("rank" + rank + "question->" + question + "answer" + answerKey);
			
		
			
			if (anotherMatch.potentialAnswer != null) {
				// two similar hits. let's try "dumb" word ranking
				int newScore = SentenceSimilarityRanker.bruteforce(preProcess(anotherMatch.interestingSentence),
						preProcess(question));
				int oldScore = SentenceSimilarityRanker.bruteforce(preProcess(match.interestingSentence),
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
