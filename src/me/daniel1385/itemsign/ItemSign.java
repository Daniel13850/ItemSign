package me.daniel1385.itemsign;

import me.daniel1385.itemsign.commands.SignCommand;
import me.daniel1385.itemsign.commands.UnsignCommand;
import me.daniel1385.itemsign.objects.SignatureData;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemSign extends JavaPlugin {
    private FileConfiguration config;
    private String prefix;

    @Override
    public void onEnable() {
        try {
            getDataFolder().mkdirs();
            File file = new File(getDataFolder(), "config.yml");
            if(!file.exists()) {
                InputStream in = getResource("config.yml");
                Files.copy(in, file.toPath());
                in.close();
            }
            config = YamlConfiguration.loadConfiguration(file);
            prefix = translateAllCodes(config.getString("prefix")) + "§r";
        } catch(IOException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("sign").setExecutor(new SignCommand(this));
        getCommand("unsign").setExecutor(new UnsignCommand(this));
        Metrics metrics = new Metrics(this, 23808);
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public SignatureData getSignature(ItemStack stack) {
        if(stack == null) {
            return null;
        }
        if(!stack.getType().isItem()) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) {
            return null;
        }
        PersistentDataContainer cont = meta.getPersistentDataContainer();
        NamespacedKey keySign1 = new NamespacedKey(this, "sign1");
        NamespacedKey keySign2 = new NamespacedKey(this, "sign2");
        NamespacedKey keySign3 = new NamespacedKey(this, "sign3");
        NamespacedKey keySignAuthor = new NamespacedKey(this, "signAuthor");
        NamespacedKey keySignUUID = new NamespacedKey(this, "signUUID");
        NamespacedKey keySignDate = new NamespacedKey(this, "signDate");
        if(!cont.has(keySign1, PersistentDataType.STRING)) {
            return null;
        }
        String sign1 = cont.get(keySign1, PersistentDataType.STRING);
        String sign2 = null;
        if(cont.has(keySign2, PersistentDataType.STRING)) {
            sign2 = cont.get(keySign2, PersistentDataType.STRING);
        }
        String sign3 = null;
        if(cont.has(keySign3, PersistentDataType.STRING)) {
            sign3 = cont.get(keySign3, PersistentDataType.STRING);
        }
        String author = cont.get(keySignAuthor, PersistentDataType.STRING);
        String uuid = cont.get(keySignUUID, PersistentDataType.STRING);
        String date = cont.get(keySignDate, PersistentDataType.STRING);
        List<String> list = new ArrayList<>();
        list.add(sign1);
        if(sign2 != null) {
            list.add(sign2);
        }
        if(sign3 != null) {
            list.add(sign3);
        }
        return new SignatureData(list, date, author, uuid);
    }

    private String translateHexCodes (String text) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(text.substring(matcher.start()+1, matcher.end()));
            text = text.replace(text.substring(matcher.start(), matcher.end()), color.toString());
            matcher = pattern.matcher(text);
        }

        return text;
    }

    private String translateAllCodes (String text) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', translateHexCodes(text));
    }
}
