package nl.jaapie.virtualhosts.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

@Data
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class VirtualHostEvent extends Event {

    /**
     * Player getting connected with vhost
     */
    private final ProxiedPlayer player;

    /**
     * server
     */
    private final ServerInfo server;

    /**
     * hostname
     */
    private final String hostname;
}
