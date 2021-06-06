package com.torrent.client.listener;

import com.torrent.client.FileItem;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HostPortFieldListener extends KeyAdapter {

    private final JPanel panelMain;
    private final JTabbedPane tabbedPane;
    private final JTextField hostPortField;
    private final JList<FileItem> fileNamesList;

    public HostPortFieldListener(JPanel panelMain,
                                 JTabbedPane tabbedPane,
                                 JTextField hostPortField,
                                 JList<FileItem> fileNamesList) {
        this.panelMain = panelMain;
        this.tabbedPane = tabbedPane;
        this.hostPortField = hostPortField;
        this.fileNamesList = fileNamesList;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }
        tabbedPane.setSelectedIndex(1);
        tabbedPane.setEnabledAt(0, false);
        JOptionPane.showMessageDialog(panelMain, "Двойное нажатие позволит скачать файл");
        try {
            HttpURLConnection listFilesConnection = getListFilesConnection();
            List<FileItem> names = getFileNames(listFilesConnection);
            DefaultListModel<FileItem> model = new DefaultListModel<>();
            model.addAll(names);
            fileNamesList.setModel(model);
            fileNamesList.addMouseListener(new FileListMouseListener(
                    fileNamesList,
                    model,
                    hostPortField,
                    panelMain
            ));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private List<FileItem> getFileNames(HttpURLConnection listFilesConnection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(listFilesConnection.getInputStream()));
        StringBuilder contentBuffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            contentBuffer.append(line);
        }
        listFilesConnection.disconnect();
        List<FileItem> names = Stream.of(contentBuffer.toString().split(","))
                .map(s -> new FileItem(s, 0))
                .collect(Collectors.toList());
        return names;
    }

    private HttpURLConnection getListFilesConnection() throws IOException {
        URL listFilesUrl = new URL("http://" + hostPortField.getText() + "/download/names");
        HttpURLConnection listFilesConnection = (HttpURLConnection) listFilesUrl.openConnection();
        listFilesConnection.setRequestMethod("GET");
        return listFilesConnection;
    }
}
