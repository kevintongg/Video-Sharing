package tutorial;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class Tutorial {

  private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

  public static void main(final String[] args) {
    new NativeDiscovery().discover();
    SwingUtilities.invokeLater(Tutorial::new);
  }

  private Tutorial() {
    JFrame frame = new JFrame("Video Sync");
    frame.setBounds(100, 100, 600, 400);
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
    JButton pauseButton = new JButton("Pause");
    controlsPane.add(pauseButton);
    JButton rewindButton = new JButton("Rewind");
    controlsPane.add(rewindButton);
    JButton skipButton = new JButton("Skip");
    controlsPane.add(skipButton);
    contentPane.add(controlsPane, BorderLayout.SOUTH);

    pauseButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().pause());

    rewindButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(-10000));

    skipButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(10000));

    frame.setContentPane(contentPane);
    frame.setVisible(true);
    mediaPlayerComponent.getMediaPlayer()
        .playMedia("/Users/kevin/Classes/CS3337/testing/VideoSync/src/tutorial/Yiruma.ogg");
  }
}
