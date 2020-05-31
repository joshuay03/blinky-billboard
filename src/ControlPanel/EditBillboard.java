package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class EditBillboard {
    protected JPanel editBillboardPanel;
    protected JPanel titlePanel;
    protected JButton backButton;
    protected JLabel createBillboardsLabel;
    protected JPanel optionPanel;
    protected JButton previewBillboardButton;
    protected JPanel createPanel;
    protected JLabel nameLabel;
    protected JTextArea nameTextArea;
    protected JLabel messageLabel;
    protected JTextArea messageTextArea;
    protected JPanel messageColourPanel;
    protected JButton messageColourButton;
    protected JButton pictureButton;
    protected JLabel pictureURLLabel;
    protected JFormattedTextField pictureURLFormattedTextField;
    protected JLabel informationLabel;
    protected JTextArea informationTextArea;
    protected JPanel informationColourPanel;
    protected JButton informationColourButton;
    protected JPanel backgroundColourPanel;
    protected JButton backgroundColourButton;
    protected JButton saveBillboardButton;

    protected ColourChooser messageColourChooser = new ColourChooser(Color.BLACK);
    protected ColourChooser informationColourChooser = new ColourChooser(Color.BLACK);
    protected ColourChooser backgroundColourChooser = new ColourChooser(Color.WHITE);
    protected JFrame previewFrame;

    public EditBillboard(JFrame frame, ClientConnector connector, List<Billboard> billboardList, final Billboard billboard) {
        if (billboard.getBillboardName() != null) {
            nameTextArea.setText(billboard.getBillboardName());
        }
        if (billboard.getMessage() != null) {
            messageTextArea.setText(billboard.getMessage());
        }
        if (billboard.getMessageColour() != null) {
            messageColourPanel.setBackground(billboard.getMessageColour());
        }
        if (billboard.getImageURL() != null) {
            pictureURLFormattedTextField.setText(billboard.getImageURL().toString());
        }
        if (billboard.getInformation() != null) {
            informationTextArea.setText(billboard.getInformation());
        }
        if (billboard.getInformationColour() != null) {
            informationColourPanel.setBackground(billboard.getInformationColour());
        }
        if (billboard.getBackgroundColour() != null) {
            backgroundColourPanel.setBackground(billboard.getBackgroundColour());
        }

        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ListBillboards(frame, connector, billboardList).listBillboardsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        previewBillboardButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension renderDimension = new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2,
                        (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);
                RenderedBillboard renderedBillboard = new RenderedBillboard(billboard, renderDimension);

                previewFrame = new JFrame();
                previewFrame.setSize(renderDimension);
                previewFrame.setContentPane(renderedBillboard);
                previewFrame.setVisible(true);
            }
        });

        messageTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setMessage(messageTextArea.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setMessage(messageTextArea.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setMessage(messageTextArea.getText());
            }
        });

        messageColourButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showOptionDialog(frame, messageColourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    messageColourPanel.setBackground(messageColourChooser.getColor());
                    billboard.setMessageColour(messageColourChooser.getColor());
                }
            }
        });

        pictureButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Pictures", "bmp", "jpeg", "png");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File pictureFile = chooser.getSelectedFile();
                    try {
                        billboard.setImageData(Arrays.toString(Base64.getDecoder().decode(encodeFileToBase64Binary(pictureFile).getBytes())));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        pictureURLFormattedTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    billboard.setImageURL(new URL(pictureURLFormattedTextField.getText()));
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    billboard.setImageURL(new URL(pictureURLFormattedTextField.getText()));
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    billboard.setImageURL(new URL(pictureURLFormattedTextField.getText()));
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }
        });

        informationTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                billboard.setInformation(informationTextArea.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                billboard.setInformation(informationTextArea.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                billboard.setInformation(informationTextArea.getText());
            }
        });

        informationColourButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showOptionDialog(frame, informationColourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    informationColourPanel.setBackground(informationColourChooser.getColor());
                    billboard.setInformationColour(informationColourChooser.getColor());
                }
            }
        });

        backgroundColourButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showOptionDialog(frame, backgroundColourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    backgroundColourPanel.setBackground(backgroundColourChooser.getColor());
                    billboard.setBackgroundColour(backgroundColourChooser.getColor());
                }
            }
        });

        saveBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (nameTextArea.getText() == null){
                    JOptionPane.showMessageDialog(frame, "Cannot create billboard without a name.");
                    return;
                } else {
                    billboard.setBillboardName(nameTextArea.getText());
                }
                //create request
                Request createBillboard = Request.createBillboardReq(billboard, connector.session);

                // Send request to server
                Response response;

                try {
                    response = createBillboard.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(frame, "Cannot save billboard on server.");
                    return;
                }

                // check status of response
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(frame, "Cannot save billboard on server. Error: " + errorMsg);
                }

                if (status) {
                    JOptionPane.showMessageDialog(frame, response.getData());
                    frame.setContentPane(new OptionMenu(frame, connector).optionMenuPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            }
        });
    }

    private static String encodeFileToBase64Binary(File file) throws IOException {
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.getEncoder().encode(bytes);
        String encodedString = new String(encoded, StandardCharsets.US_ASCII);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            JOptionPane.showMessageDialog(null, "File is too large", "Error", JOptionPane.DEFAULT_OPTION);
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
}