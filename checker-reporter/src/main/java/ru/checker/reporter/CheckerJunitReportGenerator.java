
package ru.checker.reporter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.checker.reporter.junit.models.JunitReportModel;
import ru.checker.reporter.nunit.models.NUnitTestRun;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Output report generator.
 * @author vd.zinovev
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckerJunitReportGenerator {

    /**
     * Generate JUNIT report
     * @param model JUNIT model
     * @throws IOException Writing exceptions
     */
    public static void generateJunitReport(JunitReportModel model) throws IOException {
        generateReport(model);
    }

    /**
     * Generate NUNIT report
     * @param model NUNIT model
     * @throws IOException Writing exceptions
     */
    public static void generateNUnitReport(NUnitTestRun model) throws IOException {
        generateReport(model);
    }

    /**
     * Generate abstract report.
     * @param model Report model
     * @throws IOException Writing exceptions
     */
    public static void generateReport(Object model) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String root = new File("").getAbsolutePath();
        String path = root
                .substring(0, root.indexOf("Checker"))
                + String.format(
                        "Checker/Reports/Nunit/Report-%s.xml",
                        new SimpleDateFormat("dd-MM-yyy-hh-ss").format(new Date()));
        File pathFile = new File(path);
        if(pathFile.getParentFile().listFiles() != null) {
            Arrays.asList(Objects.requireNonNull(pathFile.getParentFile().listFiles())).parallelStream().forEach(File::delete);
        }
        if (!pathFile.getParentFile().exists())
            if(!pathFile.getParentFile().mkdirs())
                throw new IOException("Не удалось создать директорию с отчетами");
        mapper.writeValue(pathFile, model);
    }
}