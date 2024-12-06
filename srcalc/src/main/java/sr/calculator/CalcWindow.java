package sr.calculator;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

/**
 * @author StephenKing638 (https://github.com/StephenKing638)
 * @since v2.0 10-19-24
 */
public class CalcWindow extends JFrame {

    private static String FILE_PATH = "settings.dat";

    private static String TITLE = "SRCalculator";

    private static String FOOTER = "srcalculator v2.0 10-19-24";

    private static String README_PATH = "README.md";

    @SuppressWarnings("unchecked")
    private static Class<LookAndFeel>[] looks = new Class[] {
        FlatArcOrangeIJTheme.class,
        FlatArcDarkIJTheme.class,
        FlatDarkFlatIJTheme.class,
        FlatArcDarkOrangeIJTheme.class,
        FlatAtomOneDarkIJTheme.class,
        FlatAtomOneLightIJTheme.class,
        FlatArcIJTheme.class,
        FlatCyanLightIJTheme.class,
        FlatMonokaiProIJTheme.class,
        FlatMoonlightIJTheme.class,
        FlatGruvboxDarkHardIJTheme.class,
        FlatHiberbeeDarkIJTheme.class,
        FlatHighContrastIJTheme.class,
        FlatLightOwlIJTheme.class,
        FlatMaterialPalenightIJTheme.class,
        FlatMaterialDeepOceanIJTheme.class,
        FlatSolarizedDarkIJTheme.class,
        FlatXcodeDarkIJTheme.class
    };
    static int lookIndex = 0;

    int buttonSpace = 4;
    int compSpace = 4;
    int cornerSafezone = 16;

    JTextArea outputArea;
    JTextField inputField;
    JPanel buttonPanel;

    int width = 300;

    double prevAns = 0;

    public CalcWindow() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        setLayout(new BorderLayout());
        requestFocus();

