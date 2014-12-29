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
package org.primesoft.midiregion.flag;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.primesoft.midiregion.utils.FormatException;

/**
 *
 * @author SBPrime
 */
public class MusicFlag extends Flag<MusicData> {

    private final static String MUSIC_FLAG = "Midi-Music";

    private final static MusicFlag s_instance = new MusicFlag();

    public static MusicFlag getInstance() {
        return s_instance;
    }

    public MusicFlag() {
        super(MUSIC_FLAG);
    }

    @Override
    public MusicData parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat {
        input = input.trim();

        final String[] split = input.split(",");
        final int cnt = split.length;
        String fileName = null;
        boolean loop = false;
        boolean reset = false;

        if (cnt > 0) {
            fileName = split[0];
        }
        try {
            if (cnt > 1) {
                loop = parseBoolean(split[1]);
            }
            if (cnt > 2) {
                reset = parseBoolean(split[2]);
            }
        } catch (FormatException ex) {
            throw new InvalidFlagFormat("Expected midiFile or midiFile,loop or midiFile,loop,reset");
        }

        if (fileName == null || fileName.isEmpty()) {
            throw new InvalidFlagFormat("Expected midiFile or midiFile,loop or midiFile,loop,reset");
        }

        return new MusicData(loop, reset, fileName);
    }

    /**
     * Try to parse the string to boolean
     *
     * @param s
     * @return
     * @throws FormatException
     */
    private static Boolean parseBoolean(String s) throws FormatException {
        s = (s == null ? "" : s).trim();

        if ("on".equalsIgnoreCase(s)) {
            return true;
        }
        if ("true".equalsIgnoreCase(s)) {
            return true;
        }
        if ("1".equalsIgnoreCase(s)) {
            return true;
        }
        if ("0".equalsIgnoreCase(s)) {
            return false;
        }
        if ("false".equalsIgnoreCase(s)) {
            return false;
        }
        if ("off".equalsIgnoreCase(s)) {
            return false;
        }

        throw new FormatException();
    }

    /**
     * Try to convert object to boolean
     *
     * @param o
     * @return
     */
    private static Boolean toBoolean(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        try {
            return parseBoolean(o != null ? o.toString() : "");
        } catch (FormatException ex) {
            return false;
        }
    }

    @Override
    public MusicData unmarshal(Object o) {
        if (o instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) o;

            Object fileName = map.get("File");
            Object loop = map.get("y");
            Object resetEmpty = map.get("z");

            if (fileName == null || loop == null || resetEmpty == null) {
                return null;
            }

            return new MusicData(toBoolean(loop), toBoolean(resetEmpty),
                    fileName != null ? fileName.toString() : "");
        }

        return null;
    }

    @Override
    public Object marshal(MusicData o) {
        Map<String, Object> vec = new HashMap<String, Object>();
        vec.put("File", o.getMidiFile());
        vec.put("IsLooped", o.isLooped());
        vec.put("ResetEmpty", o.resetOnEmpty());
        return vec;
    }

}
