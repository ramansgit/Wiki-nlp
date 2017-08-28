package com.nlp.model;

/**
 * pojo class carries match information
 * @author ramans
 *
 */
public class MatchPojo {
    @Override
	public String toString() {
		return "MatchPojo [potentialAnswer=" + potentialAnswer + ", potentialQuestion=" + potentialQuestion
				+ ", interestingSentence=" + interestingSentence + ", rank=" + rank + "]";
	}

	public String potentialAnswer;
	public  String potentialQuestion;
	public String interestingSentence;
    public int rank = Integer.MIN_VALUE;

    public void set(String answer, String question, String sentence, int rank) {
        this.potentialAnswer = answer;
        this.potentialQuestion = question;
        this.interestingSentence = sentence;
        this.rank = rank;
    }

    public void clear() {
        potentialAnswer = potentialQuestion = interestingSentence = null;
        rank = Integer.MIN_VALUE;
    }
}
