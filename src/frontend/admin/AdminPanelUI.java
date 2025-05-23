package frontend.admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import frontend.landingpage.LandingPageUI;
import frontend.utils.WindowTransitionManager;

/**
 * Admin Panel UI for the Gabay application
 * Provides administrative control for managing the application
 */
public class AdminPanelUI extends JFrame {
    // Font variables
    private Font interRegular;
    private Font interBlack;
    private Font interSemiBold;
    private Font interBold;
    private Font interMedium;
    
    // Colors matching the main UI
    private Color primaryBlue = new Color(0x2F, 0x39, 0x8E); // #2f398e
    private Color headingColor = new Color(0x47, 0x55, 0x69); // #475569
    private Color paragraphColor = new Color(0x8D, 0x8D, 0x8D); // #8D8D8D
    private Color textDark = new Color(0x47, 0x55, 0x69); // #475569 - for consistent text
    
    // Background image
    private BufferedImage backgroundImage;
    private boolean showBackgroundImage = true;
    private final int BACKDROP_WIDTH = 2555;
    private final int BACKDROP_HEIGHT = 2154;
    private final int BACKDROP_X = 206;
    private final int BACKDROP_Y = -242;
    
    // Header logo
    private BufferedImage headerLogoImage;
    
    // Text positioning constants
    private final int TITLE_X = 140; // Base X position in the reference window size
    private final int TITLE_Y = 181; // Adjusted Y position to be closer to rectangles
    private final int PARAGRAPH_WIDTH = 1154; // Base width in reference window size
    private final int ELEMENT_SPACING = -5; // Negative spacing to create overlap
    private final Color TITLE_COLOR = new Color(0x2B, 0x37, 0x80); // #2B3780
    
    // Window dimensions
    private int initialWindowWidth = 1411; // Fixed window width
    private int initialWindowHeight = 970; // Fixed window height
    
    // Add member variables for the panels
    private CandidateDirectoryPanel directoryPanel;
    private CandidateDetailsPanel detailsPanel;
    
    public AdminPanelUI() {
        // Load fonts
        loadFonts();
        
        // Load background image
        loadBackgroundImage();
        
        // Load header logo
        loadHeaderLogoImage();
        
        // Set up the window
        setTitle("Gabáy - Admin Panel");
        setSize(initialWindowWidth, initialWindowHeight);
        setResizable(false); // Make window non-resizable
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set default font for all UI elements
        setUIFont(interRegular);
        
        // Create main panel with custom background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fill the background with white
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw background image with reduced opacity if enabled
                if (backgroundImage != null && showBackgroundImage) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    // Calculate backdrop position, adjusting for window scaling
                    double widthScaleFactor = Math.min(1.0, getWidth() / (double)initialWindowWidth);
                    double heightScaleFactor = Math.min(1.0, getHeight() / (double)initialWindowHeight);
                    double scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
                    
                    // Calculate position using the specific coordinates and scaling
                    int imageX = (int)(BACKDROP_X * widthScaleFactor);
                    int imageY = (int)(BACKDROP_Y * heightScaleFactor);
                    
                    // Calculate scaled dimensions
                    int scaledWidth = (int)(BACKDROP_WIDTH * scaleFactor);
                    int scaledHeight = (int)(BACKDROP_HEIGHT * scaleFactor);
                    
                    // Set the opacity to 3%
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.03f);
                    g2d.setComposite(alphaComposite);
                    
                    g2d.drawImage(backgroundImage, imageX, imageY, scaledWidth, scaledHeight, this);
                    
