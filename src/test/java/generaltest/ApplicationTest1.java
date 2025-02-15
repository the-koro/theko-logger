package generaltest;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

import org.theko.logger.DefaultLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.LoggerConfig;

/**
 * The main class of the application, representing a simple text editor.
 */
public class ApplicationTest1 extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;
    private boolean isModified = false;
    private UndoManager undoManager = new UndoManager();
    private DefaultLogger logger;
    private float currentFontSize = 14;

    private static final float MAX_FONT_SIZE = 72;
    private static final float MIN_FONT_SIZE = 7;

    /**
     * Constructor for the class. Initializes the main components of the application.
     */
    public ApplicationTest1() {
        loadLogger();
        logger.debug("Initializing application...", "APP", "INIT");

        setTitle("Simple Notepad - Untitled");
        setSize(800, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        logger.debug("Frame launched with default size 800x600.", "FRAME", "INIT");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            logger.debug("System Look and Feel applied successfully.", "FRAME", "UI", "INIT");
        } catch (Exception e) {
            logger.warn("Failed to set Look and Feel: " + e.getMessage(), "FRAME", "UI", "ERROR");
        }

        undoManager.setLimit(1000);
        logger.debug("UndoManager initialized with a limit of 1000 edits.", "UNDO", "INIT");

        logger.debug("Initializing text area...", "TEXT", "INIT");
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, (int) currentFontSize));
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { markModified(); }
            public void removeUpdate(DocumentEvent e) { markModified(); }
            public void changedUpdate(DocumentEvent e) {}
        });
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        logger.debug("Text area initialized with Monospaced font and document listeners.", "TEXT", "INIT");

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);
        logger.debug("Scroll pane added to the frame.", "FRAME", "UI", "INIT");

        fileChooser = new JFileChooser();
        logger.debug("File chooser initialized.", "FILE", "INIT");

        logger.debug("Initializing menu bar...", "MENUBAR", "INIT");
        createMenuBar();
        logger.debug("Menu bar initialized and added to the frame.", "MENUBAR", "INIT");

        JFrame jframe = this;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmSave()) {
                    logger.info("Application is closing.", "APP", "SHUTDOWN");
                    new WindowFadeOut(jframe);
                    logger.debug("Frame disposed.", "FRAME", "SHUTDOWN");
                    //System.exit(0);
                } else {
                    logger.debug("Window closing operation canceled by user.", "FRAME", "SHUTDOWN");
                }
            }
        });
        logger.debug("Window listener added to handle closing events.", "FRAME", "INIT");

        setVisible(true);
        logger.debug("Frame set to visible.", "FRAME", "UI");
        logger.info("Application started successfully.", "APP", "STARTUP");
    }

    /**
     * Loads the logger configuration from a file.
     */
    private void loadLogger() {
        try {
            URL resourceUrl = this.getClass().getResource("config1.json");
            if (resourceUrl == null) {
                throw new IllegalStateException("Config file not found: config1.json");
            }

            String configPath = resourceUrl.toExternalForm().substring(6);
            System.out.println("Loading logger configuration from: " + configPath);

            LoggerConfig config = new LoggerConfig(configPath);
            config.load();
            System.out.println("Logger configuration loaded successfully.");

            logger = (DefaultLogger) config.getLogger();
            if (logger == null) {
                throw new IllegalStateException("Logger instance is null.");
            }

            logger.setLoggerOutput(Objects.requireNonNull(config.getLoggerOutput(), "Logger output is null."));
            logger.debug("Logger initialized and ready to use.", "LOGGER", "INIT");
        } catch (Exception e) {
            System.err.println("Error initializing logger: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates the menu bar and adds all necessary menus to it.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = createFileMenu();
        
        // Edit Menu
        JMenu editMenu = createEditMenu();
        
        // View Menu
        JMenu viewMenu = createViewMenu();
        
        // Help Menu
        JMenu helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        logger.debug("Menu bar created with all menus.", "MENUBAR", "INIT");
    }

    /**
     * Creates the "File" menu.
     * @return The "File" menu.
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile(e));
        
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile(false));
        
        JMenuItem saveAsItem = new JMenuItem("Save As");
        saveAsItem.addActionListener(e -> saveFile(true));
        
        JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(e -> {
                if (confirmSave()) {
                logger.info("Application is closing.", "APP", "SHUTDOWN");
                new WindowFadeOut(this);
                logger.debug("Frame disposed.", "FRAME", "SHUTDOWN");
                //System.exit(0);
            } else {
                logger.debug("Window closing operation canceled by user.", "FRAME", "SHUTDOWN");
            }
        });
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        return fileMenu;
    }

    /**
     * Creates the "Edit" menu.
     * @return The "Edit" menu.
     */
    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(e -> textArea.cut());
        
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(e -> textArea.copy());JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        });
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        
        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(e -> {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        });
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        
        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(e -> textArea.paste());
        
        JMenuItem findItem = new JMenuItem("Find");
        findItem.addActionListener(e -> showSearchDialog(false));
        
        JMenuItem replaceItem = new JMenuItem("Replace");
        replaceItem.addActionListener(e -> showSearchDialog(true));
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(findItem);
        editMenu.add(replaceItem);
        
        return editMenu;
    }

    /**
     * Creates the "View" menu.
     * @return The "View" menu.
     */
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        
        JMenuItem zoomIn = new JMenuItem("Zoom In (+)");
        zoomIn.addActionListener(e -> changeFontSize(2));
        
        JMenuItem zoomOut = new JMenuItem("Zoom Out (-)");
        zoomOut.addActionListener(e -> changeFontSize(-2));
        
        JMenuItem setSize = new JMenuItem("Set Font Size...");
        setSize.addActionListener(e -> setCustomFontSize());
        
        viewMenu.add(zoomIn);
        viewMenu.add(zoomOut);
        viewMenu.addSeparator();
        viewMenu.add(setSize);
        
        // Mouse wheel listener for Ctrl+Scroll
        textArea.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                int rotation = e.getWheelRotation();
                if (changeFontSize(rotation > 0 ? -2 : 2)) {
                    logger.debug("Font size changed via mouse wheel.", "VIEW", "ZOOM");
                }
            }
        });
        
        return viewMenu;
    }

    /**
     * Creates the "Help" menu.
     * @return The "Help" menu.
     */
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        return helpMenu;
    }

    /**
     * Displays the "About" dialog.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, "Simple Notepad\nusing theko-logger", "About Simple Notepad", JOptionPane.INFORMATION_MESSAGE);
        logger.debug("About dialog shown.", "HELP", "ABOUT");
    }

    /**
     * Displays the search/replace dialog.
     * @param replaceMode If true, the dialog will be in replace mode.
     */
    private void showSearchDialog(boolean replaceMode) {
        JDialog dialog = new JDialog(this, replaceMode ? "Replace" : "Find", true);
        dialog.setLayout(new BorderLayout());
        
        JTextField searchField = new JTextField(20);
        JTextField replaceField = replaceMode ? new JTextField(20) : null;
        
        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> {
            String searchText = searchField.getText();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Search field is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            findText(searchText);
        });
        
        JButton replaceButton = null;
        if (replaceMode) {
            replaceButton = new JButton("Replace");
            replaceButton.addActionListener(e -> {
                String searchText = searchField.getText();
                String replaceText = replaceField.getText();
                if (searchText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Search field is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                replaceText(searchText, replaceText);
            });
        }
        
        JPanel panel = new JPanel();
        panel.add(new JLabel("Find:"));
        panel.add(searchField);
        if (replaceMode) {
            panel.add(new JLabel("Replace:"));
            panel.add(replaceField);
        }
        panel.add(findButton);
        if (replaceMode) panel.add(replaceButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Searches for text in the text area.
     * @param text The text to search for.
     */
    private void findText(String text) {
        String content = textArea.getText();
        Pattern pattern = Pattern.compile(text);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            textArea.setCaretPosition(matcher.start());
            textArea.select(matcher.start(), matcher.end());
            logger.debug("Text found: " + text, "EDIT", "SEARCH");
        } else {
            JOptionPane.showMessageDialog(this, "Text not found!", "Search", JOptionPane.INFORMATION_MESSAGE);
            logger.debug("Text not found: " + text, "EDIT", "SEARCH");
        }
    }

    /**
     * Replaces text in the text area.
     * @param searchText The text to search for.
     * @param replaceText The text to replace with.
     */
    private void replaceText(String searchText, String replaceText) {
        String content = textArea.getText();
        String newContent = content.replace(searchText, replaceText);
        textArea.setText(newContent);
        logger.debug("Text replaced: " + searchText + " -> " + replaceText, "EDIT", "REPLACE");
    }

    /**
     * Changes the font size by the specified delta.
     * @param delta The change in font size.
     * @return true if the font size was changed, false otherwise.
     */
    private boolean changeFontSize(int delta) {
        currentFontSize += delta;
        if (currentFontSize < MIN_FONT_SIZE) {
            currentFontSize = MIN_FONT_SIZE;
            return false;
        }
        if (currentFontSize > MAX_FONT_SIZE)  {
            currentFontSize = MAX_FONT_SIZE;
            return false;
        }
        setFontSize(currentFontSize);
        return true;
    }

    /**
     * Sets the font size to the specified value.
     * @param size The new font size.
     */
    private void setFontSize(float size) {
        Font currentFont = textArea.getFont();
        textArea.setFont(currentFont.deriveFont(size));
        logger.info("Font size changed to: " + size, "VIEW", "ZOOM");
    }

    /**
     * Prompts the user to set a custom font size.
     */
    private void setCustomFontSize() {
        String size = JOptionPane.showInputDialog("Enter font size:");
        try {
            int newSize = Integer.parseInt(size);
            setFontSize(newSize);
        } catch (NumberFormatException ex) {
            logger.error("Invalid font size: " + ex.getMessage(), "VIEW", "ERROR");
        }
    }

    /**
     * Marks the file as modified and updates the title.
     */
    private void markModified() {
        if (!isModified) {
            isModified = true;
            updateTitle();
            logger.debug("File marked as modified.", "FILE");
        }
    }

    /**
     * Updates the title of the window to reflect the current file and modification status.
     */
    private void updateTitle() {
        setTitle("Simple Notepad - " + (currentFile != null ? currentFile.getName() : "Untitled") + (isModified ? " *" : ""));
        logger.debug("Title updated.", "FRAME");
    }

    /**
     * Prompts the user to save changes if the file is modified.
     * @return true if the user chooses to save or discard changes, false if the operation is canceled.
     */
    private boolean confirmSave() {
        if (!isModified) return true;
        logger.debug("Save confirmation...", "FILE");
        int option = JOptionPane.showConfirmDialog(this, "Save changes?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION) return false;
        if (option == JOptionPane.YES_OPTION) saveFile(false);
        return true;
    }

    /**
     * Opens a file for editing.
     * @param e The action event.
     */
    private void openFile(ActionEvent e) {
        if (!confirmSave()) return;
        
        logger.debug("Opening file...", "FILE");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            logger.debug("Opened file: " + currentFile, "FILE");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(currentFile), StandardCharsets.UTF_8))) {
                textArea.read(reader, null);
                undoManager.discardAllEdits();
                isModified = false;
                updateTitle();
                logger.info("Opened file: " + currentFile.getAbsolutePath(), "FILE");
            } catch (IOException ex) {
                logger.error("Error opening file: " + ex.getMessage(), "FILE");
            }
        } else {
            logger.debug("Opening canceled.", "FILE");
        }
    }
    
    /**
     * Saves the current file.
     * @param saveAs If true, prompts the user to specify a new file name.
     */
    private void saveFile(boolean saveAs) {
        logger.debug("Saving file...", "FILE");
        if (saveAs || currentFile == null) {
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                logger.debug("Saving canceled.", "FILE");
                return;
            }
            currentFile = fileChooser.getSelectedFile();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFile), StandardCharsets.UTF_8))) {
            textArea.write(writer);
            isModified = false;
            updateTitle();
            logger.info("Saved file: " + currentFile.getAbsolutePath(), "FILE");
        } catch (IOException ex) {
            logger.error("Error saving file: " + ex.getMessage(), "FILE");
        }
    }

    /**
     * The main method to launch the application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ApplicationTest1::new);
    }

    public class WindowFadeOut {
        private final JWindow effectWindow;
        private final BufferedImage image;
        private float alpha = 1.0f;
        
        public WindowFadeOut(JFrame frame) {
            Rectangle bounds = frame.getBounds();
            image = captureFrameWithBorders(frame);

            effectWindow = new JWindow();
            effectWindow.setBounds(bounds);
            effectWindow.setAlwaysOnTop(true);
            effectWindow.setFocusableWindowState(false);
            effectWindow.setBackground(new Color(0, 0, 0, 0)); // Прозрачный фон
            logger.debug("Effect window created.", "ANIM", "EXIT");

            JPanel fadePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (image != null) {
                        Graphics2D g2d = (Graphics2D) g;
                        float ease = (float)Math.pow(alpha, 3.0f);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ease));
                        
                        int imgHeight = image.getHeight();
                        int newHeight = (int) (imgHeight * ease);
                        int y = (imgHeight - newHeight) / 2;
                        
                        AffineTransform transform = new AffineTransform();
                        transform.translate(0, y);
                        transform.scale(1.0, ease);
                        g2d.drawImage(image, transform, null);
                    }
                }
            };
            fadePanel.setOpaque(false);
            fadePanel.setPreferredSize(new Dimension(bounds.width, bounds.height));
            
            effectWindow.setContentPane(fadePanel);
            effectWindow.setAlwaysOnTop(true);
            effectWindow.pack();
            effectWindow.setVisible(true);

            frame.setVisible(false);
            logger.debug("Frame hidden.", "ANIM", "EXIT", "FRAME");
            
            Timer timer = new Timer(16, e -> {
                alpha -= 0.02f;
                if (alpha <= 0) {
                    ((Timer) e.getSource()).stop();
                    effectWindow.dispose();
                    frame.dispose();
                    logger.debug("Animation completed.", "ANIM", "EXIT");
                } else {
                    effectWindow.repaint();
                }
            });
            timer.start();
        }

        private BufferedImage captureFrameWithBorders(JFrame frame) {
            try {
                logger.debug("Creating frame capture.", "FRAME", "CAPTURE");
                Robot robot = new Robot();
                return robot.createScreenCapture(frame.getBounds());
            } catch (AWTException e) {
                logger.log(LogLevel.ERROR, e.getMessage(), e, "FRAME", "CAPTURE");
                return null;
            }
        }
    }
}