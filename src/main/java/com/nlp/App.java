package com.nlp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.nlp.model.QAPojo;
import com.nlp.service.QuestionAnswerInterface;
import com.nlp.service.QuestionAnswerService;

public class App {

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		QAPojo request = getInputFromFile();

		System.out.println(request);

		QuestionAnswerInterface qaInterface = new QuestionAnswerService();

		Map<String, String> qa = qaInterface.findAnswers(request);

		for (String question : qa.keySet()) {
			System.out.println(question + "->" + qa.get(question));
		}

	}

	public static QAPojo getInputFromFile() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("zebra.txt"));
		QAPojo pojo = new QAPojo();
		String line = null;
		int count = 1;
		String paragraph = "";
		List<String> questions = new ArrayList<String>();
		String providedAnswers = "";
		while ((line = br.readLine()) != null) {
			if (count == 1) { // paragraph
				paragraph = line;
			}
			if (count > 1 && count < 7) { // reading questions
				questions.add(line);
			}
			if (count == 7) {
				providedAnswers = line;
			}

			count++;
		}

		br.close();

		final Splitter splitter = Splitter.on(';').omitEmptyStrings().trimResults();

		pojo.setParagraph(paragraph);
		pojo.setProvidedAnswers(splitter.splitToList(providedAnswers));
		pojo.setQuestions(questions);

		return pojo;
	}
}
