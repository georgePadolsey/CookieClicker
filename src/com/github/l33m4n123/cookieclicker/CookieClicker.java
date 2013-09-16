package com.github.l33m4n123.cookieclicker;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author: Leeman;
 */
public class CookieClicker extends JavaPlugin implements Listener {

	// Some declarations that I am going to need

	public SimpleConfigManager configManager;

	public SimpleConfig config;
	public static SimpleConfig playerConfig;

	private long cursorCurrentPrize;
	private long grandmaCurrentPrize;
	private long farmCurrentPrize;
	private long factoryCurrentPrize;
	private long mineCurrentPrize;
	private long shipmentCurrentPrize;
	private long alchemyCurrentPrize;
	private long portalCurrentPrize;
	private long timeCurrentPrize;
	private long antimatterCurrentPrize;
	private double cookiePerSecond = 0;
	private double growth;
	static int cookies;

	private Objective objective;
	private Scoreboard board;
	private HashMap<String, Score> cookie = new HashMap<String, Score>();
	private HashMap<String, Score> cursor = new HashMap<String, Score>();
	private HashMap<String, Score> grandma = new HashMap<String, Score>();
	private HashMap<String, Score> farm = new HashMap<String, Score>();
	private HashMap<String, Score> factory = new HashMap<String, Score>();
	private HashMap<String, Score> mine = new HashMap<String, Score>();
	private HashMap<String, Score> shipment = new HashMap<String, Score>();
	private HashMap<String, Score> alchemy = new HashMap<String, Score>();
	private HashMap<String, Score> portal = new HashMap<String, Score>();
	private HashMap<String, Score> time = new HashMap<String, Score>();
	private HashMap<String, Score> antimatter = new HashMap<String, Score>();

	static HashSet<String> scoreBoard = new HashSet<String>();

	@Override
	public void onEnable() {
		// TODO Insert logic to be performed when the plugin is enabled
		getServer().getPluginManager().registerEvents(this, this);

		configWriter();

		cookieCheck();

	}

