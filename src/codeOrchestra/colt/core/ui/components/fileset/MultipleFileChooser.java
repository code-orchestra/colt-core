package codeOrchestra.colt.core.ui.components.fileset;

import javax.swing.*;
import java.io.File;

/**
 * @author Eugene Potapenko
 */
public class MultipleFileChooser {
    static javax.swing.JFileChooser chooser = null;

    static void init(){
        if (chooser == null) {
            chooser = new JFileChooser();
        }
    }

    static File chooseFile(){
        init();
        javax.swing.JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.showOpenDialog(null);
        return chooser.getSelectedFile();
    }
}
