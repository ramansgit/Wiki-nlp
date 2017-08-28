package com.nlp.service;

import java.util.Map;

import com.nlp.handlers.QuestionAnswerHandler;
import com.nlp.model.QAPojo;

public class QuestionAnswerService implements QuestionAnswerInterface {

	/**
	 * returns answers
	 * 
	 * @throws Exception
	 */
	public Map<String,String> findAnswers(QAPojo request) throws Exception {

		QuestionAnswerHandler handler = new QuestionAnswerHandler();
		// validation phase
		handler.validationPhase(request);

		// find interesting sentence for the provided answers
		Map<String, String> interestMap = handler.matchSentenceWithAnswers(request.getParagraph(),
				request.getProvidedAnswers());

		return handler.findAnswersForQuestions(interestMap, request.getQuestions());
	}

}
