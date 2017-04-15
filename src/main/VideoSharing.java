package main;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class VideoSharing {

  private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

  public static void main(final String[] args) {
    new NativeDiscovery().discover();
    SwingUtilities.invokeLater(VideoSharing::new);
  }

  private JMenuBar createMenuBar() {

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

  private VideoSharing() {
    JFrame frame = new JFrame("Video Sync");
    frame.setBounds(100, 100, 640, 360);
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

    mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
    contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);

    JPanel controlsPane = new JPanel();
    JButton playback = new JButton("Play/Pause");
    JButton rewindButton = new JButton("Rewind");
    JButton forward = new JButton("Forward");
    JButton fastForward = new JButton("Fast Forward");
    JButton fullScreen = new JButton("Fullscreen");

    controlsPane.add(playback);
    controlsPane.add(rewindButton);
    controlsPane.add(forward);
    controlsPane.add(fastForward);
    controlsPane.add(fullScreen);

    contentPane.add(controlsPane, BorderLayout.SOUTH);

    playback.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().pause());

    rewindButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(-5000));

    forward.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(5000));

    fastForward.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().setRate(2500));

    fullScreen.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().toggleFullScreen());
    
    frame.setJMenuBar(createMenuBar());
    frame.setContentPane(contentPane);
    frame.setVisible(true);
  }
}