        // keyboard presses
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyPresses);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JButton infoButton = new JButton("ⓘ");
        infoButton.setFocusable(false);
        infoButton.setHorizontalAlignment(SwingConstants.CENTER);
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] options = { "Ok", "Cancel" };
                int response = JOptionPane.showOptionDialog(
                        CalcWindow.this,
                        "This will redirect you to the README",
                        "Redirect Alert!",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);

                if (response == 0) {
                    try {
                        File file = new File(README_PATH);
                        if (!file.exists()) throw new IOException();
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);
                    } catch (UnsupportedOperationException e1) {
                        JOptionPane.showMessageDialog(CalcWindow.this, "Could not access desktop environment", "warning", JOptionPane.WARNING_MESSAGE);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(CalcWindow.this, "Could not find specified file", "warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        menuBar.add(Box.createHorizontalGlue()); // push button to the far right
        menuBar.add(infoButton);
        setJMenuBar(menuBar);

        // PANE
        JPanel pane = new JPanel(new BorderLayout(compSpace, compSpace));
        pane.setOpaque(true);
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(cornerSafezone, cornerSafezone, cornerSafezone, cornerSafezone));
        b.setTitle(FOOTER);
        b.setTitlePosition(TitledBorder.BOTTOM);

        pane.setBorder(b);
        add(pane);

        // top half
        outputArea = new JTextArea();
        outputArea.setEditable(false); // The user should not edit the output
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(width, 200)); // Adjust height as half

        // middle
        inputField = UndoRedoManager.addTo(new JTextField());
        inputField.setPreferredSize(new Dimension(width, 30)); // Adjust size for input
        inputField.addKeyListener(keyEvent);
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // constantly regain focus
                inputField.requestFocusInWindow();
            }
        });

        // bottom
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(width, 160));
        buttonPanel.setLayout(new GridLayout(5, 5, buttonSpace, buttonSpace));

        newB("sin").newB("(").newB(")").newB("^").newB("/");
        newB("cos").newB("7").newB("8").newB("9").cusB("x");
        newB("tan").newB("4").newB("5").newB("6").newB("+");
        newB("π").newB("1").newB("2").newB("3").newB("-");
        newB("ans").cusB("del").newB("0").newB(".").cusB("=");

        pane.add(scrollPane, BorderLayout.NORTH);
        pane.add(inputField, BorderLayout.CENTER);
        pane.add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitHook();
            }
        });

        // LOAD SETTINGS
        loadSettings();
        setLookAndFeel(lookIndex);
        outputArea.setCaretPosition(outputArea.getDocument().getLength()); // focus to bottom

        // FINALIZATION
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        inputField.requestFocusInWindow();
    }


    private final KeyEventDispatcher keyPresses = new KeyEventDispatcher() {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if(e.getID() == KeyEvent.KEY_PRESSED) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_ENTER: {
                        commitTextFieldLine();
                        return true;
                    }
                    case KeyEvent.VK_ESCAPE: {
                        dispose();
                        exitHook();
                        return true;
                    }
                    case KeyEvent.VK_F3: {
                        SwingUtilities.invokeLater(() -> {
                            setLookAndFeel(++lookIndex);
                        });
                        return true;
                    }
                    case KeyEvent.VK_F4: {
                        SwingUtilities.invokeLater(() -> {
                            inputField.setText("");
                            outputArea.setText("");
                        });
                        return true;
                    }
                    default : break;
                }
            }
            return false;
        }
    };

    private final KeyListener keyEvent = new KeyAdapter() {
        boolean isHoldingCtrl = false;
            
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == '(') {
                    insertText("()", 1);
                    e.consume();
                } else if (c == '=') {
                    commitTextFieldLine();
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isHoldingCtrl = true;
                    e.consume();
                } else
                if(isHoldingCtrl) {
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        inputField.setCaretPosition(inputField.getText().length());
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        inputField.setCaretPosition(0);
                        e.consume();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isHoldingCtrl = false;
                    e.consume();
                }
            }
    };

    private synchronized void commitTextFieldLine() {
        String text = inputField.getText().trim();
        if (text.isBlank()) return;
        inputField.setText("");
        outputArea.append(text);
        outputArea.append("\n");
        // DO MATH
        try {
            boolean requestFraction = text.contains("frac(");

            // CALCULATOR
            ExpressionBuilder builder = new ExpressionBuilder(text);
            builder.variable("ans");
            builder.function(new Function("frac", 1) {
                @Override
                public double apply(double... args) {
                    if(args.length == 0) throw new RuntimeException("invalid args");
                    return args[0];
                }
            });

            Expression exp = builder.build();
            exp.setVariable("ans", prevAns);

            prevAns = exp.evaluate();

            outputArea.append(String.format("    %s\n", formatDouble(prevAns, requestFraction)));
            outputArea.setCaretPosition(outputArea.getDocument().getLength()); // focus to bottom
        } catch(ArithmeticException e) {
            outputArea.append("    NaN!");
        } catch(RuntimeException e) {
            outputArea.append("    SYNTAX ERROR!\n");
            e.printStackTrace();
        }
    }

    private void insertText(String text) {
        insertText(text, text.length());
    }

    private void insertText(String text, int moveCaratAmount) {
        String str = inputField.getText();
        int caretPosition = inputField.getCaretPosition();
        inputField.setText(str.substring(0, caretPosition) + text + str.substring(caretPosition));
        inputField.setCaretPosition(caretPosition + moveCaratAmount);
    }

    private void backspace(int amount) {
        String str = inputField.getText();
        if(str.length() < amount) return;
        int caretPosition = inputField.getCaretPosition();
        inputField.setText(str.substring(0, caretPosition - amount) + str.substring(caretPosition));
        inputField.setCaretPosition(caretPosition -= amount);
    }

    private static final DecimalFormat scientificFormat = new DecimalFormat("0.######E0");
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.######");

    private static String formatDouble(double number, boolean requestFraction) {
        if(Double.isInfinite(number) || Double.isNaN(number)) {
            return "NaN";
        }
        if(Math.abs(number) >= 1.0E6) {
            return scientificFormat.format(number);
        } else
        if(number == 0) {
            return "0";
        } else
        if (number % 1 == 0) {
            // If it's a whole number, return without decimal
            return String.valueOf((int) number);
        } else {
            // If it has decimals, format to 6 decimal places
            if(requestFraction) {
                return FracUtil.fractionToString(FracUtil.toFraction(number));
            }
            return decimalFormat.format(number);
        }
    }

    private CalcWindow newB(String displayText) {
        JButton j = new JButton(displayText);
        j.addActionListener((e) -> {
            insertText(displayText);
        });
        buttonPanel.add(j);
        return this;
    }

    private CalcWindow cusB(String displayText) {
        ActionListener customEvent = null;
        switch(displayText) {
            case "=" : {
                customEvent = (e) -> commitTextFieldLine();
                break;
            }
            case "del" : {
                customEvent = (e) -> {
                    backspace(1);
                };
                break;
            }
            case "x" : {
                customEvent = (e) -> {
                    insertText("*");
                };
                break;
            }
            default: throw new IllegalArgumentException("unknown displayText code: " + displayText);
        }

        JButton j = new JButton(displayText);
        j.addActionListener(customEvent);
        buttonPanel.add(j);
        return this;

    }

    private Object exitLock = new Object();
    private volatile boolean exitLocked = false;

    private void exitHook() {
        synchronized(exitLock) {
            if(exitLocked) return;
            exitLocked = true;
            saveSettings();
        }
    }

    private synchronized void saveSettings() {
        try {
            Map<String, Object> map = new HashMap<>(1, 1f);
            map.put("lookIndex", lookIndex);
            map.put("output", outputArea.getText());
            map.put("input", inputField.getText());

            try(FileOutputStream os = new FileOutputStream(FILE_PATH)) {
                Compressor.sendCompressedObject(map, os);
            }

            System.out.println("settings saved");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void loadSettings() {
        try {
            File file = new File(FILE_PATH);
            if(!file.exists()) {
                System.out.println("settings file does not exist");
                return;
            }
            try(FileInputStream fis = new FileInputStream(FILE_PATH)) {
                Map<String, Object> map = Compressor.receiveCompressedObject(fis);

                lookIndex = (Integer) map.getOrDefault("lookIndex", 0);
                outputArea.setText((String) map.getOrDefault("output", ""));
                inputField.setText((String) map.getOrDefault("input", ""));
            }

            System.out.println("settings loaded");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void setLookAndFeel(int index) {
        try {
            Constructor<LookAndFeel> c = looks[index%looks.length].getDeclaredConstructor();
            UIManager.setLookAndFeel(c.newInstance());
            SwingUtilities.updateComponentTreeUI(getRootPane());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CalcWindow();
        });
    }
}

