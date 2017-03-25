package tutorial;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
    JButton pauseButton = new JButton("Pause");
    JButton rewindButton = new JButton("Rewind");
    JButton forwardButton = new JButton("Forward");
    JButton fileSelect = new JButton("Select File");

    controlsPane.add(pauseButton);
    controlsPane.add(rewindButton);
    controlsPane.add(forwardButton);
    controlsPane.add(fileSelect);

    contentPane.add(controlsPane, BorderLayout.SOUTH);

    fileSelect.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      int returnValue = fileChooser.showOpenDialog(null);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        String file = fileChooser.getSelectedFile().getAbsolutePath();
        System.out.println(file);
        mediaPlayerComponent.getMediaPlayer().playMedia(file);
      }
    });

    pauseButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().pause());

    rewindButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(-5000));

    forwardButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(5000));

    frame.setContentPane(contentPane);
    frame.setVisible(true);
  }
}
