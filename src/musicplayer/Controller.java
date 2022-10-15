package musicplayer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

public class Controller extends javax.swing.JFrame {

        private javax.swing.JLabel artist_name;
        private javax.swing.JLabel image;
        private javax.swing.JPanel image_container;
        private javax.swing.JPanel image_frame;
        private javax.swing.JLabel timeStart;
        private javax.swing.JLabel timeEnd;
        private javax.swing.JLabel volumn;
        private javax.swing.JList<String> musicListSection;
        private javax.swing.JScrollPane containerMusicList;
        private javax.swing.JLabel music_name;
        private javax.swing.JButton next;
        private javax.swing.JButton play;
        private javax.swing.JButton previous;
        private javax.swing.JToggleButton replay;
        private javax.swing.JButton shuffle;
        private javax.swing.JProgressBar progressBar;
        private javax.swing.JSlider slider_volumn;

        private File directory;
        private File[] files;

        private Media media;
        private MediaPlayer mediaPlayer;

        private int musicNumber;
        private int progress = 0;

        private Timer timer;
        private TimerTask task;

        private String title, artist;
        long total_lenght;
        long pause;

        private boolean running, loop;

        private List<File> musicList = new ArrayList<File>();

        Color color = new Color(204, 204, 255);

        public Controller() {

                initComponents();
                getContentPane().setBackground(color);

                musicList = new ArrayList<File>();
                directory = new File("src//music");
                files = directory.listFiles();

                if (files != null) {
                        for (File file : files) {
                                musicList.add(file);
                        }
                }

                List<String> musicName = new ArrayList<String>();
                for (File file : files) {
                        if (file.isFile()) {
                                musicName.add(file.getName());
                        }
                }

                final DefaultListModel<String> listModel1 = new DefaultListModel();

                for (String i : musicName) {
                        listModel1.addElement(i);
                }
                musicListSection.setModel(listModel1);

                progressBar.setValue(progress);

                media = new Media(musicList.get(musicNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                musicDetails(musicNumber);

        }

        private void initComponents() {

                music_name = new javax.swing.JLabel();
                artist_name = new javax.swing.JLabel();
                containerMusicList = new javax.swing.JScrollPane();
                musicListSection = new javax.swing.JList<>();
                progressBar = new javax.swing.JProgressBar();
                slider_volumn = new javax.swing.JSlider();
                timeStart = new javax.swing.JLabel();
                timeEnd = new javax.swing.JLabel();
                volumn = new javax.swing.JLabel();
                replay = new javax.swing.JToggleButton();
                previous = new javax.swing.JButton();
                image_container = new javax.swing.JPanel();
                image_frame = new javax.swing.JPanel();
                image = new javax.swing.JLabel();
                next = new javax.swing.JButton();
                // add = new javax.swing.JButton();
                play = new javax.swing.JButton();
                shuffle = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setTitle("MusicPlayer");
                setBackground(new java.awt.Color(204, 204, 255));
                setResizable(false);

                music_name.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
                music_name.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                music_name.setText("MusicName");
                music_name.setToolTipText("");

                artist_name.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
                artist_name.setText("Artist Name");

                musicListSection.setBackground(new java.awt.Color(255, 204, 204));
                musicListSection.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                musicListSection.setForeground(new java.awt.Color(0, 0, 0));

                musicListSection.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                if (evt.getClickCount() == 2) {
                                        musicNumber = musicListSection.locationToIndex(evt.getPoint());
                                        try {

                                                if (mediaPlayer != null) {
                                                        stopMedia(mediaPlayer);

                                                        if (running) {
                                                                cancelTimer();
                                                        }

                                                        media = new Media(
                                                                        musicList.get(musicNumber).toURI().toString());
                                                        mediaPlayer = new MediaPlayer(media);
                                                        playMouseClicked();
                                                }

                                        } catch (Exception e) {
                                                alertError(
                                                                "Please double click to select in list of music !");
                                        }

                                }
                        }
                });

                containerMusicList.setViewportView(musicListSection);

                progressBar.setAutoscrolls(true);
                progressBar.setFocusable(true);
                progressBar.setName(""); // NOI18N
                progressBar.setStringPainted(true);
                progressBar.setBackground(color);

