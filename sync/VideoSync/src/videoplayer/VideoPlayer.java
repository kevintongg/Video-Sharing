package videoplayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;

public class VideoPlayer {

  private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
  private JSlider positionSlider;
  private JSlider volumeSlider;

  public static void main(final String[] args) {
    new NativeDiscovery().discover();
    SwingUtilities.invokeLater(VideoPlayer::new);

  }

  private JMenuBar createMenuBar() {

    registerListeners();
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("File");
    JMenuItem menuItem = new JMenuItem("Choose File");

    menu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(menu);

    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));

    menuItem.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      int returnValue = fileChooser.showOpenDialog(null);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        String file = fileChooser.getSelectedFile().getAbsolutePath();
        System.out.println(file);
        mediaPlayerComponent.getMediaPlayer().playMedia(file);
      }
    });

    menu.add(menuItem);

    return menuBar;
  }

  private VideoPlayer() {
    JFrame frame = new JFrame("Video Sync");
    frame.setBounds(100, 100, 1066, 720);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        mediaPlayerComponent.release();
        System.exit(0);
      }
    });

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    JLabel timeLabel = new JLabel("hh:mm:ss");
    mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
    mediaPlayerComponent.getMediaPlayer()
        .setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(frame));

    JPanel controlsPane = new JPanel();
    JButton playbackButton = new JButton("Play/Pause");
    JButton rewindButton = new JButton("Rewind"); // 5 seconds backwards
    JButton forwardButton = new JButton("Forward"); // 5 seconds forward
    JButton fullScreen = new JButton("Fullscreen");

    volumeSlider = new JSlider();
    volumeSlider.setOrientation(JSlider.HORIZONTAL);
    volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
    volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
    volumeSlider.setPreferredSize(new Dimension(100, 40));
    volumeSlider.setToolTipText("Change volume");

    positionSlider = new JSlider();
    positionSlider.setMinimum(0);
    positionSlider.setMaximum(1000);
    positionSlider.setValue(0);
    positionSlider.setToolTipText("Position");

    JPanel positionPanel = new JPanel();
    positionPanel.setLayout(new GridLayout(1, 1));
    positionPanel.add(positionSlider);

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout(8, 0));
    topPanel.setLayout(new BorderLayout(8, 0));

    topPanel.add(timeLabel, BorderLayout.WEST);
    topPanel.add(positionPanel, BorderLayout.CENTER);

    controlsPane.add(playbackButton);
    controlsPane.add(rewindButton);
    controlsPane.add(forwardButton);
    controlsPane.add(fullScreen);
    controlsPane.add(volumeSlider);

    contentPane.add(controlsPane, BorderLayout.SOUTH);
    contentPane.add(timeLabel, BorderLayout.NORTH);
    contentPane.add(positionPanel, BorderLayout.NORTH);

    playbackButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().pause());

    rewindButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(-5000));

    fullScreen.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().toggleFullScreen());
    frame.setJMenuBar(createMenuBar());
    frame.setContentPane(contentPane);
    frame.setVisible(true);

  }

  private void setSliderBasedPosition() {
    if (!mediaPlayerComponent.getMediaPlayer().isSeekable()) {
      return;
    }
    float positionValue = positionSlider.getValue() / 1000.0f;
    // Avoid end of file freeze-up
    if (positionValue > 0.99f) {
      positionValue = 0.99f;
    }
    mediaPlayerComponent.getMediaPlayer().setPosition(positionValue);
  }

  private void updatePosition(int value) {
    // positionProgressBar.setValue(value);
    positionSlider.setValue(value);
  }

  private void updateUI() {
    int position = (int) (mediaPlayerComponent.getMediaPlayer().getPosition() * 1000.0f);
    updatePosition(position);
  }

  private void registerListeners() {
    mediaPlayerComponent.getMediaPlayer()
        .addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
          @Override
          public void playing(MediaPlayer mediaPlayer) {
            // updateVolume(mediaPlayer.getVolume());
          }
        });

    positionSlider.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        setSliderBasedPosition();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        setSliderBasedPosition();
        updateUI();
      }
    });

    volumeSlider.addChangeListener(e -> {
      JSlider source = (JSlider) e.getSource();
      mediaPlayerComponent.getMediaPlayer().setVolume(source.getValue());
    });
  }
}