package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUserProf(final String uuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = userDao.getUserByUserid(uuid);
        if(userEntity==null){
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        try{
            String accessToken = userAuthTokenEntity.getAccessToken();
        } catch (NullPointerException e){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthTokenEntity.getUuid().equals(userEntity.getUuid())){

        }
        else{
            throw new AuthorizationFailedException("ATHR-002", "User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return userAuthTokenEntity.getUser();

    }
}
