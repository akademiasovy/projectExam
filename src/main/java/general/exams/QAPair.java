package general.exams;

import general.database.Answer;
import general.database.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QAPair {

    private Question question;
    private Answer[] answers;
    private int answer;

    public QAPair(Question question) {
        this.question = question;
        this.answers = new Answer[4];
        List<Answer> answerList = new ArrayList<Answer>(this.question.getAnswers());
        Collections.shuffle(answerList);
        answerList.toArray(this.answers);
    }

    public void answer(int answer) {
        this.answer = answer;
    }

    public Answer getAnswer(int number) {
        return this.answers[number];
    }

    public boolean isAnsweredCorrectly() {
        return this.answers[this.answer].isCorrect();
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
