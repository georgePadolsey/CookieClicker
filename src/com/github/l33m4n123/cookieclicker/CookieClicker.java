package com.github.l33m4n123.cookieclicker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.github.l33m4n123.cookieclicker.config.ConfigWriter;

import code.husky.mysql.MySQL;

/**
 * @author: Leeman;
 */
public class CookieClicker extends JavaPlugin implements Listener {

	// Some declarations that I am going to need

	protected ConfigWriter config = new ConfigWriter();

	private String host;
	private String port;
	private String database;
	private String user;
	private String password;

	private Plugin plugin = this;
	private MySQL mySQL;
	private Connection c = null;

	private double growth;

	ScoreboardManager manager;
	Scoreboard board;

	private static HashSet<String> scoreBoard = new HashSet<String>();
	private HashMap<String, Score> cookie = new HashMap<String, Score>();
	private HashMap<String, Score> cursors = new HashMap<String, Score>();
	private HashMap<String, Score> grandma = new HashMap<String, Score>();
	private HashMap<String, Score> farm = new HashMap<String, Score>();
	private HashMap<String, Score> factory = new HashMap<String, Score>();
	private HashMap<String, Score> mine = new HashMap<String, Score>();
	private HashMap<String, Score> shipment = new HashMap<String, Score>();
	private HashMap<String, Score> alchemy = new HashMap<String, Score>();
	private HashMap<String, Score> portal = new HashMap<String, Score>();
	private HashMap<String, Score> time = new HashMap<String, Score>();
	private HashMap<String, Score> antimatter = new HashMap<String, Score>();

	static HashSet<String> score = new HashSet<String>();

	@Override
	public void onEnable() {
		// TODO Insert logic to be performed when the plugin is enabled
		manager = Bukkit.getScoreboardManager();
		getServer().getPluginManager().registerEvents(this, this);
		config.configWriter(this);

		this.host = config.getString("Database.host");
		this.port = config.getString("Database.port");
		this.database = config.getString("Database.database");
		this.user = config.getString("Database.user");
		this.password = config.getString("Database.password");

		mySQL = new MySQL(this.plugin, this.host, this.port, this.database,
				this.user, this.password);
		c = mySQL.openConnection();

		this.growth = Double.parseDouble(config.getString("Price.growth"));

		try {
			Statement statement = c.createStatement();
			statement
					.executeUpdate("CREATE TABLE IF NOT EXISTS"
							+ " `CookieClicker`"
							+ "(`Name` varchar(28),"
							+ " `cookies` varchar(28), `cursors` varchar(28),"
							+ "`grandma` varchar(28), `farm` varchar(28),"
							+ "`factory` varchar(28), `mine` varchar(28),"
							+ "`shipment` varchar(28), `alchemy` varchar(28),"
							+ "`portal` varchar(28), `timeMachine` varchar(28), `antimatter` varchar(28),"
							+ "`cursorPrice` varchar(28),"
							+ "`grandmaPrice` varchar(28), `farmPrice` varchar(28),"
							+ "`factoryPrice` varchar(28), `minePrice` varchar(28),"
							+ "`shipmentPrice` varchar(28), `alchemyPrice` varchar(28),"
							+ "`portalPrice` varchar(28), `timeMachinePrice` varchar(28), `antimatterPrice` varchar(28),"
							+ "`cookiePerSeconds` varchar(28),"
							+ "PRIMARY KEY (`cookies`), UNIQUE KEY `Name` (`Name`))");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookieCheck();

	}

	// Adds the player into the database
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent evt) {
		final Player player = evt.getPlayer();
		this.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new BukkitRunnable() {

					@Override
					public void run() {
						player.setScoreboard(Bukkit.getServer()
								.getScoreboardManager().getNewScoreboard());

					}
				}, 1L);

