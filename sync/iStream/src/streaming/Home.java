package streaming;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Home extends javax.swing.JFrame {

  private Home() {
    initComponents();
    setLocationRelativeTo(this);
  }

  @SuppressWarnings("unchecked")
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 806, Short.MAX_VALUE));
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel1Layout.createSequentialGroup().addContainerGap()
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

    jMenu1.setText("Connect");

    jMenuItem1.setAccelerator(javax.swing.KeyStroke
        .getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItem1.setText("Ask for Screen/ Stop Screen Stream");
    jMenuItem1.addActionListener(evt -> jMenuItem1ActionPerformed());
    jMenu1.add(jMenuItem1);

    jMenuItem2.setAccelerator(javax.swing.KeyStroke
        .getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    jMenuItem2.setText("Start/ Stop Server");
    jMenuItem2.addActionListener(evt -> jMenuItem2ActionPerformed());
    jMenu1.add(jMenuItem2);

    jMenuBar1.add(jMenu1);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private boolean isStartStream = false;

  private void jMenuItem1ActionPerformed() {

    if (isStart) {
      JOptionPane
          .showMessageDialog(this,
              "If you want to ask to screen share, you must first stop your server.");
      return;
    }
    if (!isStartStream) {
      String ip = JOptionPane.showInputDialog("Enter an IP address to connect to");
      int port = Integer.parseInt(JOptionPane.showInputDialog("Enter a port to connect to"));
      if (ip != null && !ip.equals("")) {
        isStartStream = true;
        new Thread(() -> {
          try {
            while (isStartStream) {
              Socket socket = new Socket(ip, port);
              BufferedImage img = ImageIO.read(socket.getInputStream());
              jPanel2.getGraphics()
                  .drawImage(img, 0, 0, jPanel2.getWidth(), jPanel2.getHeight(), null);
              socket.close();
              try {
                Thread.sleep(10);
              } catch (Exception ignored) {
              }
            }
          } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
          }
          isStartStream = false;
        }).start();
      } else {
        JOptionPane.showMessageDialog(null, "Please enter a valid ip address.");
      }
    } else {
      isStartStream = false;
    }
  }

  private boolean isStart = false;

  private void jMenuItem2ActionPerformed() {
    if (isStartStream) {
      JOptionPane.showMessageDialog(this,
          "If you want to start a server, you must stop watching the other screen.");
      return;
    }
    if (!isStart) {
      int socketNumber = Integer
          .parseInt(JOptionPane.showInputDialog("Enter a valid port to use."));
      isStart = true;
      new Thread(() -> {
        try {
          Robot robot = new Robot();
          Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
          while (isStart) {
            ServerSocket serverSocket = new ServerSocket(socketNumber);
            Socket socket = serverSocket.accept();
            BufferedImage img = robot
                .createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(img, "png", byteArrayOutputStream);
            socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
            serverSocket.close();
            try {
              Thread.sleep(10);
            } catch (Exception ignored) {
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null, e);
        }
        isStart = false;
      }).start();
      JOptionPane.showMessageDialog(this, "Server Started.");
    } else {
      isStart = false;
    }
  }// GEN-LAST:event_jMenuItem2ActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {

    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
          .getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | javax.swing.UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ex) {
      java.util.logging.Logger.getLogger(Home.class.getName())
          .log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> new Home().setVisible(true));
  }

  // Variables declaration - do not modify
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  // End of variables declaration
}
