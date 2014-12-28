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
package org.primesoft.midiregion.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.primesoft.midiregion.ConfigProvider;
import org.primesoft.midiregion.MidiRegionMain;
import org.primesoft.midiregion.VersionChecker;

/**
 * Reload configuration command
 * @author SBPrime
 */
public class ReloadCommand extends BaseCommand {

    private final MidiRegionMain m_pluginMain;

    public ReloadCommand(MidiRegionMain pluginMain) {
        m_pluginMain = pluginMain;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String name, String[] args) {
        if (args != null && args.length > 0) {
            return false;
        }

        Player player = (cs instanceof Player) ? (Player) cs : null;

        m_pluginMain.reloadConfig();
        ReloadConfig(player);
        return true;
    }

    public boolean ReloadConfig(Player player) {
        if (!ConfigProvider.load(m_pluginMain)) {
            MidiRegionMain.say(player, "Error loading config");
            return false;
        }

        if (ConfigProvider.getCheckUpdate()) {
            MidiRegionMain.log(VersionChecker.CheckVersion(m_pluginMain.getVersion()));
        }
        if (!ConfigProvider.isConfigUpdated()) {
            MidiRegionMain.log("Please update your config file!");
        }

        MidiRegionMain.say(player, "Config loaded");
        return true;
    }
}
