package com.mondoralabs.workshop04.Workshop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import org.springframework.data.annotation.Id;

/**
 * Workshop
 */
public class Workshop {

    @Id
    @JsonProperty(access = Access.READ_ONLY)
    public String id;

    public String title;

    public String content;

    public String author;

    public String created;

}
