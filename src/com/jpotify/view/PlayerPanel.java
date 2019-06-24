package com.jpotify.view;

import com.jpotify.view.Listeners.PlayerPanelListener;
import com.jpotify.view.assets.AssetManager;
import com.jpotify.view.helper.MButton;
import com.jpotify.view.helper.MTextArea;
import com.jpotify.view.helper.MToggleButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerPanel extends JPanel implements ActionListener, ChangeListener {
    private PlayerPanelListener listener;
    private JSlider slider;
    private MTextArea musicName;
    private MTextArea singerName;
    private MToggleButton playPauseButton;

    PlayerPanel(PlayerPanelListener listener) {
        this.listener = listener;
        setup();
    }

    private void setup() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 5, 20));

        setupCenterSection();

        setupLeftSection();

        setupRightSection();

    }

    private void setupCenterSection() {
        JPanel centerBox = new JPanel();
        centerBox.setOpaque(false);
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        add(centerBox, BorderLayout.CENTER);

        JPanel controllers = new JPanel();
        controllers.setBorder(new EmptyBorder(0, 0, 10, 0));
        controllers.setOpaque(false);
        controllers.setLayout(new BoxLayout(controllers, BoxLayout.X_AXIS));
        centerBox.add(controllers);

        controllers.add(new MButton(AssetManager.getImageIconByName("shuffle.png"), this, "shuffle"));
        controllers.add(new MButton(AssetManager.getImageIconByName("previous.png"), this, "previous"));

        controllers.add(playPauseButton = new MToggleButton(AssetManager.getImageIconByName("play.png"),
                AssetManager.getImageIconByName("pause.png"), this, "play"));

        controllers.add(new MButton(AssetManager.getImageIconByName("next.png"), this, "next"));
        controllers.add(new MButton(AssetManager.getImageIconByName("replay.png"), this, "replay"));

        slider = new JSlider();
        slider.addChangeListener(this);
        slider.setOpaque(false);
        slider.setBorder(new EmptyBorder(0, 100, 0, 100));
        slider.setMaximum(1000);
        slider.setUI(new MetalSliderUI() {
            @Override
            protected void scrollDueToClickInTrack(int dir) {
                int value = this.valueForXPosition(slider.getMousePosition().x);
                slider.setValue(value);
            }
        });
        centerBox.add(slider);
    }

    private void setupLeftSection() {
        JPanel leftBox = new JPanel();
        leftBox.setOpaque(false);
        leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
        leftBox.setBorder(new EmptyBorder(0, 0, 0, 20));
        add(leftBox, BorderLayout.LINE_START);


        musicName = new MTextArea("Music Name", Color.WHITE, true);
        musicName.setFont(new Font("Arial", Font.BOLD, 13));
        leftBox.add(musicName);

        singerName = new MTextArea("Singer", null, true);
        singerName.setFont(new Font("Arial", Font.PLAIN, 11));
        leftBox.add(singerName);
    }

    private void setupRightSection() {
        JPanel rightBox = new JPanel();
        rightBox.setOpaque(false);
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.X_AXIS));
        rightBox.setBorder(new EmptyBorder(0, 20, 0, 0));
        add(rightBox, BorderLayout.LINE_END);

        rightBox.add(new MButton(AssetManager.getImageIconByName("test.png"), this, "test"));
        rightBox.add(new MButton(AssetManager.getImageIconByName("test.png"), this, "test"));
        rightBox.add(new MButton(AssetManager.getImageIconByName("test.png"), this, "test"));
        rightBox.add(new MButton(AssetManager.getImageIconByName("test.png"), this, "test"));
        rightBox.add(new MButton(AssetManager.getImageIconByName("test.png"), this, "test"));
    }

//    public void setMediaMaxFrame(int frame) {
//        slider.setMaximum(frame);
//    }

    public void setSliderCurrentPosition(int frame) {
        if (!slider.getValueIsAdjusting()) {
            slider.removeChangeListener(this);
            slider.setValue(frame);
            slider.addChangeListener(this);
        }
    }

    public void setDataMusicData(String title, String singer) {
        musicName.setText(title);
        singerName.setText(singer);
    }

    public void setToPauseToggleButton() {
        playPauseButton.changeToSecond();
    }

//    public void updateSliderAFrame() {
//        slider.setValue(slider.getValue() + 1);
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((MButton) e.getSource()).getId()) {
            case "play":
                if (e.getActionCommand().equals("true"))
                    listener.play();
                else
                    listener.pause();
                break;
            case "next":
                listener.next();
                break;
            case "previous":
                listener.previous();
                break;
            case "replay":
                listener.replay();
                break;
            case "shuffle":
                listener.shuffle();
                break;
            default:
        }
    }

    //for jSlider
    @Override
    public void stateChanged(ChangeEvent e) {
        if (!slider.getValueIsAdjusting())
            listener.sliderChanged(slider.getValue());
    }
}