                    // Reset composite for other components
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }
                
                // Draw title text at specified position
                Graphics2D g2d = (Graphics2D)g;
                drawTitleAndParagraph(g2d);
            }
            
            /**
             * Draw the title and paragraph at specified position
             */
            private void drawTitleAndParagraph(Graphics2D g2d) {
                // Calculate scaling factors based on window size
                double widthScaleFactor = Math.min(1.0, getWidth() / (double)initialWindowWidth);
                double heightScaleFactor = Math.min(1.0, getHeight() / (double)initialWindowHeight);
                
                // Calculate content width
                int availableWidth = getWidth();
                int scaledParagraphWidth = (int)(PARAGRAPH_WIDTH * widthScaleFactor);
                
                // Ensure paragraph width is not too wide for the window
                scaledParagraphWidth = Math.min(scaledParagraphWidth, availableWidth - 2 * (int)(140 * widthScaleFactor));
                
                // Calculate X position for centered content
                int scaledTitleX = (availableWidth - scaledParagraphWidth) / 2;
                
                int scaledTitleY = (int)(TITLE_Y * heightScaleFactor);
                int scaledSpacing = (int)(ELEMENT_SPACING * heightScaleFactor);
                
                // Draw title text
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Apply Inter Black font for title
                Font titleFont = interBlack;
                if (titleFont != null) {
                    // Use 70pt size for title
                    float titleFontSize = 70f * (float)widthScaleFactor;
                    titleFontSize = Math.max(40f, titleFontSize); // Minimum size
                    
                    Map<TextAttribute, Object> attributes = new HashMap<>();
                    attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                    titleFont = titleFont.deriveFont(titleFontSize).deriveFont(attributes);
                } else {
                    titleFont = new Font("Sans-Serif", Font.BOLD, (int)(70 * widthScaleFactor));
                }
                
                g2d.setFont(titleFont);
                g2d.setColor(TITLE_COLOR);
                
                // Draw the title at calculated position
                g2d.drawString("Admin Panel.", scaledTitleX, scaledTitleY);
                
                // Draw paragraph text below the title
                Font paragraphFont = interSemiBold;
                if (paragraphFont != null) {
                    // Use exact 18pt size for paragraph as requested, with scaling
                    float paragraphFontSize = 18f * (float)widthScaleFactor;
                    paragraphFontSize = Math.max(14f, paragraphFontSize); // Minimum size
                    
                    Map<TextAttribute, Object> attributes = new HashMap<>();
                    attributes.put(TextAttribute.TRACKING, -0.05); // -5% letter spacing
                    paragraphFont = paragraphFont.deriveFont(paragraphFontSize).deriveFont(attributes);
                } else {
                    paragraphFont = new Font("Sans-Serif", Font.PLAIN, (int)(18 * widthScaleFactor));
                }
                
                g2d.setFont(paragraphFont);
                g2d.setColor(paragraphColor);
                
                // Example paragraph text - replace with your desired content
                String paragraphText = "Administrator control panel for managing candidate data, " +
                    "user access, and application settings. This panel provides tools for content " +
                    "management and system administration.";
                
                // Calculate paragraph position below title - make them super close
                FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
                // Use negative spacing to push paragraph up into title space
                int paragraphY = scaledTitleY + (titleMetrics.getHeight() / 2) + scaledSpacing;
                
                // Draw wrapped paragraph text
                drawWrappedText(g2d, paragraphText, scaledTitleX, paragraphY, scaledParagraphWidth);
            }
            
            /**
             * Draw wrapped text with specified width, left-aligned
             */
            private void drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth) {
                if (text == null || text.isEmpty()) {
                    return;
                }
                
                FontMetrics fm = g2d.getFontMetrics();
                int lineHeight = fm.getHeight();
                
                String[] words = text.split("\\s+");
                StringBuilder currentLine = new StringBuilder();
                int currentY = y;
                
                for (String word : words) {
                    if (currentLine.length() > 0) {
                        String testLine = currentLine + " " + word;
                        if (fm.stringWidth(testLine) <= maxWidth) {
                            currentLine.append(" ").append(word);
                        } else {
                            // Draw current line
                            g2d.drawString(currentLine.toString(), x, currentY);
                            currentY += lineHeight;
                            currentLine = new StringBuilder(word);
                        }
                    } else {
                        currentLine.append(word);
                    }
                }
                
                // Draw the last line
                if (currentLine.length() > 0) {
                    g2d.drawString(currentLine.toString(), x, currentY);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Create logo panel
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        
        // Create logo label with the header logo image
        JLabel logoLabel = new JLabel();
        if (headerLogoImage != null) {
            // Scale logo appropriately
            int logoHeight = 40; // Height in pixels
            int logoWidth = (int)((double)headerLogoImage.getWidth() / headerLogoImage.getHeight() * logoHeight);
            
            // Create scaled version of logo
            Image scaledLogo = headerLogoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
            
            // Make logo clickable to exit the application
            logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Redirect to LandingPageUI with fade transition
                    System.out.println("Redirecting to Landing Page...");
                    WindowTransitionManager.fadeTransition(AdminPanelUI.this, () -> new LandingPageUI());
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    // Optional: Add hover effect
                    logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    logoLabel.setBorder(null);
                }
            });
        } else {
            // Fallback if image couldn't be loaded
            logoLabel.setPreferredSize(new Dimension(120, 40));
            logoLabel.setBackground(new Color(0, 0, 0, 0)); // Transparent
            
            // Still make it clickable
            logoLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Redirect to LandingPageUI with fade transition
                    System.out.println("Redirecting to Landing Page...");
                    WindowTransitionManager.fadeTransition(AdminPanelUI.this, () -> new LandingPageUI());
                }
            });
        }
        
        logoPanel.add(logoLabel);
        
        // Add components to panels
        headerPanel.add(logoPanel, BorderLayout.WEST);
        
        // Create content panel with a layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(initialWindowWidth, initialWindowHeight));
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        contentPanel.setBounds(0, 0, initialWindowWidth, initialWindowHeight);
        
        // Add component listener to handle resize events
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update bounds of all child components when the layered pane is resized
                contentPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                
                // After directoryPanel and detailsPanel are defined, we'll make sure they're visible
                if (directoryPanel != null && detailsPanel != null) {
                    // Get the current window width and available space
                    int availableWidth = layeredPane.getWidth();
                    
                    // Calculate the gap between panels
                    int gapBetweenPanels = 10;
                    
                    // Calculate total width of both panels plus gap
                    int totalWidth = directoryPanel.getWidth() + detailsPanel.getWidth() + gapBetweenPanels;
                    
                    // Calculate the start X position to center both panels horizontally
                    int startX = (availableWidth - totalWidth) / 2;
                    
                    // Keep vertical position the same
                    int directoryY = directoryPanel.getY();
                    int detailsY = detailsPanel.getY();
                    
                    // Calculate new bounds for each panel
                    directoryPanel.setBounds(startX, directoryY, directoryPanel.getWidth(), directoryPanel.getHeight());
                    detailsPanel.setBounds(startX + directoryPanel.getWidth() + gapBetweenPanels, detailsY, detailsPanel.getWidth(), detailsPanel.getHeight());
                    
                    directoryPanel.setVisible(true);
                    detailsPanel.setVisible(true);
                }
            }
        });
        
        // Remove tabbed pane - only use a simple panel
        JPanel simplePanel = new JPanel();
        simplePanel.setOpaque(false);
        contentPanel.add(simplePanel, BorderLayout.CENTER);
        
        // Add the content panel to the layered pane
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);
        
        // Create the candidate directory panel and add it to the layered pane
        directoryPanel = new CandidateDirectoryPanel();
        layeredPane.add(directoryPanel, JLayeredPane.PALETTE_LAYER);
        
        // Create the candidate details panel and add it to the layered pane
        detailsPanel = new CandidateDetailsPanel();
        layeredPane.add(detailsPanel, JLayeredPane.PALETTE_LAYER);
        
        // Connect the panels
        directoryPanel.setDetailsPanel(detailsPanel);
        
        // Add the panels to the UI
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(layeredPane, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Add component listener to handle resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update the layered pane size to match the window size
                layeredPane.setSize(getContentPane().getSize());
                
                // Force revalidation of all components
                revalidate();
                repaint();
                
                // Explicitly trigger the layeredPane's componentResized listener
                layeredPane.dispatchEvent(new ComponentEvent(layeredPane, ComponentEvent.COMPONENT_RESIZED));
            }
        });
    }
    
    private void loadFonts() {
        try {
            // Load Inter fonts
            File interBlackFile = new File("lib/fonts/Inter_18pt-Black.ttf");
            File interSemiBoldFile = new File("lib/fonts/Inter_18pt-SemiBold.ttf");
            File interBoldFile = new File("lib/fonts/Inter_18pt-Bold.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (interBlackFile.exists()) {
                interBlack = Font.createFont(Font.TRUETYPE_FONT, interBlackFile);
                ge.registerFont(interBlack);
            } else {
                interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            }
            
            if (interSemiBoldFile.exists()) {
                interSemiBold = Font.createFont(Font.TRUETYPE_FONT, interSemiBoldFile);
                ge.registerFont(interSemiBold);
            } else {
                interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interBoldFile.exists()) {
                interBold = Font.createFont(Font.TRUETYPE_FONT, interBoldFile);
                ge.registerFont(interBold);
            } else {
                interBold = new Font("Sans-Serif", Font.BOLD, 12);
            }
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            } else {
                interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
            } else {
                interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            }
            
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // Fallback to system fonts
            interRegular = new Font("Sans-Serif", Font.PLAIN, 12);
            interBlack = new Font("Sans-Serif", Font.BOLD, 12);
            interSemiBold = new Font("Sans-Serif", Font.PLAIN, 12);
            interBold = new Font("Sans-Serif", Font.BOLD, 12);
            interMedium = new Font("Sans-Serif", Font.PLAIN, 12);
        }
    }
    
    private void loadBackgroundImage() {
        try {
            File imageFile = new File("resources/images/Landing-Backdrop.png");
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            } else {
                createFallbackImage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            createFallbackImage();
        }
    }
    
    private void createFallbackImage() {
        // Create a simple colored rectangle as a fallback
        backgroundImage = new BufferedImage(BACKDROP_WIDTH, BACKDROP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = backgroundImage.createGraphics();
        g.setColor(new Color(230, 230, 250)); // Light lavender color
        g.fillRect(0, 0, BACKDROP_WIDTH, BACKDROP_HEIGHT);
        g.setColor(new Color(0x2F, 0x39, 0x8E, 100)); // Translucent blue
        int border = 20;
        g.fillRect(border, border, BACKDROP_WIDTH-2*border, BACKDROP_HEIGHT-2*border);
        g.dispose();
    }
    
    private void loadHeaderLogoImage() {
        try {
            // Try to load the header logo image from the specific path
            File logoFile = new File("resources/images/Candidate Search/HeaderLogo.png");
            if (logoFile.exists()) {
                headerLogoImage = ImageIO.read(logoFile);
                System.out.println("Header logo loaded successfully from: " + logoFile.getAbsolutePath());
            } else {
                System.out.println("Header logo file not found at: " + logoFile.getAbsolutePath());
                
                // Try alternative locations as backup
                String[] alternativePaths = {
                    "resources/images/HeaderLogo.png",
                    "HeaderLogo.png",
                    "images/HeaderLogo.png",
                    "images/Candidate Search/HeaderLogo.png",
                    "../resources/images/Candidate Search/HeaderLogo.png"
                };
                
                for (String path : alternativePaths) {
                    File altFile = new File(path);
                    if (altFile.exists()) {
                        headerLogoImage = ImageIO.read(altFile);
                        System.out.println("Header logo loaded from alternative path: " + altFile.getAbsolutePath());
                        break;
                    }
                }
            }
            
            // If still couldn't find the logo, create a blank image
            if (headerLogoImage == null) {
                headerLogoImage = new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = headerLogoImage.createGraphics();
                g.setColor(new Color(0, 0, 0, 0)); // Transparent
                g.fillRect(0, 0, 150, 40);
                g.dispose();
            }
        } catch (IOException e) {
            e.printStackTrace();
            
            // Create blank image even after exception
            headerLogoImage = new BufferedImage(150, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = headerLogoImage.createGraphics();
            g.setColor(new Color(0, 0, 0, 0)); // Transparent
            g.fillRect(0, 0, 150, 40);
            g.dispose();
        }
    }
    
    private void setUIFont(Font font) {
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
    }
    
    /**
     * Gets the CandidateDirectoryPanel
     * @return The directory panel
     */
    public CandidateDirectoryPanel getDirectoryPanel() {
        return directoryPanel;
    }
    
    /**
     * Refreshes the candidate directory panel by clearing and reloading candidates.
     * This method should be called after adding, editing, or deleting candidates.
     */
    public void refreshDirectoryPanel() {
        if (directoryPanel != null) {
            directoryPanel.clearCandidates();
            directoryPanel.loadCandidatesFromProfiles();
            directoryPanel.revalidate();
            directoryPanel.repaint();
        }
    }
    
    /**
     * Shows a custom notification dialog with modern styling.
     * @param parent The parent component (can be null for center of screen)
     * @param message The message to display
     * @param title The title of the dialog
     * @param type The type of notification: "info", "warning", or "error"
     */
    public static void showNotification(Component parent, String message, String title, String type) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent));
        dialog.setModal(true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(340, 140);
        dialog.setLocationRelativeTo(parent);

        // Colors and icons
        Color bgColor = Color.WHITE;
        Color borderColor = new Color(0x2B, 0x37, 0x80);
        Color textColor = new Color(0x47, 0x55, 0x69);
        Color titleColor = borderColor;
        String iconPath = null;
        if ("info".equalsIgnoreCase(type)) {
            iconPath = "resources/images/info.png";
        } else if ("warning".equalsIgnoreCase(type)) {
            iconPath = "resources/images/warning.png";
            titleColor = new Color(0xF59E42);
        } else if ("error".equalsIgnoreCase(type)) {
            iconPath = "resources/images/error.png";
            titleColor = new Color(0xEF4444);
        }

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2d.setColor(new Color(0,0,0,30));
                g2d.fillRoundRect(6, 6, getWidth()-12, getHeight()-12, 18, 18);
                // Background
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                // Border
                g2d.setColor(borderColor);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2d.dispose();
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);

        // Icon
        JLabel iconLabel = new JLabel();
        if (iconPath != null) {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(iconPath);
                iconLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
            }
        }
        iconLabel.setBounds(24, 32, 32, 32);
        panel.add(iconLabel);

        // Load Inter fonts
        Font titleFont = new Font("Sans-Serif", Font.BOLD, 18);
        Font messageFont = new Font("Sans-Serif", Font.PLAIN, 15);
        Font buttonFont = new Font("Sans-Serif", Font.BOLD, 14);
        
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font interBold = null;
            Font interRegular = null;
            Font interMedium = null;
            
            File interBoldFile = new File("lib/fonts/Inter_18pt-Bold.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            
            if (interBoldFile.exists()) {
                interBold = Font.createFont(Font.TRUETYPE_FONT, interBoldFile);
                ge.registerFont(interBold);
                titleFont = interBold.deriveFont(18f);
                buttonFont = interBold.deriveFont(14f);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
                messageFont = interRegular.deriveFont(15f);
            }
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            }
            
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // Fallback fonts already set above
        }

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(titleColor);
        titleLabel.setFont(titleFont);
        titleLabel.setBounds(70, 18, 240, 28);
        panel.add(titleLabel);

        // Message
        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setForeground(textColor);
        messageLabel.setFont(messageFont);
        messageLabel.setBounds(70, 48, 240, 40);
        panel.add(messageLabel);

        // OK button
        JButton okButton = new JButton("OK");
        okButton.setFocusPainted(false);
        okButton.setBackground(borderColor);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(buttonFont);
        okButton.setBounds(220, 95, 80, 30);
        okButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        okButton.addActionListener(e -> dialog.dispose());
        panel.add(okButton);

        panel.setPreferredSize(new Dimension(340, 140));
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a custom confirmation dialog with modern styling.
     * @param parent The parent component (can be null for center of screen)
     * @param message The message to display
     * @param title The title of the dialog
     * @return true if Yes is clicked, false otherwise
     */
    public static boolean showConfirmDialog(Component parent, String message, String title) {
        final boolean[] result = {false};
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent));
        dialog.setModal(true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(360, 150);
        dialog.setLocationRelativeTo(parent);

        Color bgColor = Color.WHITE;
        Color borderColor = new Color(0x2B, 0x37, 0x80);
        Color textColor = new Color(0x47, 0x55, 0x69);
        Color titleColor = new Color(0xF59E42);
        String iconPath = "resources/images/warning.png";

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0,0,0,30));
                g2d.fillRoundRect(6, 6, getWidth()-12, getHeight()-12, 18, 18);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2d.setColor(borderColor);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2d.dispose();
            }
        };
        panel.setLayout(null);
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        File iconFile = new File(iconPath);
        if (iconFile.exists()) {
            ImageIcon icon = new ImageIcon(iconPath);
            iconLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        }
        iconLabel.setBounds(24, 32, 32, 32);
        panel.add(iconLabel);

        // Load Inter fonts
        Font titleFont = new Font("Sans-Serif", Font.BOLD, 18);
        Font messageFont = new Font("Sans-Serif", Font.PLAIN, 15);
        Font buttonFont = new Font("Sans-Serif", Font.BOLD, 14);
        
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font interBold = null;
            Font interRegular = null;
            Font interMedium = null;
            
            File interBoldFile = new File("lib/fonts/Inter_18pt-Bold.ttf");
            File interRegularFile = new File("lib/fonts/Inter_18pt-Regular.ttf");
            File interMediumFile = new File("lib/fonts/Inter_18pt-Medium.ttf");
            
            if (interBoldFile.exists()) {
                interBold = Font.createFont(Font.TRUETYPE_FONT, interBoldFile);
                ge.registerFont(interBold);
                titleFont = interBold.deriveFont(18f);
                buttonFont = interBold.deriveFont(14f);
            }
            
            if (interRegularFile.exists()) {
                interRegular = Font.createFont(Font.TRUETYPE_FONT, interRegularFile);
                ge.registerFont(interRegular);
                messageFont = interRegular.deriveFont(15f);
            }
            
            if (interMediumFile.exists()) {
                interMedium = Font.createFont(Font.TRUETYPE_FONT, interMediumFile);
                ge.registerFont(interMedium);
            }
            
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // Fallback fonts already set above
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(titleColor);
        titleLabel.setFont(titleFont);
        titleLabel.setBounds(70, 18, 260, 28);
        panel.add(titleLabel);

        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setForeground(textColor);
        messageLabel.setFont(messageFont);
        messageLabel.setBounds(70, 48, 260, 40);
        panel.add(messageLabel);

        JButton yesButton = new JButton("Yes");
        yesButton.setFocusPainted(false);
        yesButton.setBackground(borderColor);
        yesButton.setForeground(Color.WHITE);
        yesButton.setFont(buttonFont);
        yesButton.setBounds(160, 100, 80, 30);
        yesButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        yesButton.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        panel.add(yesButton);

        JButton noButton = new JButton("No");
        noButton.setFocusPainted(false);
        noButton.setBackground(new Color(0xE2, 0xE8, 0xF0));
        noButton.setForeground(borderColor);
        noButton.setFont(buttonFont);
        noButton.setBounds(250, 100, 80, 30);
        noButton.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        noButton.addActionListener(e -> { result[0] = false; dialog.dispose(); });
        panel.add(noButton);

        panel.setPreferredSize(new Dimension(360, 150));
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return result[0];
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AdminPanelUI adminPanel = new AdminPanelUI();
                
                // Set minimum size to ensure panels have enough space
                adminPanel.setMinimumSize(new Dimension(1280, 800));
                
                // Set initial size and center on screen
                adminPanel.setSize(1440, 900);
                adminPanel.setLocationRelativeTo(null);
                
                adminPanel.setVisible(true);
                
                // Print a message to help with debugging
                System.out.println("Admin Panel UI launched successfully");
            }
        });
    }
} 