	// Writes the default config.yml
	public void configWriter() {

		String[] header = {
				"Welcome to the",
				"Configfile for Cookieclicker",
				"If you got any issues shoot me a pm",
				"on forums.bukkit.org",
				"",
				"",
				"This config file",
				"was created via the code",
				"by Log-out on Bukkit",
				"http://forums.bukkit.org/threads/tut-custom-yaml-configurations-with-comments.142592/" };
		String[] comment = { "Starting Price for the individual upgrades.",
				"The AntimatterCondenser Price MUST be saved",
				"as a String or you will get issues" };
		String[] comment2 = { "The grow Value will control",
				"how strong the prices grow", "The other Value control",
				"The Cookie per second", "the individual upgrade gives" };

		this.configManager = new SimpleConfigManager(this);

		this.config = configManager.getNewConfig("config.yml", header);

		this.config.set("Cookie.prices.Cursor", 15, comment);
		this.config.set("Cookie.prices.Grandma", 100);
		this.config.set("Cookie.prices.Farm", 500);
		this.config.set("Cookie.prices.Factory", 3000);
		this.config.set("Cookie.prices.Mine", 10000);
		this.config.set("Cookie.prices.Shipment", 40000);
		this.config.set("Cookie.prices.AlchemyLab", 200000);
		this.config.set("Cookie.prices.Portal", 1666666);
		this.config.set("Cookie.prices.TimeMachine", 123456789);
		this.config.set("Cookie.prices.AntimatterCondenser", "3999999999");

		this.config.set("Price.growth", 1.15d, comment2);

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

	// Writes a new player config when a player logs in
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent evt) {

		board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		objective = board.registerNewObjective("test", "dummy");
		objective.setDisplayName("CookieClicker");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		String[] header = { "This File",
				"stores " + evt.getPlayer().getName() + " cookie", "stats" };

		String[] comment = { "Prices for this player.",
				"do not touch this unless", "you want to give this specific",
				"player a boost/ruin his score" };

		this.configManager = new SimpleConfigManager(this);
		playerConfig = configManager.getNewConfig("players/"
				+ evt.getPlayer().getName() + ".cookie", header);

		playerConfig.set("Cookie.Cookies", 0, comment);

		playerConfig.set("Cookie.Cookie.Per.Second", 0);

		playerConfig.set("Cookie.upgrades.Cursor", 0);
		playerConfig.set("Cookie.upgrades.Grandma", 0);
		playerConfig.set("Cookie.upgrades.Farm", 0);
		playerConfig.set("Cookie.upgrades.Factory", 0);
		playerConfig.set("Cookie.upgrades.Mine", 0);
		playerConfig.set("Cookie.upgrades.Shipment", 0);
		playerConfig.set("Cookie.upgrades.AlchemyLab", 0);
		playerConfig.set("Cookie.upgrades.Portal", 0);
		playerConfig.set("Cookie.upgrades.TimeMachine", 0);
		playerConfig.set("Cookie.upgrades.AntimatterCondenser", 0);

		playerConfig.set("Cookie.prices.Cursor",
				config.getInt("Cookie.prices.Cursor"));
		playerConfig.set("Cookie.prices.Grandma",
				config.getInt("Cookie.prices.Grandma"));
		playerConfig.set("Cookie.prices.Farm",
				config.getInt("Cookie.prices.Farm"));
		playerConfig.set("Cookies.prices.Factory",
				config.getInt("Cookie.prices.Factory"));
		playerConfig.set("Cookie.prices.Mine",
				config.getInt("Cookie.prices.Mine"));
		playerConfig.set("Cookie.prices.Shipment",
				config.getInt("Cookie.prices.Shipment"));
		playerConfig.set("Cookie.prices.AlchemyLab",
				config.getInt("Cookie.prices.AlchemyLab"));
		playerConfig.set("Cookie.prices.Portal",
				config.getInt("Cookie.prices.Portal"));
		playerConfig.set("Cookie.prices.TimeMachine",
				config.getInt("Cookie.prices.TimeMachine"));
		playerConfig.set("Cookie.prices.AntimatterCondenser",
				config.getString("Cookie.prices.AntimatterCondenser"));
		playerConfig.saveConfig();

		pricesCheck();
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent evt) {
		if (scoreBoard.contains(evt.getPlayer().getName())) {
			scoreBoard.remove(evt.getPlayer().getName());
		}
	}

	public void pricesCheck() {
		this.cursorCurrentPrize = playerConfig.getInt("Cookie.prices.Cursor");
		this.grandmaCurrentPrize = playerConfig.getInt("Cookie.prices.Grandma");
		this.farmCurrentPrize = playerConfig.getInt("Cookie.prices.Farm");
		this.factoryCurrentPrize = playerConfig.getInt("Cookie.prices.Factory");
		this.mineCurrentPrize = playerConfig.getInt("Cookie.prices.Mine");
		this.shipmentCurrentPrize = playerConfig
				.getInt("Cookie.prices.Shipment");
		this.alchemyCurrentPrize = playerConfig
				.getInt("Cookie.prices.AlchemyLab");
		this.portalCurrentPrize = playerConfig.getInt("Cookie.prices.Portal");
		this.timeCurrentPrize = playerConfig
				.getInt("Cookie.prices.TimeMachine");
		this.antimatterCurrentPrize = Long.valueOf(playerConfig
				.getString("Cookie.prices.AntimatterCondenser"));

		this.growth = config.getDouble("Price.growth");
	}

	public void cookieCheck() {
		 this.getServer().getScheduler()
		 .scheduleSyncRepeatingTask(this, new BukkitRunnable() {
		
		 public void run() {
		 // Do stuff
		
		 for (Player player : getServer().getOnlinePlayers()) {
		 if (CookieClicker.scoreBoard.contains(player.getName())) {
		
		 int roundCps = (int) Math
		 .round(cookiePerSecond * 10);
		 int newScore = cookie.get(player.getName()).getScore() + roundCps;
		 cookie.get(player.getName()).setScore(newScore);
		 CookieClicker.cookies = newScore;
		 updatePlayerFile(player.getName());
		 }
		 }
		 }
		 }, 200L, 200L);
	}

