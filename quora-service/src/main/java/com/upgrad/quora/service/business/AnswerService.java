package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AnswerService {

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    /**
     * method use for creating an answer.
     *
     * @param answer       String value
     * @param questionUuId Id value for Question
     * @param authToken    authorization Token value
     * @return answer object
     * @throws AuthorizationFailedException exception thrown if user is not authorized
     * @throws InvalidQuestionException     exception thrown if question is not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answer, String questionUuId, final String authToken) throws AuthorizationFailedException, InvalidQuestionException {

        //Checks user signin status based on accessToken provided
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);

        if (userAuthTokenEntity != null) {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
            }

            //Returns question based on questionId if exists
            QuestionEntity question = questionDao.getQuestionByUuid(questionUuId);


            if (question != null) {
                answer.setQuestion(question);
                //Creates Answer based on input
                UserEntity userEntity = userDao.getUserByUserid(userAuthTokenEntity.getUuid());
                if (userEntity != null) {
                    answer.setUser(userEntity);
                    return answerDao.createAnswer(answer);
                }

            } else {
                throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    /**
     * method used for getting Answer for UUid.
     *
     * @param answerUuId answerUuid String
     * @return Answer object
     * @throws AnswerNotFoundException exception thrown if answer not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerForUuId(String answerUuId) throws AnswerNotFoundException {
        AnswerEntity answer = answerDao.getAnswerForUuId(answerUuId);
        if (answer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else {
            return answer;
        }
    }

    /**
     * method used for editing answer
     *
     * @param answer     Answer object
     * @param answerUuId id value for Answer Object
     * @param authToken  authorization Token
     * @return edited Answer object
     * @throws AuthorizationFailedException exception thrown if user is not authorized
     * @throws AnswerNotFoundException      exception thrown if answer is not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(AnswerEntity answer, String answerUuId, final String authToken) throws AuthorizationFailedException, AnswerNotFoundException {

        //Checks user signed in status, validates answerId and checks if the signin user is the answer owner
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);

        if (userAuthTokenEntity != null) {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
            }

            AnswerEntity existingAnswer = answerDao.getAnswerForUuId(answerUuId);

            if (existingAnswer != null) {
                UserEntity userEntity = userDao.getUserByUserid(userAuthTokenEntity.getUuid());
                if (existingAnswer.getUser().getId().equals(userEntity.getId())) {
                    return answerDao.editAnswer(existingAnswer);
                } else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    /**
     * method used for deleting answer
     *
     * @param answer     Answer object
     * @param answerUuId id value for Answer Object
     * @param authToken  authorization Token
     * @throws AuthorizationFailedException exception thrown if user is not authorized
     * @throws AnswerNotFoundException      exception thrown if answer is not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(AnswerEntity answer, String answerUuId, final String authToken) throws AuthorizationFailedException, AnswerNotFoundException {

        //Checks user signin status based on accessToken provided
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);

        if (userAuthTokenEntity != null) {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
            }

            //Returns question based on questionId if exists
            answer = answerDao.getAnswerForUuId(answerUuId);

            if (answer != null) {
                //Creates Answer based on input
                UserEntity userEntity = userDao.getUserByUserid(userAuthTokenEntity.getUuid());
                if (userEntity.getRole().equalsIgnoreCase("admin") || (answer.getUser().getUserName() == userEntity.getUserName())) {
                    System.out.println("Hello Shrish1");
                    answerDao.deleteAnswer(answer);
                    System.out.println("Hello Shrish2");
                } else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    /**
     * method used for getting answer for a specific question.
     *
     * @param questionUuId question uuid String object
     * @param authToken    authorization Token
     * @return List of Answer for the specicif question
     * @throws AuthorizationFailedException exception thrown if user is not authorized
     * @throws AnswerNotFoundException      exception thrown if answer not found
     * @throws InvalidQuestionException     exception thrown if question is not found
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAnswersForQuestion(String questionUuId, final String authToken) throws AuthorizationFailedException, AnswerNotFoundException, InvalidQuestionException {

        //Checks user signin status based on accessToken provided
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);

        if (userAuthTokenEntity != null) {
            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
            }

            //check if the question exists in question database
            QuestionEntity question = questionDao.getQuestionByUuid(questionUuId);
            if (question == null) {
                throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
            }

            //get the list of Answers for question
            List<AnswerEntity> answerList = answerDao.getAnswersForQuestion(questionUuId);
            if (answerList == null) {
                throw new AnswerNotFoundException("OTHR-001", "No Answers available for the given question uuid");
            } else {
                return answerList;
            }
        }
        throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    /**
     * method used for checking if user is an owner of the answer.
     * This method is used in both edit and deleting an answer and throws
     * specific messaged in case an exception in delete or edit mode.
     *
     * @param answerUuId     answerUuid String
     * @param authorizedUser access token of user
     * @param authToken      enum used to identify whether it is a edit or delete action
     * @return Answer object
     * @throws AnswerNotFoundException      exception thrown if answer not found
     * @throws AuthorizationFailedException exception thrown if user is not authorized for editing and deleting answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity isUserAnswerOwner(String answerUuId, UserAuthTokenEntity authorizedUser, ActionType authToken) throws AnswerNotFoundException, AuthorizationFailedException {
        AnswerEntity answer = answerDao.getAnswerForUuId(answerUuId);

        if (answer == null) {
            //if provided answer uuid is not present in database, then throw this exception
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else if (!authorizedUser.getUser().getUuid().equals(answer.getUser().getUuid())) {
            if (ActionType.EDIT_ANSWER.equals(authToken)) {
                //if users dont match and action is for editing then throw exception with below message
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            } else {
                //means we are in DELETE action and we should throw separate message for the exception
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        } else if ((!authorizedUser.getUser().getRole().equals("admin")
                && !authorizedUser.getUser().getUuid().equals(answer.getUser().getUuid()))
                && ActionType.DELETE_ANSWER.equals(authToken)) {
            //In delete mode if role is not admin or the user is not owner of the answer then throw below exception
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        } else {
            return answer;
        }
    }


}