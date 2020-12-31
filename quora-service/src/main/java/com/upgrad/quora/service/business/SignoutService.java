package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignoutService {

    //search for auth token using UserDao

    //  signout method accepts the decoded JWT token and return auth token entity similar to how authentication service.autheticate  returns user authtoken entity

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signout(final String authToken) throws SignOutRestrictedException {

        //calls User DAO to get user auth token entity for the given user auth token if it exists
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authToken);
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");

        } else {

            //update logout time of the user
            final ZonedDateTime now = ZonedDateTime.now();

            userAuthTokenEntity.setLogoutAt(now);
            return userAuthTokenEntity;
        }
    }
}
