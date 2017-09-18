package org.yakimovdenis;


import org.yaml.snakeyaml.Yaml;

import java.util.Properties;

public class Greeter {
    private static final String DEFAULT_PROPS = "application.yml";

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        Properties config = yaml.loadAs(DEFAULT_PROPS, Properties.class );
        if (null==config){
            System.out.println("Internationalization properties file is not found");
            System.exit(1);
        }

        if (args.length==0){
            System.out.println(config.getProperty("app.errorsMessages.emptyArgs"));
        }
    }
}
