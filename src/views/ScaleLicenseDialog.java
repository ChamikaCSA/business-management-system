package views;

import controllers.ScaleLicenseService;
import models.Customer;
import models.ScaleLicense;
import utils.Generator;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ScaleLicenseDialog extends JDialog {
    private ScaleLicenseService scaleLicenseService;
    private Customer customer;

    private JComboBox<String> licenseTypeComboBox;
    private JSpinner expirationDateSpinner;
    private String scaleLicenseId;
    private String licenseType;
    private Date issueDate;
    private Date expirationDate;
    private String status;

    public ScaleLicenseDialog(JFrame parentFrame, String title, Customer customer, ScaleLicenseService scaleLicenseService) {
        super(parentFrame, title, true);
        this.customer = customer;
        this.scaleLicenseService = scaleLicenseService;

        initialize(parentFrame);
    }

    private void initialize(JFrame parentFrame) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel licenseTypeLabel = new JLabel("License Type:");

        licenseTypeComboBox = new JComboBox<>();
        licenseTypeComboBox.addItem("Basic");
        licenseTypeComboBox.addItem("Pro");
        licenseTypeComboBox.addItem("Enterprise");

        JLabel expirationDateLabel = new JLabel("Expiration Date:");

        SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        expirationDateSpinner = new JSpinner(spinnerDateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(expirationDateSpinner, "dd/MM/yyyy");
        expirationDateSpinner.setEditor(dateEditor);
        expirationDateSpinner.setValue(new Date(System.currentTimeMillis() + 31556952000L));

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(licenseTypeLabel, gbc);

        gbc.gridx++;
        add(licenseTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(expirationDateLabel, gbc);

        gbc.gridx++;
        add(expirationDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        saveButton.addActionListener(_ -> saveScaleLicense());
        cancelButton.addActionListener(_ -> dispose());

        pack();
        setLocationRelativeTo(parentFrame);
        setResizable(false);
    }

    private void saveScaleLicense() {
        licenseType = (String) licenseTypeComboBox.getSelectedItem();
        expirationDate = (Date) expirationDateSpinner.getValue();

        if (licenseType == null) {
            JOptionPane.showMessageDialog(this, "Please select a license type.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (expirationDate == null) {
            JOptionPane.showMessageDialog(this, "Please select an expiration date.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        scaleLicenseId = Generator.generateDatedId("SL", scaleLicenseService.getScaleLicenseCount() + 1);
        issueDate = new Date();
        status = "Active";

        ScaleLicense scaleLicense = new ScaleLicense(scaleLicenseId, licenseType, issueDate, expirationDate, customer, status);
        scaleLicenseService.insertScaleLicense(scaleLicense);

        dispose();
    }
}
