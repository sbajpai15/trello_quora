package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity,String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User has not signed in");
        }

        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionDao.createQuestion(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestion(String authorization) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(String questUuid,final QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuth = userDao.getUserAuthToken(authorizationToken);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        QuestionEntity currentQuestionEntity = questionDao.getQuestionByUuid(questUuid);

        if (currentQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (!currentQuestionEntity.getUser().getUuid().equals(userAuth.getUser().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        currentQuestionEntity.setContent(questionEntity.getContent());
        return questionDao.editQuestionContent(currentQuestionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String questUuid, String token) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuth = userDao.getUserAuthToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete the question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questUuid);

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (!questionEntity.getUser().getUuid().equals(userAuth.getUser().getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        return questionDao.deleteQuestion(questionEntity);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionByUser(String uuid, String token) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuth = userDao.getUserAuthToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        UserEntity userEntity = userDao.getUserByUserid(uuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsByUser(userAuth.getUser());

    }

}
