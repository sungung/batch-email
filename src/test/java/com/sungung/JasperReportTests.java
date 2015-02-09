package com.sungung;

import com.sungung.data.service.BrewerService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsSingleFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class JasperReportTests {

    @Autowired
    private BrewerService brewerService;

    @Test
    public void createPDFTest() throws JRException, IOException {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("REPORT.SOURCE", brewerService.findAll(null));

        AbstractJasperReportsSingleFormatView jasperReportsSingleFormatView = new JasperReportsPdfView();
        jasperReportsSingleFormatView.setUrl("classpath:jrxml/list.jrxml");

        model.put("REPORT.SOURCE", brewerService.findAll(null));
        JasperPrint print = JasperFillManager.fillReport((new ClassPathResource("jrxml/list.jasper")).getInputStream(),
                new HashMap<String, Object>(),
                new JRBeanCollectionDataSource(brewerService.findAll(null)));
        JRExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "list.pdf");
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.exportReport();

    }
}
