package com.github.l33m4n123.cookieclicker.EventHandlers;

import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.l33m4n123.cookieclicker.CookieClicker;

public class PlayerEventHandler implements Listener {

	Plugin plugin;
	
	public PlayerEventHandler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	// Adds the player into the database
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent evt) {
		final Player player = evt.getPlayer();
		plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(plugin, new BukkitRunnable() {

					@Override
					public void run() {
						player.setScoreboard(Bukkit.getServer()
								.getScoreboardManager().getNewScoreboard());

					}
				}, 1L);

		try {
			CookieClicker.playerWriter(evt.getPlayer().getName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		player.setScoreboard(Bukkit.getServer().getScoreboardManager()
				.getNewScoreboard());
		if (CookieClicker.scoreBoard.contains(player.getName())) {
			CookieClicker.scoreBoard.remove(player.getName());
		}
	}
	
	@EventHandler
	public void onButtonPress(PlayerInteractEvent evt) {
		if (evt.getAction() == Action.RIGHT_CLICK_BLOCK
				&& evt.getClickedBlock().getType() == Material.STONE_BUTTON) {
			Player p = evt.getPlayer();
			if (CookieClicker.scoreBoard.contains(p.getName())) {
				CookieClicker.cookie.get(p.getName()).setScore(
						CookieClicker.cookie.get(p.getName()).getScore() + 1);

				// Save changes in the Database
				Statement statement;
				try {
					statement = CookieClicker.c.createStatement();
					String querySelectCookies = "UPDATE `CookieClicker` set `cookies` ='"
							+ String.valueOf(CookieClicker.cookie.get(p.getName()).getScore())
							+ "' WHERE `Name`='" + p.getName() + "'";
					statement.executeUpdate(querySelectCookies);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
}
