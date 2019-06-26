package com.jpotify.controller;

import com.jpotify.logic.*;
import com.jpotify.logic.network.FriendManager;
import com.jpotify.logic.network.FriendManagerListener;
import com.jpotify.logic.network.Server;
import com.jpotify.logic.network.ServerListener;
import com.jpotify.view.Listeners.ListenerManager;
import com.jpotify.view.helper.ListDialog;
import com.jpotify.view.helper.MButton;
import com.jpotify.view.helper.MainPanelState;
import com.jpotify.view.helper.MultiSelectListDialog;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PanelManager extends ListenerManager implements PlayerListener {

    public String networkUsername = null;
    private volatile DataBase dataBase;
    private Player player;
    private NetworkManager networkManager;

    public PanelManager(DataBase dataBase) {
        this.dataBase = dataBase;
        this.player = new Player(this);
        try {
            networkManager = new NetworkManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private DefaultListModel<String> createStringListModel(String[] strings) {
        final String[] listElements = strings;
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (String element : listElements) {
            listModel.addElement(element);
        }
        return listModel;
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
    public void removeSongButton() {
        ArrayList<String> names = new ArrayList<>();
//        PlayList currentPlayList = dataBase.getPlayListByTitle(playList.getTitle());
        for (Music music : dataBase.getMusics())
            names.add(music.getTitle());

        JList list = new JList(names.toArray(new String[0]));
        MultiSelectListDialog dialog = new MultiSelectListDialog("Please select an item in the list: ", list);
        dialog.setOnOk(action -> dialog.createSelectedItems());
        dialog.show();

        for (String s : dialog.getSelectedItems()) {
            for (PlayList playList : dataBase.getPlayLists()) {
                for (Music music : playList.getMusics())
                    if (music.getTitle().equals(s)) {
                        playList.remove(music);
                    }
            }

            for (Album album : dataBase.getAlbums()) {
                for (Music music : album.getMusics())
                    if (music.getTitle().equals(s))
                        album.remove(music);
            }

            for (Album album : dataBase.getAlbums())
                if (album.size() == 0)
                    dataBase.getAlbums().remove(album);

            for (Music music : dataBase.getMusics()) {
                if (music.getTitle().equals(s))
                    dataBase.getMusics().remove(music);
            }
        }

//        playListClicked(currentPlayList.getTitle());
        songs();
        getGUI().showMessage("Musics removed");
    }

    @Override
    public void addSongButton(File file) {
        try {
            Music music = new Music(file);

            if (dataBase.addSong(music)) {
                if (getMainPanelState() == MainPanelState.SONGS)
                    songs();

                if (getMainPanelState() == MainPanelState.ALBUMS)
                    albums();

                getGUI().showMessage(music.getTitle() + " added to your Library");
            } else {
                getGUI().showMessage("File is already exist in your library");
            }
        } catch (Exception e) {
            getGUI().showMessage("Can't Add file");
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
        getGUI().getMainPanel().addPanels(dataBase.getPlayListByTitle(name).getMusics());
        if (name.equals("Favourites"))
            getGUI().getMainPanel().setMainPanelState(MainPanelState.Favorites);
        else if (name.equals("Shared PlayList"))
            getGUI().getMainPanel().setMainPanelState(MainPanelState.Shared);
        else
            getGUI().getMainPanel().setMainPanelState(MainPanelState.OtherPlayList);
    }

    @Override
    public void newPlayList(String name) {
        if (name.isEmpty()) {
            getGUI().showMessage("Invalid name");
            return;
        }

        if (dataBase.getPlayListByTitle(name) == null) {
            dataBase.createPlayList(name);
            getGUI().getMenuPanel().getPlayList().addButton(new MButton(name, true));
            loadPlaylists();
        } else
            getGUI().showMessage("This Playlist is already exist");

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
                    playListClicked(playList.getTitle());
                }
            };

            MouseListener playListMouseListener = new MouseAdapter() {
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
                            String[] buttons = {"Play", "Change Order", "Delete Music"};
                            int returnValue = JOptionPane.showOptionDialog(null, "What do you want to do with " + "\"" + playList.getTitle() + "\"", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);
                            if (returnValue == 1) {
                                ChangeOrder(playList);
                            }
                            if (returnValue == 0) {
                                // play here
                            }
                            if (returnValue == 2) {
                                removeSelectedMusics(playList);
                            }


                        } else {
                            String[] buttons = {"Play", "Edit Name", "Change Order", "Delete", "Delete Music"};
                            int returnValue = JOptionPane.showOptionDialog(null, "What do you want to do with " + "\"" + playList.getTitle() + "\"", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons, null);

                            if (returnValue == 0) {
                                // play here
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
                                if (name != null)
                                    if (name.trim() != "")
                                        mButton.setText(name);
                            }

                            if (returnValue == 2) {
                                ChangeOrder(playList);
                            }

                            if (returnValue == 3) {
                                int n = JOptionPane.showConfirmDialog(
                                        getGUI().getMainPanel(),
                                        "Do you want to delete " + playList.getTitle() + " playlist?",
                                        "Delete PlayList",
                                        JOptionPane.YES_NO_OPTION);
                                if (n == 0) {
                                    dataBase.getPlayLists().remove(playList);
                                    loadPlaylists();
                                }
                            }

                            if (returnValue == 4) {
                                removeSelectedMusics(playList);
                            }
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

    private void removeSelectedMusics(PlayList playList) {
        ArrayList<String> names = new ArrayList<>();
        PlayList currentPlayList = dataBase.getPlayListByTitle(playList.getTitle());
        for (Music music : currentPlayList.getMusics())
            names.add(music.getTitle());

        JList list = new JList(names.toArray(new String[0]));
        MultiSelectListDialog dialog = new MultiSelectListDialog("Please select an item in the list: ", list);
        dialog.setOnOk(action -> dialog.createSelectedItems());
        dialog.show();

        for (String s : dialog.getSelectedItems()) {
            for (Music music : currentPlayList.getMusics())
                if (music.getTitle().equals(s))
                    currentPlayList.remove(music);
        }

        playListClicked(currentPlayList.getTitle());
        getGUI().showMessage("Musics removed");
    }

    private void ChangeOrder(PlayList playList) {
        String[] newOrderNames;
        PlayList currentPlayList = dataBase.getPlayListByTitle(playList.getTitle());
        DefaultListModel<String> myListModel = createStringListModel(currentPlayList.getSongsName());
        newOrderNames = getNewOrderNames(myListModel);
        PlayList newPlayList = dataBase.createNewPlayListByOrder(currentPlayList, newOrderNames);
        dataBase.getPlayLists().set(dataBase.getPlayLists().indexOf(currentPlayList), newPlayList);
        playListClicked(currentPlayList.getTitle());
    }

    private String[] getNewOrderNames(DefaultListModel<String> myListModel) {
        String[] newOrderNames;
        JList<String> myList = new JList<>(myListModel);

        ListDialog dialog = new ListDialog("Please select an item in the list: ", myList, myListModel);
        dialog.setOnOk(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.convert2SringArray();
            }
        });
        dialog.show();
        newOrderNames = dialog.convert2SringArray();
        return newOrderNames;
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
                getGUI().getMainPanel().addPanels(dataBase.getMusicsByAlbumTitle(id));
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
                networkManager.friendManager.broadCastActivity(music2, true);
                getGUI().setMusicData(music2.getTitle(), music2.getArtist(), music2.getAlbumImage());
                break;
            case NETWORK_PLAYLIST:
                Music music3 = networkManager.getLastPlayListReceived().getMusicById(id);
                networkManager.server.sendMusicRequest(music3, networkUsername);
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
            if (!dataBase.getPlayListByTitle(selectedPlayList).contains(dataBase.getMusicById(id)))
                dataBase.getPlayListByTitle(selectedPlayList).add(dataBase.getMusicById(id));
            else
                getGUI().showMessage("This song is already in selected playList");
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

    @Override
    public void buttonLyric(String id) {

        JFrame jFrame = new JFrame();
        String text = "";
        try {
            for (String string : LyricsGatherer.getSongLyrics(dataBase.getMusicById(id).getArtist(), dataBase.getMusicById(id).getTitle()))
                text += string;

            JTextPane jTextPane = new JTextPane();
            jTextPane.setText(text);
            StyledDocument doc = jTextPane.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, doc.getLength(), center, false);
//            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jFrame.add(jTextPane);
            jFrame.setVisible(true);

        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,
                    "Cant Get Lyric...");
            io.printStackTrace();
        }
    }

    // GUI
    @Override
    public void closingProgram() {
//        player.stopMusic();
        dataBase.saveDataBase();
        networkManager.stopNetwork();
    }

    @Override
    public void updatePosition(int position, int totalTime, int currentTime) {
        getGUI().getPlayerPanel().setSliderCurrentPosition(position, totalTime, currentTime);
    }

    @Override
    public void musicFinished() {

    }

    @Override
    public void friendPanelClicked(String id) {
        networkManager.server.sendSharedPlayListRequest(id);
    }

    @Override
    public String getUsername() {
        return dataBase.getUsername();
    }

    private class NetworkManager implements ServerListener, FriendManagerListener {
        private Server server;
        private FriendManager friendManager;
        private PlayList lastPlayListReceived;

        //for Testing #Test
        private String[] friendIps = {"192.168.43.92", "192.168.43.176"};

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
            return dataBase.getSharedPlayList();
        }

        //ServerListener methods
        @Override
        public void friendBecomeOnline(String username) {
            System.out.println(username + " Become Online");
        }

        @Override
        public void friendMusicStarted(String username, Music music) {
            getGUI().getNetworkPanel().addActivity(username, music);
        }

        @Override
        public void friendMusicEnded(String username, Music music) {

        }

        @Override
        public void sharedPlayListData(String username, PlayList playList) {
            System.out.println(networkUsername + " shared playlist size: " + playList.size());
            getGUI().getMainPanel().removeAll();
            getGUI().getMainPanel().addPanels(playList.getMusics());
            lastPlayListReceived = playList;
            networkUsername = username;
            getGUI().getMainPanel().setMainPanelState(MainPanelState.NETWORK_PLAYLIST);
        }

        @Override
        public void musicDownloaded(Music music) {
            player.updateMusic(music);
            music.updateLastPlayedTime();
            player.playMusic();
            getGUI().setMusicData(music.getTitle(), music.getArtist(), music.getAlbumImage());
            getGUI().getPlayerPanel().setToPauseToggleButton();

        }

        public PlayList getLastPlayListReceived() {
            return lastPlayListReceived;
        }
    }
}
