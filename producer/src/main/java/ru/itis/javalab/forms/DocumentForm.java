package ru.itis.javalab.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentForm {
    private String firstName;
    private String lastName;
    private String name;
    private String type;
    private String text;
}
