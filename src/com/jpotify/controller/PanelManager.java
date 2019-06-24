package com.jpotify.controller;

import com.jpotify.logic.*;
import com.jpotify.logic.exceptions.NoTagFoundException;
import com.jpotify.logic.network.FriendManager;
import com.jpotify.logic.network.FriendManagerListener;
import com.jpotify.logic.network.Server;
import com.jpotify.logic.network.ServerListener;
import com.jpotify.view.Listeners.ListenerManager;
import com.jpotify.view.helper.MButton;
import com.jpotify.view.helper.MainPanelState;
//import com.sun.deploy.jcp.controller.Network;
import mpatric.mp3agic.InvalidDataException;
import mpatric.mp3agic.UnsupportedTagException;

import javax.swing.*;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PanelManager extends ListenerManager implements PlayerListener {

    private DataBase dataBase;
    private Player player;
    private NetworkManager networkManager;

    public PanelManager(DataBase dataBase, Player player) {
        this.dataBase = dataBase;
        this.player = player;
    }

    public PanelManager(DataBase dataBase) {
        this.dataBase = dataBase;
        this.player = new Player(this);
        try {
            networkManager = new NetworkManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // MenuPanelListener implementation
    @Override
    public void home() {

    }

    @Override
    public void songs() {
        getGUI().getMainPanel().removeAll();
        getGUI().getMainPanel().addPanels(dataBase.getMusicsArray());
        getGUI().getMainPanel().setMainPanelState(MainPanelState.SONGS);
    }

    @Override
    public void addSongButton(File file) {
        try {
            Music music = new Music(file);

            if (dataBase.addSong(music) == 0)
                JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                        "File is already exist in your library");
            else {

                if (getGUI().getMainPanel().getMainPanelState() == MainPanelState.SONGS)
                    getGUI().getMainPanel().addPanel(music);

                if (getGUI().getMainPanel().getMainPanelState() == MainPanelState.ALBUMS)
                    this.albums();

                JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                        music.getTitle() + " added to your Library");
            }
        } //for Testing #Test
        catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (NoTagFoundException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                    "Can't Add file",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            System.out.println(e.getCause() + e.getMessage());
        }

    }

    @Override
    public void albums() {
        getGUI().getMainPanel().removeAll();
        getGUI().getMainPanel().addPanels(dataBase.getAlbumsArray());
        getGUI().getMainPanel().setMainPanelState(MainPanelState.ALBUMS);
    }

    @Override
    public void playListClicked(String name) {
        getGUI().getMainPanel().removeAll();
        getGUI().getMainPanel().addPanels(dataBase.getMusicByPlayListTitle(name));
        if (name.equals("Favourites"))
            getGUI().getMainPanel().setMainPanelState(MainPanelState.Favorites);
        else if (name.equals("Shared PlayList"))
            getGUI().getMainPanel().setMainPanelState(MainPanelState.Shared);
        else
            getGUI().getMainPanel().setMainPanelState(MainPanelState.OtherPlayList);

        getGUI().getMainPanel().repaint();
        getGUI().getMainPanel().revalidate();
    }

    @Override
    public void newPlayList(String name) {

        for (PlayList playList : dataBase.getPlayLists()) {
            if (playList.getTitle().equals(name)) {
                JOptionPane.showMessageDialog(null,
                        "This Playlist is already exist");
                return;
            }
        }
        dataBase.createPlayList(name);
        getGUI().getMenuPanel().getPlayList().addButton(new MButton(name, true));
        loadPlaylists();
    }

    @Override
    public void loadPlaylists() {

        getGUI().getMenuPanel().getPlayList().removeAll();

        for (PlayList playList : dataBase.getPlayLists()) {
            MButton mButton = new MButton(playList.getTitle());
            Color PerformedColor = Color.white;
            Color defaultColor = Color.LIGHT_GRAY;

            ActionListener playListActionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getGUI().getMenuPanel().getListener().playListClicked(playList.getTitle());
                }
            };

            MouseListener playListMouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
//                        label.setText("Left Click!");
                    }
                    if (e.getButton() == MouseEvent.BUTTON2) {
//                        label.setText("Middle Click!");
                    }
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        if (playList.getTitle().equals("Favourites") || playList.getTitle().equals("Shared PlayList")) {
                            String[] buttons = {"Play", "Change Order"};
                            int returnValue = JOptionPane.showOptionDialog(null, "What do you want to do with " + "\"" + playList.getTitle() + "\"", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);
                            if(returnValue == 1){
                                String selectedFirstSong = (String) JOptionPane.showInputDialog(
                                        getGUI().getMainPanel(),
                                        "select song : ",
                                        "First Song",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        dataBase.getPlayListByTitle(playList.getTitle()).getSongsName(),
                                        dataBase.getPlayListByTitle(playList.getTitle()).getSongsName()[0]);

                                String selectedSecondSong = (String) JOptionPane.showInputDialog(
                                        getGUI().getMainPanel(),
                                        "select song : ",
                                        "First Song",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        dataBase.getPlayListByTitle(playList.getTitle()).getSongsName(),
                                        dataBase.getPlayListByTitle(playList.getTitle()).getSongsName()[0]);

                                Collections.swap(playList,playList.indexOf(selectedFirstSong),playList.indexOf(selectedSecondSong));
                                loadPlaylists();
                            }





                        } else {
                            String[] buttons = {"Play", "Edit Name", "Change Order", "Delete"};
                            int returnValue = JOptionPane.showOptionDialog(null, "What do you want to do with " + "\"" + playList.getTitle() + "\"", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);

                            if (returnValue == 3) {
                                int n = JOptionPane.showConfirmDialog(
                                        getGUI().getMainPanel(),
                                        "Do you want to delete " + playList.getTitle() + " playlist?",
                                        "Delete PlayList",
                                        JOptionPane.YES_NO_OPTION);
                                if(n == 0) {
                                    dataBase.getPlayLists().remove(playList);
                                    loadPlaylists();
                                }
                            }

                            if (returnValue == 1) {
                                String name = JOptionPane.showInputDialog(
                                        getGUI().getMainPanel(),
                                        "Name that you want :)"
                                );
                                for (PlayList playList : dataBase.getPlayLists()) {
                                    if (playList.getTitle().equals(name)) {
                                        JOptionPane.showMessageDialog(null,
                                                "This Playlist is already exist");
                                        return;
                                    }
                                }
                                mButton.setText(name);
//                                loadPlaylists();
                            }


                            System.out.println(returnValue);
//                        label.setText("Right Click!");
                        }
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    mButton.setForeground(defaultColor);
                    mButton.setIcon(null);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    mButton.setForeground(PerformedColor);
                    mButton.setIcon(null);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    mButton.setForeground(PerformedColor);
                    mButton.setIcon(null);
                    mButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mButton.setForeground(defaultColor);
                    mButton.setIcon(null);
                    mButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            };

            mButton.addActionListener(playListActionListener);
            mButton.addMouseListener(playListMouseListener);

            getGUI().getMenuPanel().addPlayList(mButton);
        }
        getGUI().getMenuPanel().getPlayList().repaint();
        getGUI().getMenuPanel().getPlayList().revalidate();

    }

    // PlayerPanelListener implementation
    @Override
    public void play() {
        player.playMusic();
    }

    @Override
    public void pause() {
        player.pauseMusic();
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    @Override
    public void shuffle() {

    }

    @Override
    public void replay() {

    }

    @Override
    public void sliderChanged(int newPosition) {
        player.changePositionRelative(newPosition);
    }

    @Override
    public void soundVolumeChanged(int newPosition) {
        player.updateSoundVolume(newPosition);
    }

    // MainPanelListener implementation
    @Override
    public void panelClicked(String id) {
        //#Test
        switch (getGUI().getMainPanel().getMainPanelState()) {
            case SONGS:
                Music music = dataBase.getMusicById(id);
                player.updateMusic(music);
                music.updateLastPlayedTime();
                player.playMusic();
                getGUI().setMusicData(music.getTitle(), music.getArtist(), music.getAlbumImage());
                getGUI().getPlayerPanel().setToPauseToggleButton();
                songs();
                break;
            case ALBUMS:
                getGUI().getMainPanel().removeAll();
                getGUI().getMainPanel().addPanels(dataBase.getMusicByAlbumTitle(id));
                getGUI().getMainPanel().setMainPanelState(MainPanelState.SONGS);
                break;
            case Favorites:
                Music music1 = dataBase.getMusicById(id);
                player.updateMusic(music1);
                music1.updateLastPlayedTime();
                player.playMusic();
                getGUI().setMusicData(music1.getTitle(), music1.getArtist(), music1.getAlbumImage());
                break;
            case Shared:
                Music music2 = dataBase.getMusicById(id);
                player.updateMusic(music2);
                music2.updateLastPlayedTime();
                player.playMusic();
                getGUI().setMusicData(music2.getTitle(), music2.getArtist(), music2.getAlbumImage());
                break;
            default:
        }

    }

    @Override
    public void buttonAdd(String id) {
        String selectedPlayList = (String) JOptionPane.showInputDialog(
                getGUI().getMainPanel(),
                "select playlist : ",
                "Add to PlayList",
                JOptionPane.PLAIN_MESSAGE,
                null,
                dataBase.getPlaylistsNames(),
                dataBase.getPlaylistsNames()[0]);

        if (selectedPlayList != null) {
            dataBase.getPlayListByTitle(selectedPlayList).add(dataBase.getMusicById(id));
        }
    }

    @Override
    public void buttonLike(String id) {
        if (dataBase.addSongToPlayList(dataBase.getMusicById(id), dataBase.getPlayLists().get(0)) == 0) // 0 is Favourites
            JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                    "File is already exist in your Favourites");
        else {

            if (getGUI().getMainPanel().getMainPanelState() == MainPanelState.Favorites)
                getGUI().getMainPanel().addPanel(dataBase.getMusicById(id));


            JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                    dataBase.getMusicById(id).getTitle() + " added to your Favourites");
        }
    }

    @Override
    public void buttonShare(String id) {
        if (dataBase.addSongToPlayList(dataBase.getMusicById(id), dataBase.getPlayLists().get(1)) == 0) // 1 is Shared PlayList
            JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                    "File is already exist in your Shared PlayList");
        else {

            if (getGUI().getMainPanel().getMainPanelState() == MainPanelState.Shared)
                getGUI().getMainPanel().addPanel(dataBase.getMusicById(id));


            JOptionPane.showMessageDialog(getGUI().getMainPanel(),
                    dataBase.getMusicById(id).getTitle() + " added to your Shared PlayList");
        }
    }

    // GUI
    @Override
    public void closingProgram() {
        dataBase.saveDataBase();
        networkManager.stopNetwork();
    }

    @Override
    public void updatePosition(int position) {
        getGUI().getPlayerPanel().setSliderCurrentPosition(position);
    }

    @Override
    public void musicFinished() {

    }

    @Override
    public void friendPanelClicked(String id) {

    }

    private class NetworkManager implements ServerListener, FriendManagerListener {
        private Server server;
        private FriendManager friendManager;

        //for Testing #Test
        private String[] friendIps = {"172.24.26.139"};

        public NetworkManager() throws IOException {
            server = new Server(dataBase.getUsername(), this);
            friendManager = new FriendManager(dataBase.getUsername(), friendIps, this);

            new Thread(server).start();
            new Thread(friendManager).start();
        }

        public void stopNetwork() {
            friendManager.stop();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
            server.closeServer();
        }

        //FriendManagerListener methods
        @Override
        public PlayList getSharePlayList() {
            return null;
        }

        //ServerListener methods
        @Override
        public void friendBecomeOnline(String username) {
            System.out.println(username + " Become Online");
        }

        @Override
        public void friendMusicStarted(String username, Music music) {

        }

        @Override
        public void friendMusicEnded(String username, Music music) {

        }

        @Override
        public void sharedPlayListData(String username, PlayList playList) {

        }
    }
}