		try {
			playerWriter(evt.getPlayer().getName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		player.setScoreboard(Bukkit.getServer().getScoreboardManager()
				.getNewScoreboard());
		if (scoreBoard.contains(player.getName())) {
			scoreBoard.remove(player.getName());
		}
	}

	public void cookieCheck() {
		this.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, new BukkitRunnable() {

					public void run() {
						// Do stuff

						for (String playerName : CookieClicker.scoreBoard) {

							Statement statement;
							String cookiesString = "";
							double cookiePerSecond = 0;

							// Get current CPS and current cookies from Database
							// to give cookies
							try {
								statement = c.createStatement();
								String querySelectCPS = "SELECT `cookiePerSeconds` FROM `CookieClicker` WHERE `Name`='"
										+ playerName + "'";
								ResultSet rs = statement
										.executeQuery(querySelectCPS);
								rs.next();
								String cookiePerSecondString = rs.getString(1);
								cookiePerSecond = Double
										.parseDouble(cookiePerSecondString);
								// get current cookies
								String querySelectCookies = "SELECT `cookies` FROM `CookieClicker` WHERE `Name`='"
										+ playerName + "'";
								ResultSet res = statement
										.executeQuery(querySelectCookies);
								res.next();

								cookiesString = res.getString(1);
							} catch (SQLException e) {
								e.printStackTrace();
							}

							if (cookiePerSecond > 0.00d) {

								long cookies = Long.parseLong(cookiesString);
								int roundCps = (int) Math
										.round(cookiePerSecond * 10);

								int newScore = (int) cookies + roundCps;
								cookie.get(playerName).setScore(newScore);

								// Update Database
								String queryUpdate = "UPDATE `CookieClicker` SET `cookies` = '"
										+ String.valueOf(newScore)
										+ "' WHERE `Name` ='"
										+ playerName
										+ "'";
								try {
									statement = c.createStatement();
									statement.executeUpdate(queryUpdate);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
					}
				}, 200L, 200L);
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
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("leaderboard")) {
					Statement leaderboardStatement;
					try {
						leaderboardStatement = c.createStatement();

						String querySelectCookies = "SELECT `Name`, `cookies` FROM `CookieClicker` ORDER BY `cookies` DESC";
						ResultSet cookieResult = leaderboardStatement
								.executeQuery(querySelectCookies);
						int i = 0;
						while (cookieResult.next()) {
							i++;
							if (i > 5) {
								break;
							}
							player.sendMessage(ChatColor.GREEN + "" + i + ". "
									+ ChatColor.GOLD
									+ cookieResult.getString(1) + ""
									+ ChatColor.AQUA + " has: " + ChatColor.RED
									+ "" + cookieResult.getString(2) + ""
									+ ChatColor.WHITE + " Cookies");
						}

						return true;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (args[0].equalsIgnoreCase("start")) {

					board = manager.getNewScoreboard();

					int cookies = 0;
					int cursor = 0;
					int grandma = 0;
					int farm = 0;
					int factory = 0;
					int mine = 0;
					int shipment = 0;
					int alchemy = 0;
					int portal = 0;
					int timeMachine = 0;
					int antimatterInt = 0;

					Statement cookieStatement;
					Statement cursorStatement;
					Statement grandmaStatement;
					Statement farmStatement;
					Statement factoryStatement;
					Statement mineStatement;
					Statement shipmentStatement;
					Statement alchemyStatement;
					Statement portalStatement;
					Statement timeMachineStatement;
					Statement antimatterStatement;
					try {
						cookieStatement = c.createStatement();
						cursorStatement = c.createStatement();
						grandmaStatement = c.createStatement();
						farmStatement = c.createStatement();
						factoryStatement = c.createStatement();
						mineStatement = c.createStatement();
						shipmentStatement = c.createStatement();
						alchemyStatement = c.createStatement();
						portalStatement = c.createStatement();
						timeMachineStatement = c.createStatement();
						antimatterStatement = c.createStatement();

						String querySelectCookies = "SELECT `cookies` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectCursor = "SELECT `cursors` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectGrandma = "SELECT `grandma` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectFarm = "SELECT `farm` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectFactory = "SELECT `factory` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectMine = "SELECT `mine` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectShipment = "SELECT `shipment` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectAlchemy = "SELECT `alchemy` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectPortal = "SELECT `portal` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectTime = "SELECT `timeMachine` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";
						String querySelectAntimatter = "SELECT `antimatter` FROM `CookieClicker` WHERE `Name`='"
								+ player.getName() + "'";

						ResultSet cookieResult = cookieStatement
								.executeQuery(querySelectCookies);
						cookieResult.next();
						ResultSet cursorResult = cursorStatement
								.executeQuery(querySelectCursor);
						cursorResult.next();
						ResultSet grandmaResult = grandmaStatement
								.executeQuery(querySelectGrandma);
						grandmaResult.next();
						ResultSet farmResult = farmStatement
								.executeQuery(querySelectFarm);
						farmResult.next();
						ResultSet factoryResult = factoryStatement
								.executeQuery(querySelectFactory);
						factoryResult.next();
						ResultSet mineResult = mineStatement
								.executeQuery(querySelectMine);
						mineResult.next();
						ResultSet shipmentResult = shipmentStatement
								.executeQuery(querySelectShipment);
						shipmentResult.next();
						ResultSet alchemyResult = alchemyStatement
								.executeQuery(querySelectAlchemy);
						alchemyResult.next();
						ResultSet portalResult = portalStatement
								.executeQuery(querySelectPortal);
						portalResult.next();
						ResultSet timeMachineResult = timeMachineStatement
								.executeQuery(querySelectTime);
						timeMachineResult.next();
						ResultSet antimatterResult = antimatterStatement
								.executeQuery(querySelectAntimatter);
						antimatterResult.next();

						String cookieString = cookieResult.getString(1);
						String cursorString = cursorResult.getString(1);
						String grandmaString = grandmaResult.getString(1);
						String farmString = farmResult.getString(1);
						String factoryString = factoryResult.getString(1);
						String mineString = mineResult.getString(1);
						String shipmentString = shipmentResult.getString(1);
						String alchemyString = alchemyResult.getString(1);
						String portalString = portalResult.getString(1);
						String timeMachineString = timeMachineResult
								.getString(1);
						String antimatterString = antimatterResult.getString(1);

						cookies = Integer.parseInt(cookieString);
						cursor = Integer.parseInt(cursorString);
						grandma = Integer.parseInt(grandmaString);
						farm = Integer.parseInt(farmString);
						factory = Integer.parseInt(factoryString);
						mine = Integer.parseInt(mineString);
						shipment = Integer.parseInt(shipmentString);
						alchemy = Integer.parseInt(alchemyString);
						portal = Integer.parseInt(portalString);
						timeMachine = Integer.parseInt(timeMachineString);
						antimatterInt = Integer.parseInt(antimatterString);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (!scoreBoard.contains(player.getName())) {
						scoreBoard.add(player.getName());
					}

					Objective objective;
					if (board.getObjective(player.getName()) == null) {
						objective = board.registerNewObjective(
								player.getName(), "dummy");
					} else {
						objective = board.getObjective(player.getName());
					}
					objective.setDisplaySlot(DisplaySlot.SIDEBAR);
					objective.setDisplayName(player.getName());
					scoreSet(player);
					this.cookie.get(player.getName()).setScore(cookies);
					this.cursors.get(player.getName()).setScore(cursor);
					this.grandma.get(player.getName()).setScore(grandma);
					this.farm.get(player.getName()).setScore(farm);
					this.factory.get(player.getName()).setScore(factory);
					this.mine.get(player.getName()).setScore(mine);
					this.shipment.get(player.getName()).setScore(shipment);
					this.alchemy.get(player.getName()).setScore(alchemy);
					this.portal.get(player.getName()).setScore(portal);
					this.time.get(player.getName()).setScore(timeMachine);
					this.antimatter.get(player.getName()).setScore(
							antimatterInt);
					player.setScoreboard(board);

					return true;

				} else if (args[0].equalsIgnoreCase("stop")) {
					player.setScoreboard(Bukkit.getServer()
							.getScoreboardManager().getNewScoreboard());
					if (scoreBoard.contains(player.getName())) {
						scoreBoard.remove(player.getName());
					}
					return true;
				} else if (args[0].equalsIgnoreCase("prices")) {

					String cursorCurrentPrice = getPrice("cursor", player);
					String grandmaCurrentPrice = getPrice("grandma", player);
					String farmCurrentPrice = getPrice("farm", player);
					String factoryCurrentPrice = getPrice("factory", player);
					String mineCurrentPrice = getPrice("mine", player);
					String shipmentCurrentPrice = getPrice("shipment", player);
					String alchemyCurrentPrice = getPrice("alchemy", player);
					String portalCurrentPrice = getPrice("portal", player);
					String timeCurrentPrice = getPrice("time", player);
					String antimatterCurrentPrice = getPrice("antimatter",
							player);

					player.sendMessage(ChatColor.GREEN + "Cursor: "
							+ ChatColor.RED + "" + cursorCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Grandma: "
							+ ChatColor.RED + "" + grandmaCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Farm: " + ChatColor.RED + "" + farmCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Factory: "
							+ ChatColor.RED + "" + factoryCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Mine: " + ChatColor.RED + "" + mineCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Shipment: "
							+ ChatColor.RED + "" + shipmentCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Alchemy Lab: "
							+ ChatColor.RED + "" + alchemyCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Portal: "
							+ ChatColor.RED + "" +portalCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Time machine: "
							+ ChatColor.RED + "" + timeCurrentPrice.toString());
					player.sendMessage(ChatColor.GREEN + "Antimatter condenser: "
							+ ChatColor.RED + "" + antimatterCurrentPrice);
					return true;

				} else if (args[0].equalsIgnoreCase("leaderboard")) {
					/**
					 * Print a Leaderboard with the top 10 Players + their
					 * cookies
					 */
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("buy")) {
					String cps = "";
					Statement getCps;

					String getCpsFromDB = "SELECT `cookiePerSeconds` FROM `CookieClicker` WHERE `Name`='"
							+ player.getName() + "'";
					ResultSet getCurrentCPS;

					try {
						getCps = c.createStatement();

						getCurrentCPS = getCps.executeQuery(getCpsFromDB);
						getCurrentCPS.next();

						cps = getCurrentCPS.getString(1);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (args[1].equalsIgnoreCase("cursor")) {

						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("cursor", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"cursor", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							cursors.get(player.getName())
									.setScore(
											cursors.get(player.getName())
													.getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Cursor"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `cursorPrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCursorsStatement;
							String updateCursors = "UPDATE `CookieClicker` SET `cursors` ='"
									+ String.valueOf(cursors.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateCursorsStatement = c.createStatement();
								updateCursorsStatement
										.executeUpdate(updateCursors);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}

					} else if (args[1].equalsIgnoreCase("grandma")) {

						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("grandma", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"grandma", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							grandma.get(player.getName())
									.setScore(
											grandma.get(player.getName())
													.getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Grandma"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `grandmaPrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `grandma` ='"
									+ String.valueOf(grandma.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}

					} else if (args[1].equalsIgnoreCase("farm")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("farm", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"farm", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							farm.get(player.getName()).setScore(
									farm.get(player.getName()).getScore() + 1);

							long updatedFarmPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Farm"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setFarmPrice = "UPDATE `CookieClicker` SET `farmPrice` ='"
									+ String.valueOf(updatedFarmPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `farm` ='"
									+ String.valueOf(farm.get(player.getName())
											.getScore())
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setFarmPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}

					} else if (args[1].equalsIgnoreCase("factory")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("factory", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"factory", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							factory.get(player.getName())
									.setScore(
											factory.get(player.getName())
													.getScore() + 1);

							long updatedFarmPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Factory"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setFarmPrice = "UPDATE `CookieClicker` SET `factoryPrice` ='"
									+ String.valueOf(updatedFarmPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `factory` ='"
									+ String.valueOf(farm.get(player.getName())
											.getScore())
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setFarmPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}
					} else if (args[1].equalsIgnoreCase("shipment")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("shipment", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"shipment", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							shipment.get(player.getName())
									.setScore(
											shipment.get(player.getName())
													.getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Shipment"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `shipmentPrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `shipment` ='"
									+ String.valueOf(grandma.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}
					} else if (args[1].equalsIgnoreCase("alchemylab")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("alchemy", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"alchemy", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							alchemy.get(player.getName())
									.setScore(
											grandma.get(player.getName())
													.getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.AlchemyLab"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `alchemyPrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `alchemy` ='"
									+ String.valueOf(grandma.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}

					} else if (args[1].equalsIgnoreCase("portal")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("portal", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"portal", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							portal.get(player.getName())
									.setScore(
											portal.get(player.getName())
													.getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Portal"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `portalPrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `portal` ='"
									+ String.valueOf(grandma.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}

					} else if (args[1].equalsIgnoreCase("timemachine")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("time", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"time", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							time.get(player.getName()).setScore(
									time.get(player.getName()).getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.TimeMachine"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `timeMachinePrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `timeMachine` ='"
									+ String.valueOf(grandma.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}
					} else if (args[1].equalsIgnoreCase("antimatter")) {
						if (cookie.get(player.getName()).getScore() >= Integer
								.parseInt(getPrice("antimatter", player))) {
							long prize = Math.round(Integer.parseInt(getPrice(
									"antimatter", player)));
							int newPrice = (int) prize;
							cookie.get(player.getName()).setScore(
									cookie.get(player.getName()).getScore()
											- newPrice);
							antimatter.get(player.getName())
									.setScore(
											grandma.get(player.getName())
													.getScore() + 1);

							long updatedCursorPrice = Math.round(newPrice
									* growth);

							// sets the overall cps
							double newCpsInt = Double.parseDouble(this.config
									.getString("Price.boost.Antimatter"))
									+ Double.parseDouble(cps);

							String newCps = String.valueOf(newCpsInt);

							Statement setNewCPS;
							String setCPS = "UPDATE `CookieClicker` SET `cookiePerSeconds` =' "
									+ newCps
									+ "' WHERE `Name` = '"
									+ player.getName() + "'";

							Statement newCursorPrice;
							String setCursorPrice = "UPDATE `CookieClicker` SET `antimatterPrice` ='"
									+ String.valueOf(updatedCursorPrice)
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateGrandmaStatement;
							String updateGrandma = "UPDATE `CookieClicker` SET `antimatter` ='"
									+ String.valueOf(grandma.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";

							Statement updateCookieStatement;
							String updateCookie = "UPDATE `CookieClicker` SET `cookies` ='"
									+ String.valueOf(cookie.get(
											player.getName()).getScore())
									+ "' WHERE `Name` = '"
									+ player.getName()
									+ "'";
							try {
								setNewCPS = c.createStatement();
								setNewCPS.executeUpdate(setCPS);

								newCursorPrice = c.createStatement();
								newCursorPrice.executeUpdate(setCursorPrice);

								updateGrandmaStatement = c.createStatement();
								updateGrandmaStatement
										.executeUpdate(updateGrandma);

								updateCookieStatement = c.createStatement();
								updateCookieStatement
										.executeUpdate(updateCookie);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return true;
						} else {
							player.sendMessage("Not enough cookies!");
							return true;
						}

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
				cookie.get(p.getName()).setScore(
						cookie.get(p.getName()).getScore() + 1);

				// Save changes in the Database
				Statement statement;
				try {
					statement = c.createStatement();
					String querySelectCookies = "UPDATE `CookieClicker` set `cookies` ='"
							+ String.valueOf(cookie.get(p.getName()).getScore())
							+ "' WHERE `Name`='" + p.getName() + "'";
					statement.executeUpdate(querySelectCookies);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public void scoreSet(Player player) {
		Objective objective;
		if (board.getObjective(player.getName()) == null) {
			objective = board.registerNewObjective(player.getName(), "dummy");
		} else {
			objective = board.getObjective(player.getName());
		}

		player.setScoreboard(board);
		String name = player.getName();
		objective.setDisplayName("CookieClicker");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		cookie.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Cookies: ")));
		cursors.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Cursor: ")));
		grandma.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Grandma: ")));
		farm.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Farm: ")));
		factory.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Factory: ")));
		mine.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Mine: ")));
		shipment.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Shipment: ")));
		alchemy.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "AlchemyLab: ")));
		portal.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Portal: ")));
		time.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "TimeMachine: ")));
		antimatter.put(
				name,
				objective.getScore(Bukkit.getServer().getOfflinePlayer(
						ChatColor.GREEN + "Antimatter: ")));

	}

	public void playerWriter(String name) throws SQLException {
		// replace with SQL Query
		Statement statement = c.createStatement();

		String querySelect = "SELECT `Name` FROM `CookieClicker` WHERE `Name`='"
				+ name + "'";
		ResultSet rs = statement.executeQuery(querySelect);
		if (!rs.next()) {

			String cursorPrice = config.getString("Cookie.prices.Cursor");
			String grandmaPrice = config.getString("Cookie.prices.Grandma");
			String famrPrice = config.getString("Cookie.prices.Farm");
			String factoryPrice = config.getString("Cookie.prices.Factory");
			String minePrice = config.getString("Cookie.prices.Mine");
			String shipmentPrice = config.getString("Cookie.prices.Shipment");
			String alchemyPrice = config.getString("Cookie.prices.AlchemyLab");
			String portalPrice = config.getString("Cookie.prices.Portal");
			String timeMachinePrice = config
					.getString("Cookie.prices.TimeMachine");
			String antimatterPrice = config
					.getString("Cookie.prices.AntimatterCondenser");

			String queryInsert = "INSERT INTO `CookieClicker` (`Name`,"
					+ "`cookies`, `cursors`, `grandma`, `farm`,"
					+ "`factory`, `mine`, `shipment`, `alchemy`,"
					+ "`portal`, `timeMachine`, `antimatter`,"
					+ " `cursorPrice`, `grandmaPrice`, `farmPrice`,"
					+ "`factoryPrice`, `minePrice`, `shipmentPrice`,"
					+ "`alchemyPrice`, `portalPrice`,"
					+ "`timeMachinePrice`, `antimatterPrice`, `cookiePerSeconds`)"
					+ "Values" + "('"
					+ name
					+ "',"
					+ "'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', "
					+ "'"
					+ cursorPrice
					+ "', "
					+ "'"
					+ grandmaPrice
					+ "', "
					+ "'"
					+ famrPrice
					+ "', "
					+ "'"
					+ factoryPrice
					+ "', "
					+ "'"
					+ minePrice
					+ "', "
					+ "'"
					+ shipmentPrice
					+ "', "
					+ "'"
					+ alchemyPrice
					+ "', "
					+ "'"
					+ portalPrice
					+ "', "
					+ "'"
					+ timeMachinePrice
					+ "', "
					+ "'"
					+ antimatterPrice
					+ "', " + "'0');";
			statement.executeUpdate(queryInsert);
			System.out.println("Player did not exist. Added to Database");
		}
	}

	public void leaderBoardWriter(String name) {
		// replace with sql query

		// String[] header = { "This File", "stores the stats",
		// "required for the leaderboard" };
		//
		// this.configManager = new SimpleConfigManager(this);
		// this.leaderBoard = configManager.getNewConfig(
		// "players/leaderBoard.cookie", header);
		//
		// this.leaderBoard.set(name + ".Cookies", 0);
		//
		// this.leaderBoard.saveConfig();
	}

	public String getPrice(String name, Player player) {
		Statement cursor;
		Statement grandma;
		Statement farm;
		Statement factory;
		Statement mine;
		Statement shipment;
		Statement alchemy;
		Statement portal;
		Statement time;
		Statement antimatter;

		if (name.equalsIgnoreCase("cursor")) {
			String cursorGetCurrentPrice = "SELECT `cursorPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getCursorPrice;

			try {
				cursor = c.createStatement();

				getCursorPrice = cursor.executeQuery(cursorGetCurrentPrice);
				getCursorPrice.next();

				return getCursorPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (name.equalsIgnoreCase("grandma")) {
			String grandmaGetCurrentPrice = "SELECT `grandmaPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getGrandmaPrice;
			try {
				grandma = c.createStatement();
				getGrandmaPrice = grandma.executeQuery(grandmaGetCurrentPrice);
				getGrandmaPrice.next();
				return getGrandmaPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("farm")) {

			String farmGetCurrentPrice = "SELECT `farmPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getFarmPrice;
			try {
				farm = c.createStatement();
				getFarmPrice = farm.executeQuery(farmGetCurrentPrice);
				getFarmPrice.next();

				return getFarmPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("factory")) {

			String factoryGetCurrentPrice = "SELECT `factoryPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getFactoryPrice;
			try {
				factory = c.createStatement();
				getFactoryPrice = factory.executeQuery(factoryGetCurrentPrice);
				getFactoryPrice.next();

				return getFactoryPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("mine")) {
			String mineGetCurrentPrice = "SELECT `minePrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getMinePrice;
			try {
				mine = c.createStatement();
				getMinePrice = mine.executeQuery(mineGetCurrentPrice);
				getMinePrice.next();

				return getMinePrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("shipment")) {

			String shipmentGetCurrentPrice = "SELECT `shipmentPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getShipmentPrice;
			try {
				shipment = c.createStatement();
				getShipmentPrice = shipment
						.executeQuery(shipmentGetCurrentPrice);
				getShipmentPrice.next();

				return getShipmentPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("alchemy")) {

			String alchemyGetCurrentPrice = "SELECT `alchemyPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getAlchemyPrice;
			try {
				alchemy = c.createStatement();
				getAlchemyPrice = alchemy.executeQuery(alchemyGetCurrentPrice);
				getAlchemyPrice.next();

				return getAlchemyPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("portal")) {

			String portalGetCurrentPrice = "SELECT `portalPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getPortalPrice;
			try {
				portal = c.createStatement();
				getPortalPrice = portal.executeQuery(portalGetCurrentPrice);
				getPortalPrice.next();

				return getPortalPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("time")) {

			String timeGetCurrentPrice = "SELECT `timeMachinePrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getTimePrice;
			try {
				time = c.createStatement();
				getTimePrice = time.executeQuery(timeGetCurrentPrice);
				getTimePrice.next();

				return getTimePrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (name.equalsIgnoreCase("antimatter")) {

			String antimatterGetCurrentPrice = "SELECT `antimatterPrice` FROM `CookieClicker` WHERE `Name`='"
					+ player.getName() + "'";
			ResultSet getAntimatterPrice;
			try {
				antimatter = c.createStatement();
				getAntimatterPrice = antimatter
						.executeQuery(antimatterGetCurrentPrice);
				getAntimatterPrice.next();

				return getAntimatterPrice.getString(1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "";
	}

}
