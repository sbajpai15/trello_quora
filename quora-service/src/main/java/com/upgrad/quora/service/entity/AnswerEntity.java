package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name="answer", schema = "public")
@NamedQueries(
        {
                @NamedQuery(name="getAnswerForUuid", query = "SELECT a FROM AnswerEntity a WHERE a.uuid=:uuid"),
                @NamedQuery(name="getAnswerForQuestion", query = "SELECT a FROM AnswerEntity a WHERE a.question=:question")
        }
)
public class AnswerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "ans")
    @Size(max = 255)
    @NotNull
    private String answer;

    @Column(name = "date")
    private ZonedDateTime date;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    /**
     * GET method for property id
     *
     * @return id value
     */
    public Integer getId() {
        return id;
    }

    /**
     * SET method for property id
     *
     * @param id id value to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * GET method for property uuid
     *
     * @return uuid value
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * SET method for property uuid
     *
     * @param uuid string value to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * GET method for property answer
     *
     * @return Answer value
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * SET method for property answer
     *
     * @param answer String value to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * GET method for property date
     *
     * @return date value
     */
    public ZonedDateTime getDate() {
        return date;
    }

    /**
     * SET method for property date
     *
     * @param date date value to set
     */
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    /**
     * GET method for property user
     *
     * @return UserEntity value
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * SET method for property user
     *
     * @param user UserEntity object value to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * GET method for property question
     *
     * @return Question object
     */
    public QuestionEntity getQuestion() {
        return question;
    }

    /**
     * SET method for property question.
     *
     * @param question question object value to set
     */
    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}
