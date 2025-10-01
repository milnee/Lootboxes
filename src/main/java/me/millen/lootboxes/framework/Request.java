package me.millen.lootboxes.framework;
/*
 *  created by Turben on 01/06/2020
 */

import org.bukkit.inventory.Inventory;

public class Request{

	private Inventory inventory;
	private Box box;

	public Request(Inventory inventory, Box box){
		this.inventory = inventory;
		this.box = box;
	}

	public Inventory getInventory(){
		return inventory;
	}

	public Box getBox(){
		return box;
	}
}
