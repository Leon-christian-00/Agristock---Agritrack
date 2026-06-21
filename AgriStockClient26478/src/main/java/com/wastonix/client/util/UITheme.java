package com.wastonix.client.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;

public class UITheme {

    
    public static final Color GREEN_900   = new Color(20,  83,  45);
    public static final Color GREEN_800   = new Color(22, 101,  52);
    public static final Color GREEN_600   = new Color(22, 163,  74);
    public static final Color GREEN_400   = new Color(74, 222, 128);
    public static final Color GREEN_100   = new Color(220, 252, 231);
    public static final Color GREEN_50    = new Color(240, 253, 244);

    public static final Color AMBER_500   = new Color(245, 158,  11);
    public static final Color AMBER_100   = new Color(254, 243, 199);

    public static final Color RED_600     = new Color(220,  38,  38);
    public static final Color RED_100     = new Color(254, 226, 226);

    public static final Color BLUE_600    = new Color(37, 99, 235);
    public static final Color PURPLE_600  = new Color(124, 58, 237);

    public static final Color SLATE_900   = new Color(15,  23,  42);
    public static final Color SLATE_800   = new Color(30,  41,  59);
    public static final Color SLATE_700   = new Color(51,  65,  85);
    public static final Color SLATE_500   = new Color(100, 116, 139);
    public static final Color SLATE_300   = new Color(203, 213, 225);
    public static final Color SLATE_100   = new Color(241, 245, 249);
    public static final Color SLATE_50    = new Color(248, 250, 252);
    public static final Color WHITE       = Color.WHITE;

    
    public static final Color PRIMARY       = GREEN_600;
    public static final Color PRIMARY_DARK  = GREEN_800;
    public static final Color PRIMARY_LIGHT = GREEN_100;
    public static final Color ACCENT        = AMBER_500;
    public static final Color DANGER        = RED_600;
    public static final Color INFO          = BLUE_600;
    public static final Color BG            = SLATE_50;
    public static final Color CARD_BG       = WHITE;
    public static final Color TEXT_MAIN     = SLATE_900;
    public static final Color TEXT_MUTED    = SLATE_500;
    public static final Color BORDER_COLOR  = SLATE_300;
    public static final Color TABLE_HEADER  = GREEN_800;
    public static final Color TABLE_ALT     = GREEN_50;

    
    public static final Font FONT_DISPLAY = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_H2      = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO    = new Font("Consolas", Font.BOLD, 22);
    public static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 10);

    
    public static void applyGlobalDefaults() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        UIManager.put("Panel.background",              BG);
        UIManager.put("OptionPane.background",         CARD_BG);
        UIManager.put("OptionPane.messageForeground",  TEXT_MAIN);
        UIManager.put("Button.font",                   FONT_BODY);
        UIManager.put("Label.font",                    FONT_BODY);
        UIManager.put("TextField.font",                FONT_BODY);
        UIManager.put("ComboBox.font",                 FONT_BODY);
        UIManager.put("Table.font",                    FONT_BODY);
        UIManager.put("TableHeader.font",              FONT_BOLD);
        UIManager.put("TitledBorder.font",             FONT_BOLD);
        UIManager.put("TitledBorder.titleColor",       PRIMARY_DARK);
        UIManager.put("ScrollBar.width",               8);
        UIManager.put("ScrollBar.thumb",               SLATE_300);
        UIManager.put("ScrollBar.track",               SLATE_100);
    }

    public static String cleanErrorMessage(String msg) {
        if (msg == null) return "An unexpected error occurred.";
        String cleaned = msg.replaceAll("(?i)^.*?RemoteException(?: occurred in server thread; nested exception is: )?\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^(java\\.rmi\\.RemoteException:\\s*)+", "");
        cleaned = cleaned.replaceAll("(?i)^Error\\s+[^:]+:\\s*", "");
        cleaned = cleaned.replaceAll("(?i)^could not execute statement\\s*\\[.*?\\]\\s*", "");
        cleaned = cleaned.trim();
        if (cleaned.matches("(?i).*duplicate key value violates unique constraint.*Key \\(phone\\).*")) return "Phone number is already registered.";
        if (cleaned.matches("(?i).*duplicate key value violates unique constraint.*Key \\(email\\).*")) return "Email is already registered.";
        if (cleaned.matches("(?i).*already exists.*phone.*")) return "Phone number is already registered.";
        if (cleaned.matches("(?i).*already exists.*email.*")) return "Email is already registered.";
        return cleaned.isEmpty() ? "An unexpected error occurred." : cleaned;
    }

    
    public static JLabel display(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_DISPLAY); l.setForeground(WHITE); return l;
    }
    public static JLabel title(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_TITLE); l.setForeground(PRIMARY_DARK); return l;
    }
    public static JLabel h2(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_H2); l.setForeground(TEXT_MAIN); return l;
    }
    public static JLabel label(String text) {
        JLabel l = new JLabel(text); l.setFont(FONT_BOLD); l.setForeground(SLATE_700); return l;
    }
    public static JLabel muted(String text) {
        JLabel l = new JLabel(text, JLabel.CENTER); l.setFont(FONT_SMALL); l.setForeground(TEXT_MUTED); return l;
    }
    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text, JLabel.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(FONT_CAPTION); l.setForeground(fg); l.setBackground(bg);
        l.setOpaque(false); l.setBorder(new EmptyBorder(3, 10, 3, 10));
        return l;
    }

    
    public static JTextField field() { return field(0); }
    public static JTextField field(int cols) {
        JTextField f = cols > 0 ? new JTextField(cols) : new JTextField();
        f.setFont(FONT_BODY); f.setBackground(WHITE); f.setForeground(TEXT_MAIN);
        f.setCaretColor(PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(9, 12, 9, 12)));
        return f;
    }
    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_MONO); f.setBackground(WHITE); f.setForeground(PRIMARY_DARK);
        f.setCaretColor(PRIMARY); f.setHorizontalAlignment(JTextField.CENTER);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(9, 12, 9, 12)));
        return f;
    }
    public static <T> JComboBox<T> combo() {
        JComboBox<T> c = new JComboBox<>();
        c.setFont(FONT_BODY); c.setBackground(WHITE); c.setForeground(TEXT_MAIN);
        c.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        return c;
    }
    public static JComboBox<String> combo(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(FONT_BODY); c.setBackground(WHITE); c.setForeground(TEXT_MAIN);
        return c;
    }

    
    public static JButton primaryBtn(String text)   { return btn(text, PRIMARY,    WHITE,    GREEN_900,  WHITE); }
    public static JButton secondaryBtn(String text) { return btn(text, GREEN_100,  PRIMARY_DARK, GREEN_200(), PRIMARY_DARK); }
    public static JButton dangerBtn(String text)    { return btn(text, RED_600,    WHITE,    new Color(185,28,28), WHITE); }
    public static JButton accentBtn(String text)    { return btn(text, AMBER_500,  WHITE,    new Color(217,119,6), WHITE); }
    public static JButton ghostBtn(String text)     { return btn(text, new Color(0,0,0,0), SLATE_700, SLATE_100, SLATE_700); }

    private static Color GREEN_200() { return new Color(187, 247, 208); }

    private static JButton btn(String text, Color bg, Color fg, Color hoverBg, Color hoverFg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hoverBg : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BOLD); b.setForeground(fg); b.setBackground(bg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setContentAreaFilled(false); b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setForeground(hoverFg); b.repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setForeground(fg);      b.repaint(); }
        });
        return b;
    }

    
    public static void styleTable(JTable t) {
        t.setFont(FONT_BODY); t.setRowHeight(38);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(GREEN_100); t.setSelectionForeground(GREEN_900);
        t.setBackground(CARD_BG); t.setForeground(TEXT_MAIN);
        t.setFillsViewportHeight(true);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setFont(FONT_BODY);
                if (!sel) {
                    setBackground(row % 2 == 0 ? CARD_BG : TABLE_ALT);
                    setForeground(TEXT_MAIN);
                } else {
                    setBackground(GREEN_100);
                    setForeground(GREEN_900);
                }
                return this;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setFont(FONT_BOLD); h.setBackground(TABLE_HEADER); h.setForeground(WHITE);
        h.setBorder(BorderFactory.createEmptyBorder()); h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(h.getWidth(), 42));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, v, sel, foc, row, col);
                setBackground(TABLE_HEADER);
                setForeground(WHITE);
                setFont(FONT_BOLD);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        };
        h.setDefaultRenderer(headerRenderer);
    }

    public static JScrollPane scrollPane(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        sp.getViewport().setBackground(CARD_BG);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(20, 20, 20, 20)));
        return p;
    }

    public static Border sectionBorder(String title) {
        return BorderFactory.createTitledBorder(
            new LineBorder(BORDER_COLOR, 1, true), "  " + title + "  ",
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, PRIMARY_DARK);
    }

    
    public static JPanel topBar(String icon, String title, String subtitle) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, GREEN_900, getWidth(), 0, GREEN_800));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 28, 18, 28));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(44, 44));
        JLabel iconLbl = new JLabel(icon, JLabel.CENTER);
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLbl.setForeground(WHITE);
        iconCircle.add(iconLbl);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE); titleLbl.setForeground(WHITE);
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_SMALL); subLbl.setForeground(new Color(167, 243, 208));
        textPanel.add(titleLbl); textPanel.add(subLbl);

        left.add(iconCircle); left.add(textPanel);
        bar.add(left, BorderLayout.WEST);
        return bar;
    }

    public static JPanel statusBar(String text) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 7));
        bar.setBackground(SLATE_100);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));

        
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GREEN_600);
                g2.fillOval(0, 2, 8, 8);
                g2.dispose();
            }
        };
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(8, 14));

        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL); lbl.setForeground(TEXT_MUTED);
        bar.add(dot); bar.add(lbl);
        return bar;
    }

    
    public static JSeparator divider() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER_COLOR);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}