                progressBar.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                try {

                                        double dx = evt.getX();
                                        double dwidth = progressBar.getWidth();
                                        double progression = (dx / dwidth);
                                        double milliseconds = (progression * mediaPlayer.getTotalDuration().toMillis());
                                        Duration duration = new Duration(milliseconds);

                                        current = mediaPlayer.getCurrentTime().toSeconds();
                                        end = media.getDuration().toSeconds();

                                        var progress = (current / end) * 100;

                                        progressBar.setValue((int) progress);
                                        mediaPlayer.seek(duration);
                                        mediaPlayer.play();

                                } catch (Exception e7) {
                                        alertError(
                                                        "Please double click to select in list of music !");

                                }
                        }
                });

                timeStart.setText("00:00");
                timeEnd.setText("00:00");

                volumn.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
                volumn.setText("Volumn");

                slider_volumn.addChangeListener(new javax.swing.event.ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                                int value = slider_volumn.getValue();
                                mediaPlayer.setVolume(value);
                        }
                });

                // {
                // // public void mouseClicked(java.awt.event.MouseEvent evt) {
                // // changeSpeed(evt);
                // // }
                // });

                replay.setBackground(new java.awt.Color(255, 204, 204));
                replay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/replay-icon.png"))); // NOI18N
                replay.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                replay.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                replayMouseClicked(evt);
                        }
                });

                previous.setBackground(new java.awt.Color(255, 204, 204));
                previous.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/rewind-icon.png"))); // NOI18N
                previous.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                previous.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                previousMouseClicked(evt);
                        }
                });

                image_container.setBackground(new java.awt.Color(255, 204, 204));
                image_container.setBorder(
                                javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/11008_130914100858Lv.jpg")));

                javax.swing.GroupLayout image_frameLayout = new javax.swing.GroupLayout(image_frame);
                image_frame.setLayout(image_frameLayout);
                image_frameLayout.setHorizontalGroup(
                                image_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(image));
                image_frameLayout.setVerticalGroup(
                                image_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(image, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

                javax.swing.GroupLayout image_containerLayout = new javax.swing.GroupLayout(image_container);
                image_container.setLayout(image_containerLayout);
                image_containerLayout.setHorizontalGroup(
                                image_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(image_containerLayout.createSequentialGroup()
                                                                .addGap(31, 31, 31)
                                                                .addComponent(image_frame,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(31, Short.MAX_VALUE)));
                image_containerLayout.setVerticalGroup(
                                image_containerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                image_containerLayout.createSequentialGroup()
                                                                                .addContainerGap()
                                                                                .addComponent(image_frame,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addContainerGap()));

                next.setBackground(new java.awt.Color(255, 204, 204));
                next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/forward-icon.png"))); // NOI18N
                next.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                next.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                nextMouseClicked();
                        }
                });

                play.setBackground(new java.awt.Color(255, 204, 204));
                play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/play-icon.png"))); // NOI18N
                play.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                play.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                playMouseClicked();
                        }

                });

                shuffle.setBackground(new java.awt.Color(255, 204, 204));
                shuffle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/shuffle-icon.png"))); // NOI18N
                shuffle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                shuffle.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                                shuffleMouseClicked(evt);
                        }

                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                                                                .createSequentialGroup()
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(image_container,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(41, 41, 41))
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(artist_name,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                329,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(layout.createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                .addContainerGap()
                                                                                                                .addComponent(music_name,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                329,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                layout.createSequentialGroup()
                                                                                                                                .addGap(14, 14, 14)
                                                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                                                .addComponent(timeStart)
                                                                                                                                                                .addGap(271, 271,
                                                                                                                                                                                271)
                                                                                                                                                                .addComponent(timeEnd))
                                                                                                                                                .addComponent(progressBar,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                329,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                                                                .createSequentialGroup()
                                                                .addGap(17, 17, 17)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                // .addComponent(add,
                                                                                // javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                // 73,
                                                                                // javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(containerMusicList)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                .addComponent(volumn,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                47,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                                                .addComponent(slider_volumn,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                92,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                .addComponent(shuffle,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                55,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                .addComponent(previous,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                61,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                                                .addComponent(play,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                61,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addComponent(next,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                61,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(replay,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                55,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(13, 13, 13)));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(18, 18, 18)
                                                                .addComponent(image_container,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(31, 31, 31)
                                                                .addComponent(music_name)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(artist_name)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(progressBar,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(timeStart)
                                                                                .addComponent(timeEnd))
                                                                .addGap(17, 17, 17)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                false)
                                                                                                                .addComponent(volumn,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE)
                                                                                                                .addComponent(slider_volumn,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE))
                                                                                                .addGap(6, 6, Short.MAX_VALUE)
                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addComponent(play,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                58,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                                                .addComponent(previous,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                48,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addComponent(next,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                48,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addComponent(replay,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                48,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addComponent(shuffle,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                48,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)))
                                                                // .addComponent(add)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(containerMusicList,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                235,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap()));

                pack();
        }// </editor-fold>

        /*
         * 
         * 
         * 
         * 
         * 
         * 
         * 
         * 
         * 
         * State
         * 
         * 
         * 
         * 
         */

        Boolean isPlaying = true;
        Boolean isFirstClicked = true;

        public void playMouseClicked() {
                try {
                        if (mediaPlayer != null) {
                                if (running == false) {
                                        play.setBackground(new java.awt.Color(255, 204, 204));
                                        play.setIcon(new javax.swing.ImageIcon(
                                                        getClass().getResource("/icon/pause-icon.png"))); // NOI18N
                                        play.setBorder(
                                                        javax.swing.BorderFactory.createBevelBorder(
                                                                        javax.swing.border.BevelBorder.RAISED));
                                        mediaPlayer.setVolume(slider_volumn.getValue() * 0.01);

                                        beginTimer();
                                        musicDetails(musicNumber);

                                        mediaPlayer.play();

                                        mediaPlayer.setOnEndOfMedia(new Runnable() {
                                                public void run() {
                                                        if (loop == true) {
                                                                progressBar.setValue(0);
                                                                mediaPlayer.seek(Duration.seconds(0));
                                                                mediaPlayer.play();
                                                        } else {
                                                                nextMouseClicked();
                                                        }
                                                }
                                        });

                                } else {
                                        play.setBackground(new java.awt.Color(255, 204, 204));
                                        play.setIcon(new javax.swing.ImageIcon(
                                                        getClass().getResource("/icon/play-icon.png"))); // NOI18N
                                        play.setBorder(
                                                        javax.swing.BorderFactory.createBevelBorder(
                                                                        javax.swing.border.BevelBorder.RAISED));

                                        cancelTimer();
                                        mediaPlayer.pause();
                                }

                        } else {
                                alertError("No Music in Queue Please double click to select in list of music !");
                        }

                } catch (Exception e) {
                        alertError("ERROR Please restart app");
                }
        }

        public void stopMedia(MediaPlayer mediaPlayer) {
                progressBar.setValue(0);
                mediaPlayer.seek(Duration.seconds(0));
                mediaPlayer.stop();

        }

        private void nextMouseClicked() {

                if (mediaPlayer != null) {
                        if (musicNumber < musicList.size() - 1) {
                                isPlaying = false;
                                isFirstClicked = true;

                                musicNumber++;
                                stopMedia(mediaPlayer);

                                if (running) {
                                        cancelTimer();
                                }

                                System.out.println("this is music number : " + musicNumber);

                                media = new Media(musicList.get(musicNumber).toURI().toString());
                                mediaPlayer = new MediaPlayer(media);

                                System.out.println("in if");

                                playMouseClicked();

                        } else {
                                alertError("End list of music");

                        }

                } else {
                        alertError("ERROR Please restart app");
                }

        }

        private void previousMouseClicked(java.awt.event.MouseEvent evt) {

                if (mediaPlayer != null) {
                        if (musicNumber != 0) {
                                isPlaying = false;
                                isFirstClicked = true;

                                musicNumber--;
                                mediaPlayer.stop();
                                if (running) {
                                        cancelTimer();
                                }

                                media = new Media(musicList.get(musicNumber).toURI().toString());
                                mediaPlayer = new MediaPlayer(media);

                                // musicDetails(musicNumber);
                                // setSelection(musicNumber);

                                // musicLabel.setText(title);
                                // artistLabel.setText(artist);

                                playMouseClicked();

                        } else {
                                alertError("This is first music");

                        }
                } else {
                        alertError("ERROR Please restart app");
                }

        }

        private void shuffleMouseClicked(java.awt.event.MouseEvent evt) {

                int rnd = new Random().nextInt(musicList.size());
                musicNumber = rnd;

                if (mediaPlayer != null) {
                        isPlaying = false;
                        isFirstClicked = true;
                        mediaPlayer.stop();
                        if (running) {
                                cancelTimer();
                        }

                        // cancelTimer();

                        media = new Media(musicList.get(musicNumber).toURI().toString());
                        mediaPlayer = new MediaPlayer(media);

                        System.out.println("is a music number : " + musicNumber);
                        playMouseClicked();

                } else {
                        alertError("No Music in Queue Please double click to select in list of music !");
                }

        }

        private void replayMouseClicked(java.awt.event.MouseEvent evt) {

                if (loop == false) {
                        loop = true;

                        System.out.print("----------------is loop song---------------------");

                } else {
                        loop = false;
                        System.out.print("----------------is running song---------------------");
                }

        }

        double current;
        double end;

        public void beginTimer() {
                timer = new Timer();
                task = new TimerTask() {

                        public void run() {
                                running = true;
                                current = mediaPlayer.getCurrentTime().toSeconds();
                                end = media.getDuration().toSeconds();

                                var progress = (current / end) * 100;

                                progressBar.setValue((int) progress);

                                if (current / end == 1) {
                                        cancelTimer();
                                }
                        }

                };
                mediaPlayer.currentTimeProperty().addListener(new ChangeListener<>() {

                        @Override
                        public void changed(ObservableValue<? extends Duration> observable, Duration oldTime,
                                        Duration newTime) {
                                current = mediaPlayer.getCurrentTime().toMillis();
                                end = media.getDuration().toMillis();

                                timeStart.setText("" + milliSecondsToTimer(current));
                                timeEnd.setText("" + milliSecondsToTimer(end));

                        }

                });

                timer.scheduleAtFixedRate(task, 0, 1000);
        }

        public String milliSecondsToTimer(Double milliseconds) {
                String finalTimerString = "";
                String secondsString = "";
                String minutesString = "";

                // Convert total duration into time
                int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
                int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

                // Prepending 0 to seconds if it is one digit
                if (seconds < 10) {
                        secondsString = "0" + seconds;
                } else {
                        secondsString = "" + seconds;
                }

                // Prepending 0 to seconds if it is one digit
                if (minutes < 10) {
                        minutesString = "0" + minutes;
                } else {
                        minutesString = "" + minutes;
                }

                finalTimerString = finalTimerString + minutesString + ":" + secondsString;

                // return timer string
                return finalTimerString;
        }

        public void musicDetails(int musicNumber) {
                Mp3File mp3file;
                ID3v2 id3v2Tag;

                try {
                        mp3file = new Mp3File(files[musicNumber]);
                        if (mp3file.hasId3v2Tag()) {
                                id3v2Tag = mp3file.getId3v2Tag();

                                title = id3v2Tag.getTitle();
                                artist = id3v2Tag.getArtist();

                                music_name.setText(title);
                                artist_name.setText(artist);

                                byte[] albumImageData = id3v2Tag.getAlbumImage();

                                if (albumImageData != null) {

                                        BufferedImage img = scaleImage(200, 200, albumImageData);
                                        image.setIcon(new javax.swing.ImageIcon(
                                                        img));

                                } else {

                                        image.setIcon(new javax.swing.ImageIcon(
                                                        getClass().getResource("/icon/11008_130914100858Lv.jpg"))); //
                                }
                        }

                } catch (UnsupportedTagException | InvalidDataException | IOException ex) {
                        ex.printStackTrace();
                }
        }

        public BufferedImage scaleImage(int WIDTH, int HEIGHT, byte[] albumImageData) {
                BufferedImage bi = null;
                try {
                        ImageIcon ii = new ImageIcon(albumImageData);// path to image
                        bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = (Graphics2D) bi.createGraphics();
                        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,
                                        RenderingHints.VALUE_RENDER_QUALITY));
                        g2d.drawImage(ii.getImage(), 0, 0, WIDTH, HEIGHT, null);
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
                return bi;
        }

        public void cancelTimer() {
                running = false;
                timer.cancel();
        }

        public void alertError(String content) {
                JOptionPane.showMessageDialog(null, content);
        }

}
