package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
import java.util.Base64;

/**
 * A class to represent a "Create Billboards" page which is bound to CreateBillboards.form
 */
public class CreateBillboards {
    protected JPanel createBillboardsPanel;
    protected JPanel titlePanel;
    protected JButton backButton;
    protected JLabel createBillboardsLabel;
    protected JPanel optionPanel;
    protected JButton importButton;
    protected JButton exportButton;
    protected JButton previewBillboardButton;
    protected JPanel createPanel;
    protected JLabel messageLabel;
    protected JTextArea messageTextArea;
    protected JButton messageColourButton;
    protected JButton pictureButton;
    protected JLabel pictureURLLabel;
    protected JFormattedTextField pictureURLFormattedTextField;
    protected JLabel informationLabel;
    protected JTextArea informationTextArea;
    protected JButton informationColourButton;
    protected JButton backgroundColourButton;
    protected JButton saveBillboardButton;
    private JTextArea nameTextArea;
    private JLabel nameLabel;

    protected ColourChooser colourChooser = new ColourChooser();
    protected Billboard billboard;
    protected JFrame previewFrame;

    /**
     *
     * @param frame
     */
    public CreateBillboards(JFrame frame, ClientConnector connector) {
        billboard = new Billboard();

        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new OptionMenu(frame, connector).optionMenuPanel);
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
                    File xmlFile = chooser.getSelectedFile();
                    billboard = Billboard.getBillboardFromXML(xmlFile);

                    messageTextArea.setText(billboard.getMessage());
                    informationTextArea.setText(billboard.getInformation());

                    if(billboard.getImageURL() != null) pictureURLFormattedTextField.setText(billboard.getImageURL().toString());
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Document XMLRep = billboard.getXMLRepresentation();

                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File fileLocation = chooser.getSelectedFile();

                    try {
                        // create the xml file
                        //transform the DOM Object to an XML File
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource domSource = new DOMSource(XMLRep);
                        StreamResult streamResult = new StreamResult(fileLocation);

                        transformer.transform(domSource, streamResult);
                    } catch (TransformerConfigurationException ex) {
                        ex.printStackTrace();
                    } catch (TransformerException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        saveBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                billboard.setBillboardName("test");
                //create request
                Request createBillboard = Request.createBillboardReq(billboard, connector.session);

                // Send request to server
                Response response;

                try {
                    response = createBillboard.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(null, "Cannot save billboard on server.");
                    return;
                }

                // check status of response
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, "Cannot save billboard on server. Error: " + errorMsg);
                }

                if (status) {
                    JOptionPane.showMessageDialog(null, "Billboard successfully created and saved on server.");
                    frame.setContentPane(new OptionMenu(frame, connector).optionMenuPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
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
                int res = JOptionPane.showOptionDialog(null, colourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    billboard.setMessageColour(colourChooser.getColor());
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
                        billboard.setImageData(Base64.getDecoder().decode(encodeFileToBase64Binary(pictureFile).getBytes()).toString());
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
                int res = JOptionPane.showOptionDialog(null, colourChooser, "Choose colour", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (res == 0) {
                    billboard.setInformationColour(colourChooser.getColor());
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
                    billboard.setBackgroundColour(colourChooser.getColor());
                }
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