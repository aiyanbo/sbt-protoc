package com.github.os72.protocjar;

import java.io.IOException;

/**
 * @author AI
 * 2019/11/5
 */
public class MavenUtils {

    private MavenUtils() {
    }

    public static MavenUtil.MavenSettings getMavenSettings() {
        return MavenUtil.getMavenSettings();
    }

    public static URLSpec getReleaseDownloadUrl(String path, MavenUtil.MavenSettings settings) throws IOException {
        return MavenUtil.getReleaseDownloadUrl(path, settings);
    }

}
