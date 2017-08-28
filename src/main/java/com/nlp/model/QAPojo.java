package com.nlp.model;

import java.util.List;

public class QAPojo {

	@Override
	public String toString() {
		return "QAPojo [paragraph=" + paragraph + ", questions=" + questions + ", providedAnswers=" + providedAnswers
				+ "]";
	}

	public List<String> getProvidedAnswers() {
		return providedAnswers;
	}

	public void setProvidedAnswers(List<String> providedAnswers) {
		this.providedAnswers = providedAnswers;
	}

	private String paragraph;

	private List<String> questions;

	private List<String> providedAnswers;

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}

}
