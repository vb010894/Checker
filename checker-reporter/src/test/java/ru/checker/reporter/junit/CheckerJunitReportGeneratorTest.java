package ru.checker.reporter.junit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import ru.checker.reporter.junit.models.ErrorModel;
import ru.checker.reporter.junit.models.JunitReportModel;
import ru.checker.reporter.junit.models.SkippedModel;
import ru.checker.reporter.junit.models.TestCaseModel;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckerJunitReportGeneratorTest {

    @Test
    void testGeneratedReport() {
        JunitReportModel model = JunitReportModel
                .builder()
                .tests("3")
                .name("Test report")
                .time("21.888")
                .skipped("1")
                .errors("1")
                .failures("0")
                .build();

        TestCaseModel caseModel = TestCaseModel
                .builder()
                .name("test1")
                .className("ru.checker.ssm")
                .time("1.101")
                .build();

        TestCaseModel errorCaseModel = TestCaseModel
                .builder()
                .name("test2")
                .className("ru.checker.ssm")
                .time("1.102")
                .build();

        ErrorModel errorModel = ErrorModel
                .builder()
                .message("not found")
                .type(IllegalAccessError.class.getName())
                .data("error stack")
                .build();

        errorCaseModel.setError(errorModel);

        TestCaseModel skippedTests = TestCaseModel
                .builder()
                .name("test3")
                .className("ru.checker.ssm")
                .time("1.103")
                .build();

        SkippedModel skipped = SkippedModel
                .builder()
                .message("skipped")
                .build();

        skippedTests.setSkipped(skipped);

        model.setTestcase(List.of(caseModel, errorCaseModel, skippedTests));

        XmlMapper mapper = new XmlMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String root = new File("").getAbsolutePath();
        String path = root.substring(0, root.indexOf("Checker")) + "Checker/Reposts/Junit/Report.xml";
        File pathFile = new File(path);
        if (!pathFile.getParentFile().exists())
            assertTrue(pathFile.getParentFile().mkdirs(), "Не удалось создать директорию для тестов - " + pathFile.getParentFile().getAbsolutePath());


        assertDoesNotThrow(() -> mapper.writeValue(pathFile, model), "Не удалось сохранить отчет");
    }

}