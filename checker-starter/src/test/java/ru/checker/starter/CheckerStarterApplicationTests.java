package ru.checker.starter;

import com.sun.jna.platform.win32.User32;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class CheckerStarterApplicationTests {

    @Test
    void contextLoads() {
        var handle = User32.INSTANCE.FindWindow("TfProd", " ВЫПУСК ПРОДУКЦИИ");
        System.out.println(handle);
    }

}