	public void updatePlayerFile(String player) {

		String[] comment = { "Prices for this player.",
				"do not touch this unless", "you want to give this specific",
				"player a boost/ruin his score" };

		playerConfig.set("Cookie.Cookies", cookies, comment);

		playerConfig.set("Cookie.Cookie.Per.Second", this.cookiePerSecond);

		playerConfig.set("Cookie.upgrades.Cursor", cursor.get(player).getScore());
		playerConfig.set("Cookie.upgrades.Grandma", grandma.get(player).getScore());
		playerConfig.set("Cookie.upgrades.Farm", farm.get(player).getScore());
		playerConfig.set("Cookie.upgrades.Factory", factory.get(player).getScore());
		playerConfig.set("Cookie.upgrades.Mine", mine.get(player).getScore());
		playerConfig.set("Cookie.upgrades.Shipment", shipment.get(player).getScore());
		playerConfig.set("Cookie.upgrades.AlchemyLab", alchemy.get(player).getScore());
		playerConfig.set("Cookie.upgrades.Portal", portal.get(player).getScore());
		playerConfig.set("Cookie.upgrades.TimeMachine", time.get(player).getScore());
		playerConfig.set("Cookie.upgrades.AntimatterCondenser",
				antimatter.get(player).getScore());

		playerConfig.set("Cookie.prices.Cursor", cursorCurrentPrize);
		playerConfig.set("Cookie.prices.Grandma", grandmaCurrentPrize);
		playerConfig.set("Cookie.prices.Farm", farmCurrentPrize);
		playerConfig.set("Cookies.prices.Factory", factoryCurrentPrize);
		playerConfig.set("Cookie.prices.Mine", mineCurrentPrize);
		playerConfig.set("Cookie.prices.Shipment", shipmentCurrentPrize);
		playerConfig.set("Cookie.prices.AlchemyLab", alchemyCurrentPrize);
		playerConfig.set("Cookie.prices.Portal", portalCurrentPrize);
		playerConfig.set("Cookie.prices.TimeMachine", timeCurrentPrize);
		playerConfig.set("Cookie.prices.AntimatterCondenser",
				antimatterCurrentPrize);
		playerConfig.saveConfig();
	}

	@Override
	public void onDisable() {
		// TODO Insert logic to be performed when the plugin is disabled

	}

