package com.jpotify.view.helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MButton extends JButton implements DrawableItem {
    private Color defaultColor;
    private Icon defaultIcon;
    private String id;

    public MButton(String text, Icon defaultIcon, Color defaultColor,
                   boolean enableDefaultHover, ActionListener listener) {
        super(text, defaultIcon);
        this.defaultColor = defaultColor;
        this.defaultIcon = defaultIcon;
        this.id = text;
        firstSetup();

        addActionListener(listener);
        if (enableDefaultHover)
            setDefaultHoverEffect();
    }

    public MButton(String text, Icon defaultIcon, Color defaultColor) {
        this(text, defaultIcon, defaultColor, false, null);
    }

    public MButton(String text, Icon icon) {
        this(text, icon, Color.LIGHT_GRAY);
    }

    public MButton(String text, Icon icon, boolean enableDefaultHover, ActionListener listener) {
        this(text, icon, Color.LIGHT_GRAY, enableDefaultHover, listener);
    }

    public MButton(String text, Icon icon, boolean enableDefaultHover) {
        this(text, icon, Color.LIGHT_GRAY, enableDefaultHover, null);
    }

    public MButton(String text) {
        this(text, null);
    }

    public MButton(String text, boolean enableDefaultHover) {
        this(text, null, Color.LIGHT_GRAY, enableDefaultHover, null);
    }

    public MButton(String text, boolean enableDefaultHover, ActionListener listener) {
        this(text, null, Color.LIGHT_GRAY, enableDefaultHover, listener);
    }

    public MButton(String text, ActionListener actionListener, MouseListener mouseListener) {
        this(text, null, Color.LIGHT_GRAY, false, actionListener);
        addMouseListener(mouseListener);
    }

    public MButton(Icon defaultIcon, String id) {
        this(null, defaultIcon, false);
        this.id = id;
    }

    public MButton(Icon defaultIcon, ActionListener listener, String id) {
        this(null, defaultIcon, false, listener);
        this.id = id;
    }

    public MButton(Icon defaultIcon, Icon performedIcon, String id) {
        this(null, defaultIcon, false);
        setHoverEffect(null, performedIcon);
        this.id = id;
    }

    public MButton(Icon defaultIcon, Icon performedIcon, ActionListener listener, String id) {
        this(null, defaultIcon, false, listener);
        setHoverEffect(null, performedIcon);
        this.id = id;
    }

    private void firstSetup() {
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFont(new Font("Arial", Font.BOLD, 15));
        setForeground(defaultColor);
        setFocusPainted(false);
    }

    protected Icon getDefaultIcon() {
        return defaultIcon;
    }

    @Override
    public JPanel draw(int width, int height) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(this, BorderLayout.PAGE_START);
        return panel;
    }

    public String getId() {
        return id;
    }

    public void setDefaultHoverEffect() {
        setTextHoverEffect(Color.WHITE);
    }

    public void setTextHoverEffect(Color performedColor) {
        setHoverEffect(performedColor, defaultIcon);
    }

    public void setHoverEffect(Color PerformedColor, Icon performedIcon) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(PerformedColor);
                setIcon(performedIcon);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(defaultColor);
                setIcon(defaultIcon);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setForeground(defaultColor);
                setIcon(defaultIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setForeground(PerformedColor);
                setIcon(performedIcon);
            }
        });
    }

}
