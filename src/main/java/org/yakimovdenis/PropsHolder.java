package org.yakimovdenis;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.util.Properties;

public class PropsHolder {
    private static final String DEFAULT_PROPS = "application.yml";
    private Properties props;

    public PropsHolder() {
        Yaml yaml = new Yaml();
        Properties config = yaml.loadAs(DEFAULT_PROPS, Properties.class );
        if (null==config){
            System.out.println("Internationalization properties file is not found");
            System.exit(1);
        }
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }
}
