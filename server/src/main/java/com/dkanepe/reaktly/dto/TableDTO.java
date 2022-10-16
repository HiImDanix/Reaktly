package com.dkanepe.reaktly.dto;

import lombok.Data;

@Data
public class TableDTO {
    private String[] headers;
    private String[][] rows;

    public TableDTO(String[] headers, String[][] rows) {
        this.headers = headers;
        this.rows = rows;
    }

}
