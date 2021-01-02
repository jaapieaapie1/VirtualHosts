package nl.jaapie.virtualhosts;

import lombok.AccessLevel;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nl.jaapie.virtualhosts.commands.VirtualHostCommand;
import nl.jaapie.virtualhosts.listeners.ReconnectListener;
import nl.jaapie.virtualhosts.models.VirtualHost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class VirtualHosts extends Plugin {

    @Getter
    private static VirtualHosts instance;

    @Getter(AccessLevel.PUBLIC)
    private static final HashMap<String, VirtualHost> hosts = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        getProxy().getPluginManager().registerCommand(this, new VirtualHostCommand());
        getProxy().setReconnectHandler(new ReconnectListener(getProxy().getReconnectHandler()));

        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

            if(configuration.contains("hosts")) {
                for (String key : configuration.getSection("hosts").getKeys()) {
                    String hostName = key.replaceAll("_", ".");
                    registerVirtualHost(new VirtualHost(hostName, configuration.getSection("hosts." + key)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register virtual host
     * @param host host to register
     */
    public static void registerVirtualHost(VirtualHost host) {
        hosts.put(host.getHost(), host);
    }

    /**
     * remove a host
     * @param hostname hostname
     */
    public static void unregisterVirtualHost(String hostname) {
        hosts.remove(hostname);
    }

    /**
     * get virtual host
     * @param hostname hostname
     * @return Virtual host
     */
    public static VirtualHost getVirtualHost(String hostname) {
        return hosts.get(hostname);
    }

    /**
     * get list of hostnames
     */
    public static List<String> getVirtualHosts() {
        return new ArrayList<>(hosts.keySet());
    }
}
