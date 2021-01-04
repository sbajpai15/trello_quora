package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {
        String password = userEntity.getPassword();
        if(password==null){
            userEntity.setPassword("quora@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        if(userEntity==null){
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        return userDao.createUser(userEntity);
    }

    /**
     * method used for get user auth details for the user with right access token.
     *
     * @param authorizationToken access token value
     * @param actionType         action type based on which we have to send various messages
     * @return UserAuthToken Entity object
     * @throws AuthorizationFailedException exception indicating user is not signed in or not signed out.
     */
    public UserAuthTokenEntity getUserByAccessToken(String authorizationToken, ActionType actionType)
            throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthTokenEntity.getLogoutAt() != null) {
            if (actionType.equals(ActionType.CREATE_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            } else if (actionType.equals(ActionType.ALL_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            } else if (actionType.equals(ActionType.EDIT_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            } else if (actionType.equals(ActionType.DELETE_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            } else if (actionType.equals(ActionType.ALL_QUESTION_FOR_USER)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
            } else if (actionType.equals(ActionType.CREATE_ANSWER)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
            } else if (actionType.equals(ActionType.EDIT_ANSWER)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            } else if (actionType.equals(ActionType.DELETE_ANSWER)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
            } else if (actionType.equals(ActionType.GET_ALL_ANSWER_TO_QUESTION)) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
            }
        }
        return userAuthTokenEntity;
    }

}
