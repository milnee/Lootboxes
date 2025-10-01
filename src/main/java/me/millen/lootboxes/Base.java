package me.millen.lootboxes;
/*
 *  created by millen on 01/06/2020
 */

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.bukkit.plugin.java.JavaPlugin;

import me.millen.lootboxes.cache.Cache;
import me.millen.lootboxes.command.BoxCommand;
import me.millen.lootboxes.listeners.ClickBox;
import me.millen.lootboxes.listeners.InventoryClick;
import me.millen.lootboxes.management.managers.BoxManager;
import me.millen.lootboxes.management.managers.SaveManager;
import me.millen.lootboxes.utils.updater.Updater;

public class Base extends JavaPlugin{

	public static Base get(){
		return getPlugin(Base.class);
	}

	private BoxManager boxManager;
	private SaveManager saveManager;

	private Cache cache;

	boolean validated;

	@Override
	public void onEnable(){
		verifyConfiguration();
		setup();
		validated = true;
	}

	@Override
	public void onDisable(){
		if(validated)
			saveManager.save();
	}

	public void setup(){
		cache = new Cache();
		cache.setup();

		boxManager = new BoxManager();
		boxManager.setup();

		saveManager = new SaveManager();
		saveManager.setup();

		getCommand("box").setExecutor(new BoxCommand());

		getServer().getPluginManager().registerEvents(new InventoryClick(), this);
		getServer().getPluginManager().registerEvents(new ClickBox(), this);
		getServer().getPluginManager().registerEvents(new BoxManager(), this);
	}

	public void verifyConfiguration(){
		File file = new File(getDataFolder(), "config.yml");

		Updater updater = new Updater();
		try{
			if(!file.exists()){
				saveDefaultConfig();
				reloadConfig();
				return;
			}
			updater.update(this, "config.yml", file, Collections.emptyList());
		}catch(IOException ex){
			getLogger().warning(String.format("Failed to update config.yml: %s", ex.getMessage()));
		}

		reloadConfig();
	}

	public void reloadCache(){
		reloadConfig();
		cache.reload();
	}

	public BoxManager getBoxManager(){
		return boxManager;
	}

	public Cache getCache(){
		return cache;
	}
}
