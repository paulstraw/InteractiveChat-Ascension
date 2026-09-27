/*
 * This file is part of InteractiveChat-Ascension.
 *
 * Copyright (C) 2026. Paul Straw
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.io.InputStream;

/**
 * An in-memory stream that can be read in full any number of times.
 * <p>
 * DiscordSRV sends one message object to every channel in a Discord server, and JDA reads each attachment's
 * stream to the end and then closes it once per request. Each thread gets its own position, and closing
 * rewinds it, so every send uploads the whole file, including sends running at the same time.
 */
public class ReplayableInputStream extends InputStream {

    private final byte[] data;
    private final ThreadLocal<int[]> position = ThreadLocal.withInitial(() -> new int[1]);

    public ReplayableInputStream(byte[] data) {
        this.data = data;
    }

    @Override
    public int read() {
        int[] pos = position.get();
        return pos[0] < data.length ? data[pos[0]++] & 0xFF : -1;
    }

    @Override
    public int read(byte[] b, int off, int len) {
        int[] pos = position.get();
        if (pos[0] >= data.length) {
            return -1;
        }
        int count = Math.min(len, data.length - pos[0]);
        System.arraycopy(data, pos[0], b, off, count);
        pos[0] += count;
        return count;
    }

    @Override
    public int available() {
        return data.length - position.get()[0];
    }

    @Override
    public void close() {
        position.get()[0] = 0;
    }

}
