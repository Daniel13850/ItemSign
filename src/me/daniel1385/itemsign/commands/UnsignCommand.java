package me.daniel1385.itemsign.commands;

import java.util.ArrayList;
import java.util.List;

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

public class UnsignCommand implements CommandExecutor {
	public ItemSign plugin;

	public UnsignCommand(ItemSign plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(plugin.getPrefix() + "§cDieser Befehl kann nur von einem Spieler ausgeführt werden!");
			return false;
		}
		Player p = (Player) sender;
		if(!p.hasPermission("itemsign.unsign")) {
			p.sendMessage(plugin.getPrefix() + "§4Dafür hast du keine Berechtigung!");
			return false;
		}
		ItemStack item = p.getInventory().getItemInMainHand();
		if(item.getType().equals(Material.AIR)) {
			p.sendMessage(plugin.getPrefix() + "§cBitte halte das Item in der Hand!");
			return false;
		}
		SignatureData data = plugin.getSignature(item);
		if(data == null) {
			p.sendMessage(plugin.getPrefix() + "§cDas Item ist nicht signiert!");
			return false;
		} else if(!data.getPlayerUUID().equals(p.getUniqueId().toString())) {
			if(!p.hasPermission("itemsign.unsign.all")) {
				p.sendMessage(plugin.getPrefix() + "§cDie Signatur ist nicht von dir!");
				return false;
			}
		}
		ItemMeta meta = item.getItemMeta();
		PersistentDataContainer cont = meta.getPersistentDataContainer();
		NamespacedKey keyLast = new NamespacedKey(plugin, "signLast");
		List<String> lore = meta.getLore();
		if(lore == null) {
			lore = new ArrayList<>();
		}
		int start = lore.indexOf(cont.get(keyLast, PersistentDataType.STRING))-(data.getLines().size()+1);
		if(start >= 0) {
			lore.remove(start);
			for(int i = 0; i < data.getLines().size(); i++) {
				lore.remove(start);
			}
			lore.remove(start);
		}
		meta.setLore(lore);
		NamespacedKey keySign1 = new NamespacedKey(plugin, "sign1");
		NamespacedKey keySign2 = new NamespacedKey(plugin, "sign2");
		NamespacedKey keySign3 = new NamespacedKey(plugin, "sign3");
		NamespacedKey keySignAuthor = new NamespacedKey(plugin, "signAuthor");
		NamespacedKey keySignUUID = new NamespacedKey(plugin, "signUUID");
		NamespacedKey keySignDate = new NamespacedKey(plugin, "signDate");
		cont.remove(keySign1);
		cont.remove(keySign2);
		cont.remove(keySign3);
		cont.remove(keySignAuthor);
		cont.remove(keySignUUID);
		cont.remove(keySignDate);
		cont.remove(keyLast);
		item.setItemMeta(meta);
		p.sendMessage(plugin.getPrefix() + "§aDie Signatur wurde entfernt!");
		return true;
	}

}
