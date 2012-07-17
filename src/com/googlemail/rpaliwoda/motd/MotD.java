package com.googlemail.rpaliwoda.motd;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

// TO-DO
// Add colour support. Does it already have this, with Minecraft's colour codes?
// Allow user to set colour of 'New MotD' and 'MotD' by command - stow the &-code in config.yml.

public class MotD extends JavaPlugin implements Listener
{
	Logger log;
	String motd = "";
	String[] motdLines;
	
	public void onEnable()
	{
		log = this.getLogger();
		getServer().getPluginManager().registerEvents(this, this);
		log.info("MotD plugin has been enabled.");
		getConfig();
		getMotD();
	}
	
	public void onDisable()
	{
		this.saveConfig();
		log.info("MotD plugin has been disabled.");
	}
	
	@EventHandler // EventPriority.NORMAL by default
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Display a message on player login.
		// display motd String retrieved earlier
		Player player = event.getPlayer();
		for(int i = 0; i < motdLines.length; i++)
		{
			player.sendMessage(motdLines);
		}
		//log.info(player.getName() + " logged in; MotD sent");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player player = null;
		if (sender instanceof Player)
		{
			player = (Player) sender;
		}
		
		if(cmd.getName().equalsIgnoreCase("motd"))
		{
			if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("set"))
				{
					if(player != null)
					{
					    if(player.hasPermission("motd.admins"))
					    {
							setMotD(args);
					    }
					    else
					    {
					        // error message re: permissions
					    	player.sendMessage("You do not have permission to set the MotD.");
					    }
					}
					else
					{
						setMotD(args);
					}
				}
				if(args[0].equalsIgnoreCase("get"))
				{
					if(player != null)
						for(int i = 0; i < motdLines.length; i++)
						{
							player.sendMessage(motdLines[i]);
						}
					else
						for(int i = 0; i < motdLines.length; i++)
						{
							log.info(motdLines[i]);
						}
				}
				if(args[0].equalsIgnoreCase("broadcast"))
				{
					if(player != null)
						if(player.hasPermission("motd.admins"))
						{
							for(int i = 0; i < motdLines.length; i++)
							{
								getServer().broadcastMessage(motdLines[i]);
							}
						}
						else
							player.sendMessage("You do not have permission to broadcast the MotD.");
					else
						for(int i = 0; i < motdLines.length; i++)
						{
							getServer().broadcastMessage(motdLines[i]);
						}
				}
			}
			else
			{
				// help text
				if(player != null)
				{
					
				}
				else
				{
					
				}
			}

			return true;
		}
		return false;
	}
	
	public void setMotD(String[] args)
	{
		// reset old MotD stuff.
		if(this.getConfig().getConfigurationSection("motd") != null)
		{
			Set<String> subKeys = this.getConfig().getConfigurationSection("motd").getKeys(false);
			Iterator<String> s = subKeys.iterator();
			
			do
			{
				this.getConfig().set(("motd." + (String) s.next()), null);
			}while(s.hasNext());
		}
		motdLines = null;

    	// accept new motd
		for(int i = 1; i < args.length; i++)
		{
			if(i != 1)
				motd = motd + " " + args[i];
			else
				motd = args[i];
		}
		
		//split MotD on \n
		//put into config.yml as line 0, line1, line2, line3 ...
		motdLines = motd.split("\\\\n");
		
		for(int i = 0; i < motdLines.length; i++)
		{
			this.getConfig().set("motd.line"+i,motdLines[i]);
			getServer().broadcastMessage(motdLines[i]);
		}
		
		this.saveConfig();
	}
	
	public void getMotD()
	{
		if(this.getConfig().getConfigurationSection("motd") != null)
		{
			Set<String> subKeys = this.getConfig().getConfigurationSection("motd").getKeys(false);
			Iterator<String> s = subKeys.iterator();
			
			do
			{
				motd += this.getConfig().getString("motd." + (String) s.next()) + "\n";
			}while(s.hasNext());
			
			motdLines = motd.split("\\\\n");
		}
	}
}
