package me.millen.lootboxes.listeners;
/*
 *  created by Turben on 01/06/2020
 */

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.millen.lootboxes.Base;

public class InventoryClick implements Listener{

	@EventHandler
	public void click(InventoryClickEvent event){
		if(event.getInventory() == null || Base.get().getBoxManager().getBoxes().isEmpty() || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
		if(Base.get().getBoxManager().getBoxes().stream().anyMatch(box -> box.getLoot().equals(event.getInventory()))){
			if(!event.getWhoClicked().hasPermission("lootboxes.editloot"))
				event.setCancelled(true);
			else if(event.getCurrentItem().getItemMeta().hasDisplayName() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BOLD +"Separator"))
				event.setCancelled(true);
		}else if(event.getView().getTitle().equalsIgnoreCase(ChatColor.GRAY +"Box List"))
			event.setCancelled(true);
	}
}
