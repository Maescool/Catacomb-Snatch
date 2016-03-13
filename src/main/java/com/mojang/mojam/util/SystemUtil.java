package com.mojang.mojam.util;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class SystemUtil {
    public static void setClipboard(String contents) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(contents), null);
    }

    public static String getClipboard() {
        try {
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if(t != null & t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)t.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
            }
        } catch(Exception e) { e.printStackTrace(); }
        return "";
    }
}