	/**
	 * 
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("cookieclicker")
				&& (sender instanceof Player)
				&& sender.hasPermission("cookie.clicker")) {
			Player player = (Player) sender;
			if (args.length == 1 && args[0].equalsIgnoreCase("start")) {

				player.setScoreboard(board);

				if (cookie.get(player.getName()) == null) {
					cookie.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Cookies: ")));
				}
				if (cursor.get(player.getName()) == null) {
					cursor.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Cursor: ")));
				}
				if (grandma.get(player.getName()) == null) {
					grandma.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Grandma: ")));
				}
				if (farm.get(player.getName()) == null) {
					farm.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Farm: ")));
				}
				if (factory.get(player.getName()) == null) {
					factory.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Factory: ")));
				}
				if (mine.get(player.getName()) == null) {
					mine.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Mine: ")));
				}
				if (shipment.get(player.getName()) == null) {
					shipment.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Shipment: ")));
				}
				if (alchemy.get(player.getName()) == null) {
					alchemy.put(player.getName(), objective
							.getScore(Bukkit.getServer().getOfflinePlayer(
									ChatColor.GREEN + "AlchemyLab: ")));
				}
				if (portal.get(player.getName()) == null) {
					portal.put(player.getName(), objective.getScore(Bukkit.getServer()
							.getOfflinePlayer(ChatColor.GREEN + "Portal: ")));
				}
				if (time.get(player.getName()) == null) {
					time.put(player.getName(), objective
							.getScore(Bukkit.getServer().getOfflinePlayer(
									ChatColor.GREEN + "TimeMachine: ")));
				}
				if (antimatter.get(player.getName()) == null) {
					antimatter.put(player.getName(), objective.getScore(Bukkit
							.getServer().getOfflinePlayer(
									ChatColor.GREEN + "Antimatter: ")));
				}

				if (!scoreBoard.contains(player.getName())) {
					scoreBoard.add(player.getName());
				}

				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
				player.setScoreboard(Bukkit.getServer().getScoreboardManager()
						.getNewScoreboard());
				if (scoreBoard.contains(player.getName())) {
					scoreBoard.remove(player.getName());
				}
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("prices")) {

				// get Prizes for cursos in a Integer
				long cursorPrize = Math.round(cursorCurrentPrize);
				String cursorSentPrize = new Long(cursorPrize).toString();

				// get Prizes for grandma in a Integer
				long grandmaPrize = Math.round(grandmaCurrentPrize);
				String grandmaSentPrize = new Long(grandmaPrize).toString();

				// get Prizes for farm in a Integer
				long farmPrize = Math.round(farmCurrentPrize);
				String farmSentPrize = new Long(farmPrize).toString();

				// get Prizes for factory in a Integer
				long factoryPrize = Math.round(factoryCurrentPrize);
				String factorySentPrize = new Long(factoryPrize).toString();

				// get Prizes for mine in a Integer
				long minePrize = Math.round(mineCurrentPrize);
				String mineSentPrize = new Long(minePrize).toString();

				// get Prizes for shipment in a Integer
				long shipmentPrize = Math.round(shipmentCurrentPrize);
				String shipmentSentPrize = new Long(shipmentPrize).toString();

				// get Prizes for alchemy in a Integer
				long alchemyPrize = Math.round(alchemyCurrentPrize);
				String alchemySentPrize = new Long(alchemyPrize).toString();

				// get Prizes for portal in a Integer
				long portalPrize = Math.round(portalCurrentPrize);
				String portalSentPrize = new Long(portalPrize).toString();

				// get Prizes for time machine in a Integer
				long timePrize = Math.round(timeCurrentPrize);
				String timeSentPrize = new Long(timePrize).toString();

				// get Prizes for antimatter in a Integer
				long antimatterPrize = antimatterCurrentPrize;
				String antimatterSentPrize = new Long(antimatterPrize)
						.toString();

				player.sendMessage("Cursor: " + cursorSentPrize.toString());
				player.sendMessage("Grandma: " + grandmaSentPrize.toString());
				player.sendMessage("Farm: " + farmSentPrize.toString());
				player.sendMessage("Factory: " + factorySentPrize.toString());
				player.sendMessage("Mine: " + mineSentPrize.toString());
				player.sendMessage("Shipment: " + shipmentSentPrize.toString());
				player.sendMessage("Alchemy Lab: "
						+ alchemySentPrize.toString());
				player.sendMessage("Portal: " + portalSentPrize.toString());
				player.sendMessage("Time machine: " + timeSentPrize.toString());
				player.sendMessage("Antimatter condenser: "
						+ antimatterSentPrize);
				return true;

			} else if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
				if (args[1].equalsIgnoreCase("cursor")) {
					if (cookie.get(player.getName()).getScore() >= cursorCurrentPrize) {
						long prize = Math.round(cursorCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						cursor.get(player.getName()).setScore(cursor.get(player.getName()).getScore() + 1);

						this.cursorCurrentPrize = Math
								.round(this.cursorCurrentPrize * growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Cursor");
						return true;
					} else {
						double prize = cursorCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("grandma")) {
					if (cookie.get(player.getName()).getScore() > grandmaCurrentPrize) {
						long prize = Math.round(grandmaCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						grandma.get(player.getName()).setScore(grandma.get(player.getName()).getScore() + 1);

						this.grandmaCurrentPrize = Math
								.round(this.grandmaCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Grandma");

						return true;
					} else {
						double prize = grandmaCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("farm")) {
					if (cookie.get(player.getName()).getScore() > farmCurrentPrize) {
						long prize = Math.round(farmCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						farm.get(player.getName()).setScore(farm.get(player.getName()).getScore() + 1);

						this.farmCurrentPrize = Math
								.round(this.farmCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Farm");

						return true;
					} else {
						double prize = farmCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("factory")) {
					if (cookie.get(player.getName()).getScore() > factoryCurrentPrize) {
						long prize = Math.round(factoryCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						factory.get(player.getName()).setScore(factory.get(player.getName()).getScore() + 1);

						this.factoryCurrentPrize = Math
								.round(this.factoryCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Factory");
						return true;

					} else {
						double prize = factoryCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("mine")) {
					if (cookie.get(player.getName()).getScore() > mineCurrentPrize) {
						long prize = Math.round(mineCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						mine.get(player.getName()).setScore(mine.get(player.getName()).getScore() + 1);

						this.mineCurrentPrize = Math
								.round(this.mineCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Mine");
						return true;

					} else {
						double prize = mineCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("shipment")) {
					if (cookie.get(player.getName()).getScore() > shipmentCurrentPrize) {
						long prize = Math.round(shipmentCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						shipment.get(player.getName()).setScore(shipment.get(player.getName()).getScore() + 1);

						this.shipmentCurrentPrize = Math
								.round(this.shipmentCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Shipment");
						return true;

					} else {
						double prize = shipmentCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("alchemylab")) {
					if (cookie.get(player.getName()).getScore() > alchemyCurrentPrize) {
						long prize = Math.round(alchemyCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						alchemy.get(player.getName()).setScore(alchemy.get(player.getName()).getScore() + 1);

						this.alchemyCurrentPrize = Math
								.round(this.alchemyCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.AlchemyLab");
						return true;

					} else {
						double prize = alchemyCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("portal")) {
					if (cookie.get(player.getName()).getScore() >= portalCurrentPrize) {
						long prize = Math.round(portalCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						portal.get(player.getName()).setScore(portal.get(player.getName()).getScore() + 1);

						this.portalCurrentPrize = Math
								.round(this.portalCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.Portal");

						return true;
					} else {
						double prize = portalCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("timemachine")) {
					if (cookie.get(player.getName()).getScore() > timeCurrentPrize) {
						long prize = Math.round(timeCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						time.get(player.getName()).setScore(time.get(player.getName()).getScore() + 1);

						this.timeCurrentPrize = Math
								.round(this.timeCurrentPrize * this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.TimeMachine");

						return true;

					} else {
						double prize = timeCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;
					}

				} else if (args[1].equalsIgnoreCase("antimatter")) {
					if (cookie.get(player.getName()).getScore() > antimatterCurrentPrize) {
						long prize = Math.round(antimatterCurrentPrize);
						int newPrize = (int) prize;
						cookie.get(player.getName()).setScore(cookie.get(player.getName()).getScore() - newPrize);
						antimatter.get(player.getName()).setScore(antimatter.get(player.getName()).getScore() + 1);

						this.antimatterCurrentPrize = Math
								.round(this.antimatterCurrentPrize
										* this.growth);

						// sets the overall cps
						this.cookiePerSecond += this.config
								.getDouble("Price.boost.AntimatterCondenser");

						return true;
					} else {
						double prize = antimatterCurrentPrize;
						Integer sentPrize = (int) prize;
						player.sendMessage("Not enough cookies! You need "
								+ sentPrize.toString());
						return true;

					}

				}
			}
		} else if (cmd.getName().equalsIgnoreCase("cookieclicker")
				&& !(sender instanceof Player)) {
			sender.sendMessage("This action can only be performed by a Player");
			return true;
		}
		return false;
	}

	@EventHandler
	public void onButtonPress(PlayerInteractEvent evt) {
		if (evt.getAction() == Action.RIGHT_CLICK_BLOCK
				&& evt.getClickedBlock().getType() == Material.STONE_BUTTON) {
			Player p = evt.getPlayer();
			if (scoreBoard.contains(p.getName())) {
				cookie.get(p.getName()).setScore(cookie.get(p.getName()).getScore() + 1);
			}
		}
	}

}
