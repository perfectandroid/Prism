package com.perfect.prism.Model;

public class TopicModel {

    private String ID_Topic;
    private String TopicName;

    public TopicModel(String ID_Topic, String TopicName) {
        this.TopicName = TopicName;
        this.ID_Topic = ID_Topic;
    }

    public String getID_Topic() {
        return ID_Topic;
    }

    public void setID_Topic(String ID_Client) {
        this.ID_Topic = ID_Topic;
    }

    public String getTopicName() {
        return TopicName;
    }

    public void setTopicName(String TopicName) {
        TopicName = TopicName;
    }
}
