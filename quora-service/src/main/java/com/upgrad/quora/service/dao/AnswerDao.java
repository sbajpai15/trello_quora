package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * method used to create answer record in database.
     *
     * @param answer answer Object
     * @return answer object
     */
    public AnswerEntity createAnswer(AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }

    /**
     * method used for getting answer for a uuid.
     * returns null if object does not exist
     *
     * @param answerUuId answeruuid string object
     * @return Answer object for the specific uuid
     */
    public AnswerEntity getAnswerForUuId(String answerUuId) {
        try {
            return entityManager
                    .createNamedQuery("getAnswerForUuid", AnswerEntity.class)
                    .setParameter("uuid", answerUuId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * method used for editing and answer.
     *
     * @param answer answer object to be edited
     * @return edited answer object
     */
    public AnswerEntity editAnswer(AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }

    /**
     * method used for deleting an answer.
     *
     * @param answer answer object to be deleted
     * @return answer Entity
     */
    public AnswerEntity  deleteAnswer(AnswerEntity answer) {
        entityManager.remove(answer);
        return answer;
    }

    /**
     * method used for getting answer for a specific question from database.
     * returns null if no answers are there in the database for the specific question.
     *
     * @param question to pass id of question Entity for whom the answer list is required
     * @return List of answers
     */
    public List<AnswerEntity> getAnswersForQuestion(QuestionEntity question) {
        try {
            return entityManager.createNamedQuery("getAnswerForQuestion", AnswerEntity.class)
                    .setParameter("question", question)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}

