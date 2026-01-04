package com.sliit.healthins.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Data
@NoArgsConstructor
public class ReportDTO {
    private String type;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String filePath;
    private byte[] content;
}