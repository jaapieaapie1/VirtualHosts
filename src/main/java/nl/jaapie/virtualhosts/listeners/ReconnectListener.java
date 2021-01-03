package nl.jaapie.virtualhosts.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.jaapie.virtualhosts.VirtualHosts;
import nl.jaapie.virtualhosts.events.VirtualHostEvent;
import nl.jaapie.virtualhosts.VirtualHost;

public class ReconnectListener implements ReconnectHandler {

    private final ReconnectHandler superHandler;

    public ReconnectListener(ReconnectHandler superHandler) {
        this.superHandler = superHandler;
    }

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        String hostname = player.getPendingConnection().getVirtualHost().getHostName();
        VirtualHost vHost = VirtualHosts.getVirtualHost(hostname);
        if(vHost == null)
            return superHandler.getServer(player);

        ProxyServer server = ProxyServer.getInstance();
        ServerInfo info = server.getServerInfo(vHost.getServer());

        server.getLogger().info("Virtualhost sended " + player.getName() + " (" + player.getUniqueId().toString() + ") to server " + info.getName());

        server.getPluginManager().callEvent(new VirtualHostEvent(player, info, hostname));
        return info;
    }

    @Override
    public void setServer(ProxiedPlayer player) {
        superHandler.setServer(player);
    }

    @Override
    public void save() {
        superHandler.save();
    }

    @Override
    public void close() {
        superHandler.close();
    }
}
