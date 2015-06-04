package com.sungung;

import com.sungung.data.service.BrewerService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsSingleFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

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

        AbstractJasperReportsSingleFormatView jasperReportsSingleFormatView = new JasperReportsPdfView();

        jasperReportsSingleFormatView.setUrl("classpath:jrxml/list.jrxml");

        JasperPrint print = JasperFillManager.fillReport((new ClassPathResource("jrxml/list.jasper")).getInputStream(),
                new HashMap<String, Object>(),
                new JRBeanCollectionDataSource(brewerService.findAll(null)));

        File output = new File("list.pdf");

        OutputStream os = new FileOutputStream(output);

        JasperExportManager.exportReportToPdfStream(print, os);

        assertTrue("File must have some contents", output.length() > 0L);

    }

}
