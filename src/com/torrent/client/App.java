package com.torrent.client;

import com.torrent.client.listener.HostPortFieldListener;

import javax.swing.*;

public class App {
    private JPanel panelMain;
    private JTabbedPane tabbedPane;
    private JPanel entrancePanel;
    private JPanel serverBrowsePanel;
    private JLabel hostPortLabel;
    private JTextField hostPortField;
    private JList<FileItem> fileNamesList;

    public App() {
        hostPortField.addKeyListener(new HostPortFieldListener(
                panelMain,
                tabbedPane,
                hostPortField,
                fileNamesList
        ));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Клиент");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setVisible(true);
    }
}
