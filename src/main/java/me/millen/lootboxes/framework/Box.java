package me.millen.lootboxes.framework;
/*
 *  created by Turben on 01/06/2020
 */

import me.millen.lootboxes.Base;
import me.millen.lootboxes.utils.general.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Box{

	private String label;
	private Inventory loot;
	private ItemStack stack;

	public Box(String label, ItemStack stack){
		this.label = label;
		this.loot = Bukkit.createInventory(null, 54, ChatColor.GRAY +label +" Loot");

		ItemStack separator = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
		ItemMeta meta = separator.getItemMeta();
		meta.setDisplayName(ChatColor.BOLD +"Separator");
		meta.setLore(Utils.asLore(
				ChatColor.GOLD +"< " +ChatColor.WHITE +"Normal Rewards"
				, ChatColor.GOLD +"> " +ChatColor.WHITE +"Final Rewards"));
		separator.setItemMeta(meta);
		for(int i = 0; i < 6; i++){
			int slot = 6 + (i > 0 ? i * 9 : i);
			loot.setItem(slot, separator);
		}

		this.stack = stack;

		Base.get().getBoxManager().add(this);
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label = label;
	}

	public Inventory getLoot(){
		return loot;
	}

	public ItemStack getStack(){
		return stack;
	}
}
