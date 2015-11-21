package com.app.fxa.joke.model;

import com.orm.SugarRecord;

import java.util.Date;

public class Joke extends SugarRecord<Joke> {

    private String content;

    private Date date;

    private String title;

    private int type;

    public String getContent() {
        return this.content;
    }

    public Date getDate() {
        return this.date;
    }

    public String getTitle() {
        return this.title;
    }

    public int getType() {
        return this.type;
    }

    public void setContent(String paramString) {
        this.content = paramString;
    }

    public void setDate(Date paramDate) {
        this.date = paramDate;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }

    public void setType(int paramInt) {
        this.type = paramInt;
    }

    public String toString() {
        StringBuilder localStringBuilder = new StringBuilder();
        return "Joke{title='" + this.title + '\'' + ", content='" + this.content + '\'' + '}';
    }
}
