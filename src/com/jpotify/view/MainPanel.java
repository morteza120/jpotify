package com.jpotify.view;

import com.jpotify.view.Listeners.MainPanelListener;
import com.jpotify.view.helper.DrawableItem;
import com.jpotify.view.helper.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MainPanel extends JPanel {
    private final int ITEM_WIDTH = 200, ITEM_HEIGHT = 250;

    private MainPanelListener listener;

    public MainPanel(MainPanelListener listener) {
        this.listener = listener;
        setLayout(new WrapLayout(0, 30, 30));

    }

    public void addPanel(DrawableItem item) {
        JPanel panel = item.draw(ITEM_WIDTH, ITEM_HEIGHT);
        panel.setBackground(Color.DARK_GRAY);
        add(panel);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                listener.panelClicked(item.getId());
            }
        });

        repaint();
        revalidate();
    }

    public void addPanels(DrawableItem[] items) {
        for (DrawableItem item : items)
            addPanel(item);
    }


}
