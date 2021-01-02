package nl.jaapie.virtualhosts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

@Getter
@AllArgsConstructor()
public class VirtualHost {

    private final String host;
    private final String server;

    /**
     * Create from config section
     * @param host host
     * @param configSection configSection
     */
    public VirtualHost(String host, Configuration configSection) {
        this.host = host;
        this.server = configSection.getString("server");
    }
}
