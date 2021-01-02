package nl.jaapie.virtualhosts;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

@Getter
public class VirtualHost {

    /**
     * Host name
     */
    private final String host;
    /**
     * Server name
     */
    private final String server;

    /**
     * Virtual host
     * @param host hostname
     * @param server sername
     */
    public VirtualHost(String host, String server) {
        this.host = host;
        this.server = server;
    }

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
