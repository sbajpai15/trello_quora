package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    AnswerService answerService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserAdminBusinessService userAdminBusinessService;

    /**
     * Rest API endpoint method for creating an answer for a question
     *
     * @param answerRequest Answer request object
     * @param questionUuId  question uuid object as String
     * @param authorization access Token for user authorization
     * @return ResponseEntity object with AnswerResponse Object
     * @throws AuthorizationFailedException exception thrown if user Authorization failed
     * @throws InvalidQuestionException     thrown if question is invalid
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionUuId,
                                                       @RequestHeader final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        String [] bearerToken = authorization.split("Bearer ");

        //Authorize the user
        UserAuthTokenEntity authorizedUser = userAdminBusinessService.getUserByAccessToken(authorization, ActionType.CREATE_QUESTION);

        //Create answer object
        AnswerEntity answer = new AnswerEntity();
        answer.setAnswer(answerRequest.getAnswer());
        answer.setUuid(UUID.randomUUID().toString());
        answer.setUser(authorizedUser.getUser());
        ZonedDateTime now = ZonedDateTime.now();
        answer.setDate(now);

        //Send the answer object from creation in database
        AnswerEntity createdAnswer = answerService.createAnswer(answer, questionUuId, bearerToken[0]);

        //create answer reponse object
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }


    /**
     * Rest API endpoint method for editing an answer for a question
     *
     * @param answerEditRequest edit request
     * @param answerUuId        answerUuid to be edited
     * @param authorization     user uuid for authorization
     * @return ResponseEntity class with AnswerEditResponse
     * @throws AuthorizationFailedException exception thrown if user Authorization failed
     * @throws AnswerNotFoundException      thrown if answer uuid is not found
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(AnswerEditRequest answerEditRequest,
                                                         @PathVariable("answerId") final String answerUuId,
                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        //Authorize the user if he has signed in properly
        UserAuthTokenEntity authorizedUser = userAdminBusinessService.getUserByAccessToken(authorization, ActionType.EDIT_ANSWER);

        //get the answer Object after checking if user if owner of the answer
        AnswerEntity answer = answerService.isUserAnswerOwner(answerUuId, authorizedUser, ActionType.EDIT_ANSWER);
        //set the details that needs to updated in database
        answer.setAnswer(answerEditRequest.getContent());
        answer.setDate(ZonedDateTime.now());
        AnswerEntity editedAnswer = answerService.editAnswer(answer, answerUuId, bearerToken[0]);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse()
                .id(answerUuId)
                .status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }


    /**
     * Rest API endpoiint fmethod for deleting a answer.
     * Only and answer owner or an admin can delete this
     *
     * @param answerUuId    answeruuid string
     * @param authorization acces token of user
     * @return ResponseEntity object with AnswerDeleteResponse
     * @throws AuthorizationFailedException exception thrown if user Authorization failed
     * @throws AnswerNotFoundException      thrown if answer uuid is not found
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerUuId,
                                                             @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        //Authorize the user if he has signed in properly
        UserAuthTokenEntity authorizedUser = userAdminBusinessService.getUserByAccessToken(authorization, ActionType.DELETE_ANSWER);
        //Check if the user is himself or an admin trying to delete the answer
        AnswerEntity answer = answerService.isUserAnswerOwner(answerUuId, authorizedUser, ActionType.DELETE_ANSWER);
        answerService.deleteAnswer(answer,answerUuId,bearerToken[0]);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse()
                .id(answerUuId)
                .status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }


    /**
     * Rest API endpoint method for getting all answers for a specific question.
     *
     * @param questionId    question uuid String
     * @param authorization accesstoken of the user
     * @return ResponseEntity object with AnswerDetailsResponse object
     * @throws AuthorizationFailedException exception thrown if user Authorization failed
     * @throws InvalidQuestionException     thrown is question is invalid
     * @throws AnswerNotFoundException      thrown if answer uuid is not found
     */
    @RequestMapping(method = RequestMethod.GET, path = "/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId,
                                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        //Authorize the user if he has signed in properly
        UserAuthTokenEntity authorizedUser = userAdminBusinessService.getUserByAccessToken(authorization, ActionType.GET_ALL_ANSWER_TO_QUESTION);
        List<AnswerEntity> answerList = answerService.getAnswersForQuestion(questionId, bearerToken[0]);
        StringBuilder contentBuilder = new StringBuilder();
        getContentsString(answerList, contentBuilder);
        StringBuilder uuIdBuilder = new StringBuilder();
        String questionContentValue = getUuIdStringAndQuestionContent(answerList, uuIdBuilder);
        AnswerDetailsResponse response = new AnswerDetailsResponse()
                .id(uuIdBuilder.toString())
                .answerContent(contentBuilder.toString())
                .questionContent(questionContentValue);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * private utility method for appending the uuid of answers.
     *
     * @param answerList  List of questions
     * @param uuIdBuilder StringBuilder object
     */
    public static final String getUuIdStringAndQuestionContent(List<AnswerEntity> answerList, StringBuilder uuIdBuilder) {
        String questionContent = new String();
        for (AnswerEntity answerObject : answerList) {
            uuIdBuilder.append(answerObject.getUuid()).append(",");
            questionContent = answerObject.getQuestion().getContent();
        }
        return questionContent;
    }

    /**
     * private utility method for providing contents string in appended format
     *
     * @param answerList list of questions
     * @param builder    StringBuilder with appended content list.
     */
    public static final StringBuilder getContentsString(List<AnswerEntity> answerList, StringBuilder builder) {
        for (AnswerEntity answerObject : answerList) {
            builder.append(answerObject.getAnswer()).append(",");
        }
        return builder;
    }

}
