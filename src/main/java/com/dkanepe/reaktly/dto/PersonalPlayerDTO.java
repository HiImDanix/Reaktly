package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class PersonalPlayerDTO {
    private long ID;
    private String session;
    private String name;


    public PersonalPlayerDTO(long ID, String session, String name) {
        this.ID = ID;
        this.session = session;
        this.name = name;
    }
}
