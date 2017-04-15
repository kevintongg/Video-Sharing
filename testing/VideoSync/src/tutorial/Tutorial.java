package tutorial;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.Equalizer;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class Tutorial {

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JSlider volumeSlider;

	public static void main(final String[] args) {
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(Tutorial::new);
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
		JButton playButton = new JButton("Play");
		JButton rewindButton = new JButton("Rewind"); // 5 seconds backwards
		JButton forwardButton = new JButton("Forward"); // 5 seconds forward

		volumeSlider = new JSlider();
		volumeSlider.setOrientation(JSlider.HORIZONTAL);
		volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
		volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
		volumeSlider.setPreferredSize(new Dimension(100, 40));
		volumeSlider.setToolTipText("Change volume");

		controlsPane.add(playButton);
		controlsPane.add(pauseButton);
		controlsPane.add(rewindButton);
		controlsPane.add(forwardButton);
		controlsPane.add(volumeSlider);

		contentPane.add(controlsPane, BorderLayout.SOUTH);

		pauseButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().pause());

		playButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().play());

		rewindButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(-5000));

		forwardButton.addActionListener(e -> mediaPlayerComponent.getMediaPlayer().skip(5000));

		frame.setJMenuBar(createMenuBar());
		frame.setContentPane(contentPane);
		frame.setVisible(true);
	}

	private void registerListeners() {
		mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void playing(MediaPlayer mediaPlayer) {
				//updateVolume(mediaPlayer.getVolume());
			}
		});
		
	    volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                mediaPlayerComponent.updateVolume(source.getValue());
            }
        });
	}

	private void updateVolume(int value) {
		volumeSlider.setValue(value);
	}

}