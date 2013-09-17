package com.github.l33m4n123.cookieclicker.config;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigWriter {


	protected SimpleConfigManager configManager;
	protected SimpleConfig config;

	public void configWriter(JavaPlugin plugin) {

		String[] header = {
				"Welcome to the",
				"Configfile for AdminControl",
				"If you got any issues shoot me a pm",
				"on forums.bukkit.org",
				"",
				"This config file",
				"was created via the code",
				"by Log-out on Bukkit",
				"forums.bukkit.org/threads/tut-custom-yaml-configurations-with-comments.142592/"};

		this.configManager = new SimpleConfigManager(plugin);

		this.config = this.configManager.getNewConfig("config.yml", header);

		this.config.set("Database.host", "localhost");
		this.config.set("Database.port", "3306");
		this.config.set("Database.database", "minecraft");
		this.config.set("Database.user", "root");
		this.config.set("Database.password", "pass");

		// End of Database Construct

		String[] comment = {
				"Here starts the config", "for the start prices", "of CookieClicker"				
		};
		this.config.set("Cookie.prices.Cursor", "15", comment);
		this.config.set("Cookie.prices.Grandma", "100");
		this.config.set("Cookie.prices.Farm", "500");
		this.config.set("Cookie.prices.Factory", "3000");
		this.config.set("Cookie.prices.Mine", "10000");
		this.config.set("Cookie.prices.Shipment", "40000");
		this.config.set("Cookie.prices.AlchemyLab", "200000");
		this.config.set("Cookie.prices.Portal", "1666666");
		this.config.set("Cookie.prices.TimeMachine", "123456789");
		this.config.set("Cookie.prices.AntimatterCondenser", "3999999999");

		this.config.set("Price.growth", 1.15d);

		this.config.set("Price.boost.Cursor", 0.1d);
		this.config.set("Price.boost.Grandma", 0.5d);
		this.config.set("Price.boost.Farm", 2);
		this.config.set("Price.boost.Factory", 10);
		this.config.set("Price.boost.Mine", 40);
		this.config.set("Price.boost.Shipment", 100);
		this.config.set("Price.boost.AlchemyLab", 400);
		this.config.set("Price.boost.Portal", 6666);
		this.config.set("Price.boost.TimeMachine", 98765);
		this.config.set("Price.boost.AntimatterCondenser", 999999);

		this.config.saveConfig();
	}

	public String getString(String path) {
		return this.config.getString(path);
	}

}
