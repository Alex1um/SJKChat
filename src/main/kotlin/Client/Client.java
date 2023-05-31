package Client;

import Base.Events.ConnectionEvent;
import Base.Events.DisconnectionEvent;
import Base.Events.MessageEvent;
import Base.Messages.Command;
import Base.Messages.CommandMessage;
import Base.Messages.CommandMessageAnswer;
import Base.Messages.TextMessage;
import Server.Config;

import javax.swing.*;
import java.io.IOException;

public class Client {

    private String nickname = null;
    private SocketClient client = null;

    private ClientInterface clientInterface = new ClientInterface();
    private ConnectionDialog connectionDialog = new ConnectionDialog("");

    private JFrame frame = new JFrame("Chat");
    public Client() {
        clientInterface.createUIComponents();
        frame.setContentPane(clientInterface.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        clientInterface.sendButton.addActionListener((e) -> {
            if (!clientInterface.sendTextArea.getText().isEmpty()) {
                try {
                    client.send(new TextMessage(clientInterface.sendTextArea.getText()));
                    clientInterface.sendTextArea.setText("");
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        clientInterface.eventField.append("Error while sending message" + ex + "\n");
                    });
                }
            }
        });
        clientInterface.usersUpdateButton.addActionListener((e) -> {
            try {
                client.send(new CommandMessage(Command.GET_USERS));
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    clientInterface.eventField.append("Error while updating users" + ex + "\n");
                });
            }
        });
        clientInterface.eventField.setLineWrap(true);
        clientInterface.eventField.setWrapStyleWord(true);
        clientInterface.contentPanel.setLayout(new BoxLayout(clientInterface.contentPanel, BoxLayout.Y_AXIS));
        Timer pingTimer = new Timer(Config.INSTANCE.getPING_DELAY(), (e) -> {
            if (client != null) {
                try {
                    client.send(new CommandMessage(Command.PING));
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        clientInterface.eventField.append("Error while ping" + ex + "\n");
                    });
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
        while (true) {
            connectionDialog.setVisible(true);
            client = connectionDialog.getSocketClient();
            nickname = connectionDialog.getNickname();
            if (client == null || nickname == null) break;
            try {
                pingTimer.restart();
                start(client, nickname);
            } catch (Exception e) {
                pingTimer.stop();
                connectionDialog = new ConnectionDialog("Error " + e);
            }
        }
        frame.dispose();
    }

    public void start(SocketClient client, String nickname) throws IOException, ClassNotFoundException {
        SwingUtilities.invokeLater(clientInterface::clear);
        this.client = client;
        this.nickname = nickname;
        while (true) {
            Object obj = client.input();
            if (obj instanceof MessageEvent) {
                MessageEvent msg = (MessageEvent)obj;
                JLabel label = new JLabel("%s> %s".formatted(msg.getSubject(), msg.getTextMessage().getMessage()));
                SwingUtilities.invokeLater(() -> {
                    clientInterface.contentPanel.add(label);
                    clientInterface.contentPanel.revalidate();
                    clientInterface.contentPanel.repaint();
                });
            } else if (obj instanceof ConnectionEvent) {
                ConnectionEvent ce = (ConnectionEvent)obj;
                SwingUtilities.invokeLater(() -> {
                    clientInterface.eventField.append(ce.getMessage() + "\n");
                });
            } else if (obj instanceof DisconnectionEvent) {
                DisconnectionEvent de = (DisconnectionEvent)obj;
                SwingUtilities.invokeLater(() -> {
                    clientInterface.eventField.append(de.getMessage() + "\n");
                });
            } else if (obj instanceof CommandMessageAnswer) {
                CommandMessageAnswer cma = (CommandMessageAnswer) obj;
                SwingUtilities.invokeLater(() -> {
                    if (cma.getCommand().equals(Command.GET_USERS)) {
                        DefaultListModel model = (DefaultListModel) clientInterface.usersList.getModel();
                        model.clear();
                        for (String name : cma.getData().split(",")) {
                            model.addElement(name);
                        }
                    }
                });
            }
        }
    }

    public static void main(String[] argv) {
        new Client();
    }

}
