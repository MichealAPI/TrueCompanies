package it.mikeslab.truecompanies.util.error;

import io.sentry.Sentry;
import org.bukkit.Bukkit;

public class SentryDiagnostic {
    private final static String DSN = "https://5909fed2602f49cf805a3cac34a88086@o4504045887029248.ingest.sentry.io/4505538530705408";
    private static boolean initialized;

    public static void initSentry() {
        Sentry.init(options -> {
            options.setDsn(DSN);

            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");


            options.setTag("plugin", "TrueCompanies");
            options.setTag("ServerVersion", Bukkit.getServer().getVersion());
            options.setTag("ServerImplementation", Bukkit.getServer().getName());
            options.setTag("environment", osName + " " + osVersion + " " + osArch);
            options.setTag("runtime", System.getProperty("java.version"));

        });

        initialized = true;
    }


    public static void capture(Throwable throwable) {
        if(!initialized) return;

        Sentry.captureException(throwable);
    }
}