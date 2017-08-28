package com.nlp.service;

import java.util.Map;

import com.nlp.handlers.QuestionAnswerHandler;
import com.nlp.model.QAPojo;

/**
 * gets answers for the questions
 * @author ramans
 *
 */
public class QuestionAnswerService implements QuestionAnswerInterface {
/**
 * gets answer for the given questions from the paragraph
 */
	public Map<String,String> findAnswers(QAPojo request) throws Exception {

		QuestionAnswerHandler handler = new QuestionAnswerHandler();
	
		// find interesting sentence for the provided answers
		Map<String, String> interestMap = handler.matchInterestingSentenceWithAnswers(request.getParagraph(),
				request.getProvidedAnswers());

		return handler.findAnswersForQuestions(interestMap, request.getQuestions());
	}

}
