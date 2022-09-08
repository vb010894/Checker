package ru.checker.tests.desktop.utils;

import com.sun.jna.platform.win32.WinDef;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import mmarquee.automation.Element;
import mmarquee.automation.controls.mouse.AutomationMouse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;


/**
 * Инструмент манипуляций с периферией.
 *
 * @author vd.zinovev
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public final class CheckerDesktopManipulator {


    /**
     * Инструмент для работы с клавиатурой.
     *
     * @author vd.zinovev
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Keyboard {
        /**
         * Вставляет текст в элемент с отчисткой элемента по умолчанию.
         *
         * @param element Элемент для ввода текста
         * @param text    Вводимый текст
         */
        public static void sendText(Element element, String text) {
            sendText(element, text, true);
        }

        /**
         * Вставляет текст в элемент.
         *
         * @param element   Элемент для ввода текста
         * @param text      Вводимый текст
         * @param needClear требуется ли очистка элемента
         */
        public static void sendText(Element element, String text, boolean needClear) {
            log.debug("Фокусировка над элементом");
            element.setFocus();
            log.debug("Элемент сфокусирован");
            sendText(text, needClear);
        }

        /**
         * Вводит текст с клавиатуры !!! с выключенной отчисткой по умолчанию!!!
         *
         * @param text Вводимый текст
         */
        public static void sendText(String text) {
            sendText(text, false);
        }

        /**
         * Вводит текст с клавиатуры.
         *
         * @param text      Вводимый текст
         * @param needClear нужны ли действия по очистке
         */
        @SneakyThrows
        public static void sendText(String text, boolean needClear) {
            Robot robot = new Robot();

            if (needClear) {
                log.debug("Очистка элемента");
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_A);

                robot.keyPress(KeyEvent.VK_DELETE);
                robot.keyRelease(KeyEvent.VK_DELETE);
                log.debug("Элемент отчищен");
            }

            log.debug("Ввод текста '{}'", text);
            for (char c : text.toCharArray()) {
                int key = KeyStroke.getKeyStroke(String.valueOf(c)).getKeyCode();
                robot.keyPress(key);
                robot.keyRelease(key);
            }
            log.debug("Текст '{}' успешно введен", text);
        }

        /**
         * Посылает комбинацию клавиш над элементом.
         * В маске клавиши следует разделять символом '|'.
         *
         * @param element Текущий элемент
         * @param mask    Маска
         */
        public static void sendKeys(Element element, String mask) {
            element.setFocus();
            sendKeys(mask);
        }

        /**
         * Посылает комбинацию клавиш.
         * В маске клавиши следует разделять символом '|'.
         *
         * @param mask Маска клавиш
         */
        @SneakyThrows
        public static void sendKeys(String mask) {
            log.debug("Ввод комбинации кнопок - '{}'", mask);

            Robot r = new Robot();
            Arrays.asList(mask.split("\\|")).forEach(key -> {
                int keys = KeyStroke.getKeyStroke(key.trim()).getKeyCode();
                r.keyPress(keys);
            });
            Arrays.asList(mask.split("\\|")).forEach(key -> {
                int keys = KeyStroke.getKeyStroke(key.trim()).getKeyCode();
                r.keyRelease(keys);
            });

            log.debug("Комбинация клавиш успешно выполнена");

        }
    }

    /**
     * Инструмент для работы с мышью.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Mouse {

        /**
         * Клик мышью по координатам.
         *
         * @param x Координата X
         * @param y Координата Y
         */
        public static void click(int x, int y) {
            log.debug("Нажатие мышью (Левая кнопка) по координатам. X - '{}', Y - '{}'", x, y);
            AutomationMouse.getInstance().setLocation(x, y);
            AutomationMouse.getInstance().leftClick();
            log.debug("Нажатие левой кнопкой мыши выполнено");
        }

        /**
         * Нажатие левой кнопкой мыши с помощью Point
         *
         * @param point Точка нажатия
         * @see Point - AWT
         */
        public static void click(Point point) {
            click(point.x, point.y);
        }


        /**
         * Нажатие левой кнопкой мыши с помощью POINT
         *
         * @param point Точка нажатия
         * @see com.sun.jna.platform.win32.WinDef.POINT - User32
         */
        public static void click(WinDef.POINT point) {
            click(point.x, point.y);
        }

        /**
         * Двойное нажатие левой кнопкой мыши по координатам.
         *
         * @param x Координата X
         * @param y Координата Y
         */
        public static void doubleClick(int x, int y) {
            log.debug("Двойное нажатие мышью (Левая кнопка) по координатам. X - '{}', Y - '{}'", x, y);
            AutomationMouse.getInstance().setLocation(x, y);
            AutomationMouse.getInstance().doubleLeftClick();
            log.debug("Двойное нажатие левой кнопкой мыши выполнено");
        }

        /**
         * Двойное нажатие левой кнопкой мыши с помощью Point
         *
         * @param point Точка нажатия
         * @see Point - AWT
         */
        public static void doubleClick(Point point) {
            doubleClick(point.x, point.y);
        }


        /**
         * Двойное нажатие левой кнопкой мыши с помощью POINT
         *
         * @param point Точка нажатия
         * @see com.sun.jna.platform.win32.WinDef.POINT - User32
         */
        public static void doubleClick(WinDef.POINT point) {
            doubleClick(point.x, point.y);
        }

        /**
         * Двойное нажатие правой кнопкой мыши по координатам.
         *
         * @param x Координата X
         * @param y Координата Y
         */
        public static void rightClick(int x, int y) {
            log.debug("Двойное нажатие мышью (Левая кнопка) по координатам. X - '{}', Y - '{}'", x, y);
            AutomationMouse.getInstance().setLocation(x, y);
            AutomationMouse.getInstance().rightClick();
            log.debug("Двойное нажатие левой кнопкой мыши выполнено");
        }

        /**
         * Двойное нажатие правой кнопкой мыши с помощью Point
         *
         * @param point Точка нажатия
         * @see Point - AWT
         */
        public static void rightClick(Point point) {
            rightClick(point.x, point.y);
        }


        /**
         * Двойное нажатие правой кнопкой мыши с помощью POINT
         *
         * @param point Точка нажатия
         * @see com.sun.jna.platform.win32.WinDef.POINT - User32
         */
        public static void rightClick(WinDef.POINT point) {
            rightClick(point.x, point.y);
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static final class Screen {

        Robot robot;

        /**
         * Конструктор.
         *
         * @throws AWTException Возникает, при ошибке класса Robot.
         *                      Необходимо проверить системную переменную "java.awt.headless" со значением "false"
         */
        public Screen() throws AWTException {
            this.robot = new Robot();
        }

        /**
         * Получает скриншот дисплея.
         *
         * @return Скриншот дисплея
         */
        public BufferedImage getScreenshot() {
            log.debug("Получение скриншота дисплея");
            Rectangle rectangle = new Rectangle(0,
                    0,
                    Toolkit.getDefaultToolkit().getScreenSize().width,
                    Toolkit.getDefaultToolkit().getScreenSize().height);

            BufferedImage screen =  this.robot.createScreenCapture(rectangle);
            log.debug("Скриншот получен. Размер - {}x{}. ", rectangle.width, rectangle.height);
            return screen;
        }

        /**
         * Получает цвет пикселя по координатам X, Y.
         *
         * @param x Координата X
         * @param y Координата Y
         * @return Цвет
         */
        public Color getPixelColor(int x, int y) {
            log.debug("Получение цвета пикселя. X - {}, Y - {}", x, y);
            Color color = this.robot.getPixelColor(x, y);
            log.debug("Цвет получен. RGB - {}", color.toString());
            return color;
        }

        /**
         * Получает цвет пикселя по точке.
         *
         * @param point Точка
         * @return Цвет
         */
        public Color getPixelColor(Point point) {
            return this.getPixelColor(point.x, point.y);
        }

        /**
         * Получает цвет пикселя по WIN.DEF точке.
         *
         * @param point Точка
         * @return Цвет
         */
        public Color getPixelColor(WinDef.POINT point) {
            return this.getPixelColor(point.x, point.y);
        }

        /**
         * Получает цвет пикселя по координатам X, Y в виде double.
         *
         * @param x Координата X
         * @param y Координата Y
         * @return Цвет
         */
        public Color getPixelColor(double x, double y) {
            return this.getPixelColor((int) x, (int) y);
        }
    }
}
