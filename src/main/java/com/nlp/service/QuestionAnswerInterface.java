package com.nlp.service;

import java.util.Map;

import com.nlp.model.QAPojo;

public interface QuestionAnswerInterface {

	public Map<String,String> findAnswers(QAPojo pojo) throws Exception;
	
}
