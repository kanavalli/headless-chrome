package software.reinvent.headless.chrome.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import software.reinvent.commons.config.ConfigLoader;

import java.io.File;

import static com.google.common.io.Resources.getResource;
import static java.lang.System.setProperty;
import static org.openqa.selenium.chrome.ChromeOptions.CAPABILITY;

/**
 * Created on 16.04.2017.
 *
 * @author <a href="mailto:lenny@reinvent.software">Leonard Daume</a>
 */
public class HeadlessChromeProvider implements Provider<ChromeDriver> {
    private Config config;

    @Inject
    public HeadlessChromeProvider(Config config) {
        this.config = config;
    }

    @Override
    public ChromeDriver get() {
        if (config == null) {
            config = ConfigLoader.load();
        }

        if (config.hasPath("webdriver.chrome.driver")) {
            setProperty("webdriver.chrome.driver", config.getString("webdriver.chrome.driver"));
        } else {
            final String chromedriver_linux64 = getResource(this.getClass(), "chromedriver_linux64").getFile();
            new File(chromedriver_linux64).setExecutable(true);
            setProperty("webdriver.chrome.driver", chromedriver_linux64);

        }

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(config.hasPath("webdriver.chrome.binary") ? config.getString("webdriver.chrome.binary") : "/usr/bin/google-chrome-unstable");
        final String windowSize;
        if (config.hasPath("chrome.window.size")) {
            windowSize = config.getString("chrome.window.size");
        } else {
            windowSize = "1920,1200";
        }
        chromeOptions.addArguments("--headless", "--disable-gpu", "--incognito", "window-size=" + windowSize);

        final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(CAPABILITY, chromeOptions);

        return new ChromeDriver(capabilities);
    }
}