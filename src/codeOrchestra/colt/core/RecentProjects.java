package codeOrchestra.colt.core;

import codeOrchestra.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Alexander Eliseyev
 */
public class RecentProjects {

    private static final String RECENT_COLT_PROJECTS = "recentCOLTProjects";

    private static Preferences preferences = Preferences.userNodeForPackage(RecentProjects.class);

    public static void addRecentProject(String path) {
        List<String> paths = getRecentProjectsPaths();

        if (paths.contains(path)) {
            paths.remove(path);
        }

        paths.add(0, path);

        preferences.put(RECENT_COLT_PROJECTS, createList(paths));

        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            // ignore
        }
    }

    public static List<String> getRecentProjectsPaths() {
        return parseString(preferences.get(RECENT_COLT_PROJECTS, ""));
    }

    private static String createList(List<String> list) {
        StringBuffer path = new StringBuffer("");//$NON-NLS-1$

        for (String item : list) {
            path.append(item);
            path.append(File.pathSeparator);
        }

        return path.toString();
    }

    private static List<String> parseString(String stringList) {
        List<String> result = new ArrayList<String>();

        if (StringUtils.isEmpty(stringList)) {
            return result;
        }

        StringTokenizer st = new StringTokenizer(stringList, File.pathSeparator + "\n\r");

        while (st.hasMoreElements()) {
            result.add((String) st.nextElement());
        }
        return result;
    }

    public static void clear(String leave) {
        preferences.put(RECENT_COLT_PROJECTS, leave != null ? leave : "");
    }

}
