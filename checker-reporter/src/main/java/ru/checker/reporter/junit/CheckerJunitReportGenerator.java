
package ru.checker.reporter.junit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.util.FileUtils;
import ru.checker.reporter.junit.models.JunitReportModel;
import ru.checker.reporter.nunit.models.NUnitTestResults;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckerJunitReportGenerator {

    public static List<Throwable> generateJunitReports(List<JunitReportModel> models) {
        return models.parallelStream().map(model -> {
            try {
                generateJunitReport(model);
                return null;
            } catch (IOException e) {
                log.error("Не удалось создать отчет", e);
                return e;

            }
        }).collect(Collectors.toList());
    }

    public static void generateJunitReport(JunitReportModel model) throws IOException {
        generateReport(model);
    }

    public static void generateNUnitReport(NUnitTestResults model) throws IOException {
        generateReport(model);
    }



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
