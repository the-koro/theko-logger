package test.theko.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.theko.logger.GlobalLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.LoggerOutput;

public class Test5 {
    private JFrame frame;
    private BufferedImage canvas;
    private Graphics2D g2d;
    private int brushWidth = 5;
    private Color currentColor = Color.BLACK;
    private Point prevMousePos = null; // To store the previous mouse position

    public Test5() {
        initLogger();
        initUI();
    }

    private void initLogger() {
        try {
            InputStream is = Test5.class.getClassLoader().getResourceAsStream("outputConfig.json");
            GlobalLogger.setLoggerOutput(LoggerOutput.loadFrom(
                new JSONObject(new JSONTokener(is))
            ));
            GlobalLogger.log(LogLevel.INFO, "Logger initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    private void initUI() {
        GlobalLogger.log(LogLevel.INFO, "Initializing UI...");
        frame = new JFrame("Paint");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        JPanel canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
            }
        };
        canvasPanel.setPreferredSize(new Dimension(800, 600));
        frame.add(canvasPanel, BorderLayout.CENTER);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new FlowLayout());

        // Brush width slider
        JSlider slider = new JSlider(1, 50, brushWidth);
        slider.addChangeListener(e -> {
            brushWidth = slider.getValue();
            GlobalLogger.log(LogLevel.INFO, "Brush width changed to " + brushWidth);
        });
        toolsPanel.add(new JLabel("Brush Width:"));
        toolsPanel.add(slider);

        // Color buttons
        Color[] colors = {
            Color.BLACK, Color.WHITE, Color.RED, Color.ORANGE, Color.YELLOW,
            Color.GREEN, Color.CYAN, Color.BLUE, new Color(135, 206, 250), // Sky
            Color.MAGENTA, Color.PINK
        };
        for (Color color : colors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.addActionListener(e -> {
                currentColor = color;
                GlobalLogger.log(LogLevel.INFO, "Color changed to " + color.toString());
            });
            toolsPanel.add(colorButton);
        }

        frame.add(toolsPanel, BorderLayout.NORTH);

        // Mouse listener for line drawing
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevMousePos = e.getPoint();
                GlobalLogger.log(LogLevel.DEBUG, "Mouse pressed at " + prevMousePos);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                prevMousePos = null;
                GlobalLogger.log(LogLevel.DEBUG, "Mouse released");
            }
        });

        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (prevMousePos != null) {
                    Point currentMousePos = e.getPoint();

                    // Draw a line from the previous mouse position to the current position
                    g2d.setColor(currentColor);
                    g2d.setStroke(new java.awt.BasicStroke(brushWidth));
                    g2d.drawLine(prevMousePos.x, prevMousePos.y, currentMousePos.x, currentMousePos.y);

                    canvasPanel.repaint();
                    GlobalLogger.log(LogLevel.DEBUG, "Line drawn from " + prevMousePos + " to " + currentMousePos);

                    // Update the previous mouse position
                    prevMousePos = currentMousePos;
                }
            }
        });

        frame.setVisible(true);
        GlobalLogger.log(LogLevel.INFO, "UI initialized successfully.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Test5();
                GlobalLogger.log(LogLevel.INFO, "Paint application started.");
            } catch (Exception e) {
                GlobalLogger.log(LogLevel.ERROR, "Error initializing paint application.", e);
            }
        });
    }
}
