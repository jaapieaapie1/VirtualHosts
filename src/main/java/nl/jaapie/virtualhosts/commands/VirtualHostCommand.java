package nl.jaapie.virtualhosts.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nl.jaapie.virtualhosts.VirtualHosts;
import nl.jaapie.virtualhosts.models.VirtualHost;
import org.yaml.snakeyaml.util.ArrayUtils;
import sun.security.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VirtualHostCommand extends Command implements TabExecutor {

    public VirtualHostCommand() {
        super("vhost", "", "virtualhost", "vhosts");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission("virtualhost.command")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid permission"));
            return;
        }

        if(args.length == 0) {
            sendHelp(sender);
            return;
        }

        switch (args[0]) {
            case "list":

                Set<String> keyset = VirtualHosts.getHosts().keySet();
                TextComponent[] texts = new TextComponent[keyset.size() + 1];
                texts[0] = new TextComponent(ChatColor.GREEN + "All vhosts:\n");
                String[] keys =  keyset.toArray(new String[0]);
                for (int i = 0; i < keys.length; i++) {
                    String s = keys[i];
                    String servername = VirtualHosts.getVirtualHost(s).getServer();
                    texts[i + 1] = new TextComponent(ChatColor.GREEN + s + " -> " + servername + "\n");
                }

                sender.sendMessage(texts);
                break;

            case "create":
                if(args.length < 3) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Use it like this: /vhost create <hostname> <server>"));
                    break;
                }
                String hostName = args[1];
                String serverName = args[2];

                if(VirtualHosts.getVirtualHost(hostName) != null) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Vhost already exists"));
                    break;
                }

                if(ProxyServer.getInstance().getServerInfo(serverName) == null) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Server doesn't exists"));
                    break;
                }

                try {
                    File file = new File(VirtualHosts.getInstance().getDataFolder(), "config.yml");
                    ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
                    Configuration configuration = provider.load(file);
                    Configuration section = new Configuration();
                    section.set("server", serverName);
                    configuration.set("hosts." + hostName.replaceAll("\\.", "_"), section);

                    provider.save(configuration, file);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
//                configuration.

                VirtualHosts.registerVirtualHost(new VirtualHost(hostName, serverName));
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "Registered and saved " + hostName));
                break;

            case "remove":
                if(args.length < 2) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Use it like this: /vhost remove <hostname>"));
                    return;
                }

                String hostname = args[1];

                if(VirtualHosts.getVirtualHost(hostname) == null) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "Vhost doesn't exists"));
                    return;
                }

                try {
                    File file = new File(VirtualHosts.getInstance().getDataFolder(), "config.yml");
                    ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
                    Configuration configuration = provider.load(file);
                    configuration.set("hosts." + hostname.replaceAll("\\.", "_"), null);

                    provider.save(configuration, file);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                VirtualHosts.unregisterVirtualHost(hostname);
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "Removed " + hostname));
                break;
            case "default":
                sendHelp(sender);
                break;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> tabs = new ArrayList<>();
        if(!sender.hasPermission("virtualhost.command")) {
            return tabs;
        }
        if(args.length < 2) {
            tabs.add("list");
            tabs.add("create");
            tabs.add("remove");
            return tabs;
        }

        switch (args[0]) {
            case "create":
                if(args.length == 3)
                    return new ArrayList<>(ProxyServer.getInstance().getServers().keySet());
                break;

            case "remove":
                if(args.length == 2)
                    return VirtualHosts.getVirtualHosts();
                break;
        }
        return tabs;
    }

    public void sendHelp(CommandSender sender) {
        TextComponent text1 = new TextComponent(ChatColor.RED + "Use /vhost like this:\n");
        TextComponent text2 = new TextComponent(ChatColor.RED + "/vhost list\n");
        text2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/vhost list"));
        TextComponent text3 = new TextComponent(ChatColor.RED + "/vhost create <hostname> <server>\n");
        text3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/vhost create <hostname> <server>"));
        TextComponent text4 = new TextComponent(ChatColor.RED + "/vhost remove <hostname>");
        text4.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/vhost remove <hostname>"));

        sender.sendMessage(text1, text2, text3, text4);
    }
}
