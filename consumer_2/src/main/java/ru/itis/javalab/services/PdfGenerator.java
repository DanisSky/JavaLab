package ru.itis.javalab.services;


import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.itis.javalab.exceptions.PdfException;
import ru.itis.javalab.forms.DocumentForm;
import ru.itis.javalab.models.Document;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdfGenerator {

    @Value("${jasper.path-to-template.pdf}")
    private String report;

    public byte[] createPdf(DocumentForm documentForm) {
        try {
            Document document = Document.builder()
                    .name(documentForm.getName())
                    .type(documentForm.getType())
                    .content(documentForm.getText())
                    .createdAt(LocalDate.now())
                    .firstName(documentForm.getFirstName())
                    .lastName(documentForm.getLastName())
                    .build();

            List<Document> documents = Collections.singletonList(document);
            File file = ResourceUtils.getFile(report);

            JasperReport compileReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(documents);
            Map<String, Object> parameters = new HashMap<>();

            parameters.put("poweredBy", "Java Lab");
            JasperPrint jasperPrint = JasperFillManager.fillReport(compileReport, parameters, dataSource);
            byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

            return data;
        } catch (JRException | IOException e) {
            log.error("Произошла ошибка генерации документа JasperReports: " + e);
            throw new PdfException("Ошибка в генерации PDF отчета из-за: ", e.getCause());
        }
    }
}
