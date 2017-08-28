package com.nlp.service;

import java.util.Map;

import com.nlp.model.QAPojo;

/**
 * QA interface
 * @author ramans
 *
 */
public interface QuestionAnswerInterface {

	/**
	 * gets answers for the questions from paragraph
	 * @param pojo
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> findAnswers(QAPojo pojo) throws Exception;
	
}
