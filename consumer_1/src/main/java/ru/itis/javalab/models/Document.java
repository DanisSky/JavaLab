package ru.itis.javalab.models;

import lombok.*;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Document {
    private String firstName;
    private String lastName;
    private String name;
    private String type;
    private String content;
    private LocalDate createdAt;

}
