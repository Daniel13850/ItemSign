package me.daniel1385.itemsign.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.daniel1385.itemsign.ItemSign;
import me.daniel1385.itemsign.objects.SignatureData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

public class SignCommand implements CommandExecutor {
	private ItemSign plugin;

	public SignCommand(ItemSign plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return false;
		}
		Player p = (Player) sender;
		if(!(p.hasPermission("itemsign.sign"))) {
			p.sendMessage(plugin.getPrefix() + "§4Dafür hast du keine Berechtigung!");
			return false;
		}
		if(args.length == 0) {
			p.sendMessage(plugin.getPrefix() + "§cSyntax: §6/sign <Text>");
			return false;
		}
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item.getType().equals(Material.AIR)) {
			p.sendMessage(plugin.getPrefix() + "§cBitte halte das Item in der Hand!");
			return false;
		}
		int permission = 1;
		if(p.hasPermission("itemsign.lines.2")) {
			permission = 2;
		}
		if(p.hasPermission("itemsign.lines.3")) {
			permission = 3;
		}
		SignatureData data = plugin.getSignature(item);
		if(data != null) {
			if(data.getLines().size() >= permission || !data.getPlayerUUID().equals(p.getUniqueId().toString())) {
				p.sendMessage(plugin.getPrefix() + "§cDas Item ist bereits signiert!");
				return false;
			}
		}
		StringBuilder builder = new StringBuilder("§8" + args[0]);
		for(int i = 0; i < args.length; i++) {
			if(i == 0) {
				continue;
			}
			builder.append(" " + args[i]);
		}
		String text = builder.toString();
		if(p.hasPermission("itemsign.color.0")) {
			text = text.replace("&0", "§0");
		}
		if(p.hasPermission("itemsign.color.1")) {
			text = text.replace("&1", "§1");
		}
		if(p.hasPermission("itemsign.color.2")) {
			text = text.replace("&2", "§2");
		}
		if(p.hasPermission("itemsign.color.3")) {
			text = text.replace("&3", "§3");
		}
		if(p.hasPermission("itemsign.color.4")) {
			text = text.replace("&4", "§4");
		}
		if(p.hasPermission("itemsign.color.5")) {
			text = text.replace("&5", "§5");
		}
		if(p.hasPermission("itemsign.color.6")) {
			text = text.replace("&6", "§6");
		}
		if(p.hasPermission("itemsign.color.7")) {
			text = text.replace("&7", "§7");
		}
		if(p.hasPermission("itemsign.color.8")) {
			text = text.replace("&8", "§8");
		}
		if(p.hasPermission("itemsign.color.9")) {
			text = text.replace("&9", "§9");
		}
		if(p.hasPermission("itemsign.color.a")) {
			text = text.replace("&a", "§a");
		}
		if(p.hasPermission("itemsign.color.b")) {
			text = text.replace("&b", "§b");
		}
		if(p.hasPermission("itemsign.color.c")) {
			text = text.replace("&c", "§c");
		}
		if(p.hasPermission("itemsign.color.d")) {
			text = text.replace("&d", "§d");
		}
		if(p.hasPermission("itemsign.color.e")) {
			text = text.replace("&e", "§e");
		}
		if(p.hasPermission("itemsign.color.f")) {
			text = text.replace("&f", "§f");
		}
		if(p.hasPermission("itemsign.color.hex")) {
			text = translateHexCodes(text);
		}
		if(p.hasPermission("itemsign.format.bold")) {
			text = text.replace("&l", "§l");
		}
		if(p.hasPermission("itemsign.format.obfuscated")) {
			text = text.replace("&k", "§k");
		}
		if(p.hasPermission("itemsign.format.strikethrough")) {
			text = text.replace("&m", "§m");
		}
		if(p.hasPermission("itemsign.format.underline")) {
			text = text.replace("&n", "§n");
		}
		if(p.hasPermission("itemsign.format.italic")) {
			text = text.replace("&o", "§o");
		}
		text = text.replace("&r", "§r§8");
		if(ChatColor.stripColor(text).length() > 128) {
			p.sendMessage(plugin.getPrefix() + "§cDer Text ist zu lang.");
			return false;
		}
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		List<String> lines = new ArrayList<>();
		if(lore == null) {
			lore = new ArrayList<>();
		} else if(data != null) {
			lines = data.getLines();
			PersistentDataContainer cont = meta.getPersistentDataContainer();
			NamespacedKey keyLast = new NamespacedKey(plugin, "signLast");
			int start = lore.indexOf(cont.get(keyLast, PersistentDataType.STRING))-(data.getLines().size()+1);
			if(start >= 0) {
				lore.remove(start);
				for(int i = 0; i < data.getLines().size(); i++) {
					lore.remove(start);
				}
				lore.remove(start);
			}
		}
		lore.add(" ");
		lines.add(text);
		for(String s : lines) {
			lore.add(s);
		}
		String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
		String last = plugin.getFormat().replace("{PLAYER}", p.getName()).replace("{DATE}", date);
		lore.add(last);
		meta.setLore(lore);
		PersistentDataContainer cont = meta.getPersistentDataContainer();
		NamespacedKey keySign1 = new NamespacedKey(plugin, "sign1");
		NamespacedKey keySign2 = new NamespacedKey(plugin, "sign2");
		NamespacedKey keySign3 = new NamespacedKey(plugin, "sign3");
		NamespacedKey keySignAuthor = new NamespacedKey(plugin, "signAuthor");
		NamespacedKey keySignUUID = new NamespacedKey(plugin, "signUUID");
		NamespacedKey keySignDate = new NamespacedKey(plugin, "signDate");
		NamespacedKey keyLast = new NamespacedKey(plugin, "signLast");
		int i = 0;
		for(String s : lines) {
			i++;
			if(i == 1) {
				cont.set(keySign1, PersistentDataType.STRING, s);
			}
			if(i == 2) {
				cont.set(keySign2, PersistentDataType.STRING, s);
			}
			if(i == 3) {
				cont.set(keySign3, PersistentDataType.STRING, s);
			}
		}
		cont.set(keySignAuthor, PersistentDataType.STRING, p.getName());
		cont.set(keySignUUID, PersistentDataType.STRING, p.getUniqueId().toString());
		cont.set(keySignDate, PersistentDataType.STRING, date);
		cont.set(keyLast, PersistentDataType.STRING, last);
		item.setItemMeta(meta);
		p.sendMessage(plugin.getPrefix() + "§aDein Item wurde signiert!");
		return true;
	}

	private String translateHexCodes (String text) {
		Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(text);

		while(matcher.find()) {
			ChatColor color = ChatColor.of(text.substring(matcher.start()+1, matcher.end()));
			text = text.replace(text.substring(matcher.start(), matcher.end()), color.toString());
			matcher = pattern.matcher(text);
		}

		return text;
	}

	private String translateAllCodes (String text) {
		return ChatColor.translateAlternateColorCodes('&', translateHexCodes(text));
	}

}
