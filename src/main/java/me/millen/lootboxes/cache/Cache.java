package me.millen.lootboxes.cache;
/*
 *  created by Turben on 04/06/2020
 */

import com.google.common.collect.Maps;
import me.millen.lootboxes.Base;
import me.millen.lootboxes.utils.general.Utils;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;

public class Cache{

	private Map<String, String> messages = Maps.newConcurrentMap();

	public void setup(){
		for(String key : Base.get().getConfig().getKeys(false))
			if(Base.get().getConfig().get(key) instanceof String)
				messages.put(key, Base.get().getConfig().getString(key));
	}

	public void reload(){
		messages.clear();
		setup();
	}

	public String PERMISSION_DENIED(){
		return getString("permission-denied");
	}

	public String NORMAL_REWARD_NAME(){
		return getString("normal-reward-name").replace("{arrow}", "»");
	}

	public String FINAL_REWARD_NAME(){
		return getString("final-reward-name").replace("{arrow}", "»");
	}

	public List<String> NORMAL_REWARD_LORE(){
		return Utils.colorize(Base.get().getConfig().getStringList("normal-reward-lore"));
	}

	public List<String> FINAL_REWARD_LORE(){
		return Utils.colorize(Base.get().getConfig().getStringList("final-reward-lore"));
	}

	public String getString(String key){
		return ChatColor.translateAlternateColorCodes('&', messages.get(key));
	}
}
