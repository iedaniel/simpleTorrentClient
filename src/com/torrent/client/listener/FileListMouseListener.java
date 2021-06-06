package com.torrent.client.listener;

import com.torrent.client.FileItem;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileListMouseListener implements MouseListener {

    private final JList<FileItem> fileNamesList;
    private final DefaultListModel<FileItem> model;
    private final JTextField hostPortField;
    private final JPanel panelMain;

    public FileListMouseListener(JList<FileItem> fileNamesList,
                                 DefaultListModel<FileItem> model,
                                 JTextField hostPortField,
                                 JPanel panelMain) {
        this.fileNamesList = fileNamesList;
        this.model = model;
        this.hostPortField = hostPortField;
        this.panelMain = panelMain;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int index = fileNamesList.locationToIndex(e.getPoint());
            FileItem fileItem = model.get(index);
            String downloadAddr = "http://" + hostPortField.getText() + "/download?fileName=" + fileItem.getName();
            HttpURLConnection fileSizeConnection = getFileSizeConnection(downloadAddr);
            long fileSize = getFileSizeFromConnection(fileSizeConnection);

            int downloadConfirmed = JOptionPane.showConfirmDialog(panelMain, "Размер файла - " +
                    fileSize + " байт" + "\nНажмите \"Yes\" для скачивания");
            if (downloadConfirmed != 0) {
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int save = chooser.showDialog(panelMain, "save");
            if (save != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File downloadPath = chooser.getSelectedFile();

            startFileDownload(index, fileItem, downloadAddr, fileSize, downloadPath);
        }
    }

    private void startFileDownload(int index, FileItem fileItem, String downloadAddr, long fileSize, File downloadPath) {
        Runnable updateThread = () -> {
            try (BufferedInputStream in1 = new BufferedInputStream(new URL(downloadAddr).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(downloadPath)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                long total = 0;
                while ((bytesRead = in1.read(dataBuffer, 0, 1024)) != -1) {
                    total += bytesRead;
                    long tt = total;
                    fileItem.setProgress((int) (tt * 100 / fileSize));
                    model.set(index, fileItem);
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        };
        new Thread(updateThread).start();
    }

    private long getFileSizeFromConnection(HttpURLConnection fileSizeConnection) {
        long fileSize = fileSizeConnection.getContentLengthLong();
        fileSizeConnection.disconnect();
        return fileSize;
    }

    private HttpURLConnection getFileSizeConnection(String downloadAddr) {
        HttpURLConnection downloadConnection = null;
        try {
            URL downloadUrl = new URL(downloadAddr);
            downloadConnection = (HttpURLConnection) downloadUrl.openConnection();
            downloadConnection.setRequestMethod("HEAD");
            downloadConnection.getInputStream();
        } catch (Exception malformedURLException) {
            malformedURLException.printStackTrace();
        }
        return downloadConnection;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
