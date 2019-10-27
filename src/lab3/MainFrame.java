package lab3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import javax.swing.*;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private Double[] coefficients;

    private JFileChooser fileChooser = null;

    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem saveToCSVMenuItem;

    private JTextField textFieldFrom;
    private JTextField textFieldTo;
    private JTextField textFieldStep;

    private Box hBoxResult;

    // Визуализатор ячеек таблицы
    private GornerTableCellRenderer renderer = new GornerTableCellRenderer();

    // Модель данных с результатами вычислений
    private GornerTableModel data;

    private String addExtension(String filename, String extension){
        if (filename.endsWith("." + extension)) return filename;
        return filename + "." + extension;
    }

    public static DecimalFormat getFormatter(){
        return formatter;
    }

    public MainFrame(Double[] coefficients) {
        super("Табулирование многочлена на отрезке по схеме Горнера");

        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        DecimalFormatSymbols dottedDouble = formatter.getDecimalFormatSymbols();
        dottedDouble.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(dottedDouble);

        this.coefficients = coefficients;
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        JMenu tableMenu = new JMenu("Таблица");
        menuBar.add(tableMenu);

        Action saveToTextAction = new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));

                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                    saveToTextFile(fileChooser.getSelectedFile());
            }
        };
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        saveToTextMenuItem.setEnabled(false);

        Action saveToGraphicsAction = new AbstractAction("Сохранить данные для построения графика") {

            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    saveToGraphicsFile(fileChooser.getSelectedFile());
                }
            }
        };
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
        saveToGraphicsMenuItem.setEnabled(false);

        Action saveToCSV = new AbstractAction("Сохранить в CSV файл") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileChooser == null){
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if(fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
                    saveToCSVFile(fileChooser.getSelectedFile(), ";");
                }
            }
        };
        saveToCSV.setEnabled(false);
        saveToCSVMenuItem = fileMenu.add(saveToCSV);

        Action searchValueAction = new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
                String value = JOptionPane.showInputDialog(MainFrame.this, "Введите значение для поиска",
                                "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                renderer.setNeedle(value);
                getContentPane().repaint();
            }
        };
        searchValueAction.setEnabled(false);

        Action searchRange = new AbstractAction("Поиск в диапазоне") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(MainFrame.this,
                        "Введите диапазон (два числа через запятую)", "Поиск в диапазоне", JOptionPane.QUESTION_MESSAGE);
                String[] range = input.split(",");
                if(range.length != 2) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Некорректный ввод",
                            "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                }
                else{
                    renderer.setRange(Double.parseDouble(range[0]), Double.parseDouble(range[1]));
                    getContentPane().repaint();
                }
            }
        };
        searchRange.setEnabled(false);
        tableMenu.add(searchValueAction);
        tableMenu.add(searchRange);

        JMenu referenceMenu = new JMenu("Справка");
        Action aboutProgramAction = new AbstractAction("О программе") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Box mainPanel = Box.createVerticalBox();

                Box labelBox = Box.createHorizontalBox();
                JLabel l1 = new JLabel("Автор: Коробкин Александр, 6 группа");
                l1.setFont(new Font("TimesRoman", Font.ROMAN_BASELINE, 20));
                labelBox.add(Box.createHorizontalGlue());
                labelBox.add(l1);
                labelBox.add(Box.createHorizontalGlue());


                ImageIcon icon = new ImageIcon("author.jpg");
                JLabel image = new JLabel(icon);
                Box imageBox = Box.createHorizontalBox();
                imageBox.add(Box.createHorizontalGlue());
                imageBox.add(image);
                imageBox.add(Box.createHorizontalGlue());

                mainPanel.add(Box.createVerticalGlue());
                mainPanel.add(labelBox);
                mainPanel.add(Box.createVerticalStrut(10));
                mainPanel.add(imageBox);
                mainPanel.add(Box.createVerticalGlue());


                JOptionPane.showMessageDialog(MainFrame.this,
                        mainPanel, "О программе", JOptionPane.INFORMATION_MESSAGE);
            }
        };



        referenceMenu.add(aboutProgramAction);
        menuBar.add(referenceMenu);

        JLabel labelForFrom = new JLabel("X изменяется на интервале от:");
        textFieldFrom = new JTextField("0.0", 10);
        textFieldFrom.setMaximumSize(textFieldFrom.getPreferredSize());
        JLabel labelForTo = new JLabel("до:");
        textFieldTo = new JTextField("1.0", 10);
        textFieldTo.setMaximumSize(textFieldTo.getPreferredSize());
        JLabel labelForStep = new JLabel("с шагом:");
        textFieldStep = new JTextField("0.1", 10);
        textFieldStep.setMaximumSize(textFieldStep.getPreferredSize());
        Box hboxRange = Box.createHorizontalBox();
        hboxRange.setBorder(BorderFactory.createBevelBorder(1));
        hboxRange.add(Box.createHorizontalGlue());
        hboxRange.add(labelForFrom);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldFrom);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(labelForTo);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldTo);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(labelForStep);
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldStep);
        hboxRange.add(Box.createHorizontalGlue());

        hboxRange.setPreferredSize(new Dimension(new Double(hboxRange.getMaximumSize().getWidth()).intValue(),
                new Double(hboxRange.getMinimumSize().getHeight()).intValue() * 2));
        // Установить область в верхнюю (северную) часть компоновки
        getContentPane().add(hboxRange, BorderLayout.NORTH);

        JButton buttonCalc = new JButton("Вычислить");
        buttonCalc.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    Double from = Double.parseDouble(textFieldFrom.getText());
                    Double to = Double.parseDouble(textFieldTo.getText());
                    Double step = Double.parseDouble(textFieldStep.getText());
                    data = new GornerTableModel(from, to, step, MainFrame.this.coefficients);
                    JTable table = new JTable(data);
                    table.setDefaultRenderer(Double.class, renderer);
                    table.setRowHeight(30);
                    hBoxResult.removeAll();
                    // Добавить в hBoxResult таблицу, "обѐрнутую" в панель с полосами прокрутки
                    hBoxResult.add(new JScrollPane(table));
                    // Обновить область содержания главного окна
                    getContentPane().validate();
                    // Пометить ряд элементов меню как доступных
                    saveToTextMenuItem.setEnabled(true);
                    saveToGraphicsMenuItem.setEnabled(true);
                    saveToCSVMenuItem.setEnabled(true);
                    searchRange.setEnabled(true);
                    searchValueAction.setEnabled(true);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JButton buttonReset = new JButton("Очистить поля");

        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                textFieldFrom.setText("0.0");
                textFieldTo.setText("1.0");
                textFieldStep.setText("0.1");
                hBoxResult.removeAll();
                hBoxResult.add(new JPanel());
                saveToTextMenuItem.setEnabled(false);
                saveToGraphicsMenuItem.setEnabled(false);
                saveToCSVMenuItem.setEnabled(false);

                searchRange.setEnabled(false);
                searchValueAction.setEnabled(false);

                getContentPane().validate();
            }
        });
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.setPreferredSize(new Dimension(new
                Double(hboxButtons.getMaximumSize().getWidth()).intValue(), new
                Double(hboxButtons.getMinimumSize().getHeight()).intValue() * 2));
        getContentPane().add(hboxButtons, BorderLayout.SOUTH);
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        getContentPane().add(hBoxResult, BorderLayout.CENTER);
    }

    protected void saveToGraphicsFile(File selectedFile) {
        try {
            selectedFile = new File(addExtension(selectedFile.getName(), "bin"));
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            for (int i = 0; i < data.getRowCount(); i++) {
                out.writeDouble((Double) data.getValueAt(i, 0));
                out.writeDouble((Double) data.getValueAt(i, 1));
            }
            out.close();
        } catch (Exception e) {
            // Исключительную ситуацию "ФайлНеНайден" в данном случае можно не обрабатывать,
            // так как мы файл создаѐм, а не открываем для чтения
        }
    }

    protected void saveToTextFile(File selectedFile) {
        try {
            selectedFile = new File(addExtension(selectedFile.getName(), "txt"));
            PrintStream out = new PrintStream(selectedFile);
            out.println("Результаты табулирования многочлена по схеме Горнера ");
            out.print("Многочлен: ");

            for (int i = 0; i < coefficients.length - 1; i++) {
                out.print(coefficients[i] + "*X^" + (coefficients.length - i - 1) + " + " );
            }
            out.print(coefficients[coefficients.length - 1]);

            out.println("");
            out.println("Интервал от " + data.getFrom() + " до " +
                    data.getTo() + " с шагом " + data.getStep());

            out.println("====================================================");

            int rowCount = data.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                out.println("Значение в точке " + data.getValueAt(i, 0)
                        + " равно " + formatter.format(data.getValueAt(i, 1)));
            }
            out.close();
        } catch (FileNotFoundException e) {
            // Исключительную ситуацию "ФайлНеНайден" можно не
            // обрабатывать, так как мы файл создаѐм, а не открываем
        }
    }

    protected void saveToCSVFile(File file, String delimiter){
        try{
            file = new File(addExtension(file.getName(), "csv"));
            PrintStream out = new PrintStream(file);
            int rowCount = data.getRowCount();
            int colCountMinus1 = data.getColumnCount() - 1;
            for (int i = 0; i < rowCount; i++) {
                for(int j = 0; j < colCountMinus1; j++){
                    out.print(formatter.format(data.getValueAt(i, j)) + delimiter);
                }
                out.println(formatter.format(data.getValueAt(i, colCountMinus1)));
            }
            out.close();

        }
        catch (FileNotFoundException e){

        }
    }

    public static void main(String[] args) {
        // Если не задано ни одного аргумента командной строки -
        // Продолжать вычисления невозможно, коэффиценты неизвестны
        if (args.length == 0) {
            System.out.println("Невозможно табулировать многочлен, для которого не задано ни одного коэффициента !");
            System.exit(-1);
        }
        Double[] coefficients = new Double[args.length];
        int i = 0;
        try {
            for (String arg : args) {
                coefficients[i++] = Double.parseDouble(arg);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Ошибка преобразования строки '" +
                    args[i] + "' в число типа Double");
            System.exit(-2);
        }
        MainFrame frame = new MainFrame(coefficients);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}