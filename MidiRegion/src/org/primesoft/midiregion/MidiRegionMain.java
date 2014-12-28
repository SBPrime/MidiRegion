/*
 * MidiRegion a plugin that allows you to set a custom musin for WorldGuard region.
 * Copyright (c) 2014, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) MidiRegion contributors
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted free of charge provided that the following 
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution,
 * 3. Redistributions of source code, with or without modification, in any form 
 *    other then free of charge is not allowed,
 * 4. Redistributions in binary form in any form other then free of charge is 
 *    not allowed.
 * 5. Any derived work based on or containing parts of this software must reproduce 
 *    the above copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided with the 
 *    derived work.
 * 6. The original author of the software is allowed to change the license 
 *    terms or the entire license of the software as he sees fit.
 * 7. The original author of the software is allowed to sublicense the software 
 *    or its parts using any license terms he sees fit.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.primesoft.midiregion;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.midiplayer.MidiPlayerMain;
import org.primesoft.midiplayer.mcstats.MetricsLite;
import org.primesoft.midiregion.commands.ReloadCommand;
import org.primesoft.midiregion.commands.TestCommand;

/**
 *
 * @author SBPrime
 */
public class MidiRegionMain extends JavaPlugin {

    private static final Logger s_log = Logger.getLogger("Minecraft.MidiRegion");
    private static String s_prefix = null;
    private static final String s_logFormat = "%s %s";

    /**
     * The instance of the class
     */
    private static MidiRegionMain s_instance;

    /**
     * Send message to the log
     *
     * @param msg
     */
    public static void log(String msg) {
        if (s_log == null || msg == null || s_prefix == null) {
            return;
        }

        s_log.log(Level.INFO, String.format(s_logFormat, s_prefix, msg));
    }

    /**
     * Sent message directly to player
     *
     * @param player
     * @param msg
     */
    public static void say(Player player, String msg) {
        if (player == null) {
            log(msg);
        } else {
            player.sendRawMessage(msg);
        }
    }

    /**
     * The instance of the class
     *
     * @return
     */
    public static MidiRegionMain getInstance() {
        return s_instance;
    }

    /**
     * Metrics
     */
    private MetricsLite m_metrics;

    /**
     * The plugin version
     */
    private String m_version;

    /**
     * The reload command handler
     */
    private ReloadCommand m_reloadCommandHandler;

    /**
     * Instance of WorldGuard
     */
    private WorldGuardPlugin m_worldGuard;

    /**
     * Instance of MidiPlayer
     */
    private MidiPlayerMain m_midiPlayer;

    /**
     * Is the plugin enabled
     */
    private boolean m_isInitialized;

    public WorldGuardPlugin getWorldGuard() {
        return m_worldGuard;
    }

    public MidiPlayerMain getMidiPlayer() {
        return m_midiPlayer;
    }

    public String getVersion() {
        return m_version;
    }

    public boolean isPluginInitialized() {
        return m_isInitialized;
    }

    @Override
    public void onEnable() {
        m_isInitialized = false;
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            if (!metrics.isOptOut()) {
                m_metrics = metrics;
                m_metrics.start();
            }
        } catch (IOException e) {
            log("Error initializing MCStats: " + e.getMessage());
        }

        PluginDescriptionFile desc = getDescription();
        s_prefix = String.format("[%s]", desc.getName());
        s_instance = this;

        m_version = desc.getVersion();
        InitializeCommands();

        if (!m_reloadCommandHandler.ReloadConfig(null)) {
            log("Error loading config");
            return;
        }

        m_worldGuard = Utils.getPlugin(this, "WorldGuard", WorldGuardPlugin.class);
        m_midiPlayer = Utils.getPlugin(this, "MidiPlayer", MidiPlayerMain.class);

        if (m_worldGuard == null ||
                m_midiPlayer == null) {
            return;
        }
        
        m_isInitialized = true;
        super.onEnable();
    }

    /**
     * Initialize the commands
     *
     * @return
     */
    private void InitializeCommands() {
        m_reloadCommandHandler = new ReloadCommand(this);
        TestCommand testCommandHandler = new TestCommand(this);

        PluginCommand commandReload = getCommand("mrreload");
        commandReload.setExecutor(m_reloadCommandHandler);

        PluginCommand commandtest = getCommand("test");
        commandtest.setExecutor(testCommandHandler);
    }
}
