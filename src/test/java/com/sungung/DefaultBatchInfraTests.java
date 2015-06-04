package com.sungung;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.OutputCapture;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;


/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class DefaultBatchInfraTests {

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testDefaultSettings() {
        assertEquals(0, SpringApplication.exit(SpringApplication.run(Application.class)));
        String output = this.outputCapture.toString();
        assertFalse("Wrong output: " + output, output.contains("Exception in thread"));
    }
}
