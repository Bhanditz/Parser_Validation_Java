package org.bibalex.eol.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bibalex.eol.dwca.validation.DwcaValidator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Mina.Edward
 * */
public class LogHandler {

    private static boolean initialized = false;

    public static void initializeHandler(String propertiesFile) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(propertiesFile);
            prop.load(input);
            String logFileConfigurationsDirectory = prop.getProperty("log4j.configurationFile");
            System.setProperty("log4j.configurationFile", logFileConfigurationsDirectory);
            initialized = true;
        } catch (IOException ex) {
            System.err.println("Failed to load the log4j2_configurations from the properties file");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("Failed to close the properties file after trying to read the log4j2_configurations");
                }
            }
        }
    }

    public static Logger getLogger(String loggerName) {
        if (!initialized) {
            System.err.println("LogHandler not initialized !");
        }
        return LogManager.getLogger(loggerName);
    }

    public static void main(String[] args) {
        LogHandler.initializeHandler("configs.properties");
        Logger logger = getLogger(DwcaValidator.class.getName());
        logger.info("Starting logging after intializations");
//        Logger logger = LogManager.getLogger(DwcaValidator.class.getName());
//        logger.info("Starting logging after intializations");
    }

}