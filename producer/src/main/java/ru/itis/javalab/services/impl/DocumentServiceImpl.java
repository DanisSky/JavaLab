package ru.itis.javalab.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.javalab.exceptions.NoSuchRoutingKeyException;
import ru.itis.javalab.exceptions.ReportGeneratingException;
import ru.itis.javalab.forms.DocumentForm;
import ru.itis.javalab.models.Document;
import ru.itis.javalab.rabbit.RabbitOption;
import ru.itis.javalab.rabbit.RoutingKeys;
import ru.itis.javalab.repositories.DocumentRepository;
import ru.itis.javalab.security.jwt.JwtAuthenticationProvider;
import ru.itis.javalab.services.DocumentService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public ByteArrayOutputStream getReport(DocumentForm documentForm) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String routingKey;
            switch (documentForm.getType()) {
                case "type_1":
                    routingKey = RoutingKeys.PDF_TYPE_1.toString();
                    break;
                case "type_2":
                    routingKey = RoutingKeys.PDF_TYPE_2.toString();
                    break;
                default:
                    throw new NoSuchRoutingKeyException("No such routing key for: " + documentForm.getType());

            }
            byte[] bytes = (byte[]) rabbitTemplate.convertSendAndReceive(RabbitOption.EXCHANGE, routingKey, documentForm);
            assert bytes != null;
            outputStream.write(bytes);


            documentRepository.save(Document.builder()
                    .type(documentForm.getType())
                    .user(jwtAuthenticationProvider.getUser())
                    .date(LocalDate.now())
                    .build()
            );

            return outputStream;
        } catch (IOException ex) {
            log.error("generation exception : " + ex);
            throw new ReportGeneratingException(" Error in creating report: %s " + ex.getMessage(), ex.getCause());
        }
    }

}
