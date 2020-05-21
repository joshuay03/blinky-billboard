package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;

/**
 * A class to represent a "Create Billboards" page which is bound to CreateBillboards.form
 * @author Joshua Young
 */
public class CreateBillboards {
    protected JPanel createBillboardsPanel;
    protected JButton importButton;
    protected JButton exportButton;
    protected JLabel createBillboardsLabel;
    protected JPanel optionPanel;
    protected JPanel createPanel;
    protected JLabel messageLabel;
    protected JTextArea messageTextArea;
    protected JButton messageColourButton;
    protected JLabel pictureLabel;
    protected JFormattedTextField pictureFormattedTextField;
    protected JLabel informationLabel;
    protected JTextArea informationTextArea;
    protected JButton informationColourButton;
    protected JButton backgroundColourButton;
    protected JButton viewBillboardButton;
    private JButton backButton;

    protected Color backgroundColour;
    protected String messageText;
    protected Color messageColor;
    protected String informationText;
    protected Color informationColor;
    protected String pictureText;

    protected ColourChooser colourChooser = new ColourChooser();

    /**
     *
     * @param frame
     */
    public CreateBillboards(JFrame frame) {
        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new OptionMenu(frame).optionMenuPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        importButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File xmlFile = chooser.getSelectedFile();
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(xmlFile);
                        doc.getDocumentElement().normalize();

                        NodeList billboardNodes = doc.getElementsByTagName("billboard");
                        if (billboardNodes.getLength() > 0) {
                            if (billboardNodes.item(0).getAttributes().getLength() > 0) {
                                backgroundColour = Color.decode(billboardNodes.item(0).getAttributes().item(0).getTextContent());
                            }
                        }

                        NodeList messageNodes = doc.getElementsByTagName("message");
                        if (messageNodes.getLength() > 0) {
                            messageText = messageNodes.item(0).getTextContent();

                            if (messageNodes.item(0).getAttributes().getLength() > 0) {
                                messageColor = Color.decode(messageNodes.item(0).getAttributes().item(0).getTextContent());
                            }
                        }

                        NodeList informationNodes = doc.getElementsByTagName("information");
                        if (informationNodes.getLength() > 0) {
                            informationText = informationNodes.item(0).getTextContent();

                            if (informationNodes.item(0).getAttributes().getLength() > 0) {
                                informationColor = Color.decode(informationNodes.item(0).getAttributes().item(0).getTextContent());
                            }
                        }

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        messageTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                messageText = messageTextArea.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                messageText = messageTextArea.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                messageText = messageTextArea.getText();
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
                int res = JOptionPane.showOptionDialog(null, colourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    messageColor = colourChooser.getColor();
                }
            }
        });

        pictureFormattedTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                pictureText = pictureFormattedTextField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pictureText = pictureFormattedTextField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                pictureText = pictureFormattedTextField.getText();
            }
        });

        informationTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                informationText = informationTextArea.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                informationText = informationTextArea.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                informationText = informationTextArea.getText();
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
                int res = JOptionPane.showOptionDialog(null, colourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    informationColor = colourChooser.getColor();
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
                int res = JOptionPane.showOptionDialog(null, colourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    backgroundColour = colourChooser.getColor();
                }
            }
        });

        viewBillboardButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Billboard billboard = new Billboard(backgroundColour, messageColor, informationColor, messageText, informationText, new ImageIcon(pictureText), LocalDateTime.now(), 30, 5);
                frame.setContentPane(new RenderedBillboard(billboard, new Dimension(900, 500)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}