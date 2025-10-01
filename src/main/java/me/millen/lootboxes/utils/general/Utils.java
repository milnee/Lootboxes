package me.millen.lootboxes.utils.general;
/*
 *  created by Turben on 01/06/2020
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class Utils{

	public static List<String> asLore(String... strings){
		return Arrays.asList(strings);
	}

	public static List<String> colorize(List<String> list){
		List<String> newList = new ArrayList<>();
		for(String string : list)
			newList.add(ChatColor.translateAlternateColorCodes('&', string.replace("{arrow}", "»")));

		return newList;
	}
}
