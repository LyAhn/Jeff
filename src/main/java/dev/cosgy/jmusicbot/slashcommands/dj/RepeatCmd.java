/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.cosgy.jmusicbot.slashcommands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import dev.cosgy.jmusicbot.settings.RepeatMode;
import dev.cosgy.jmusicbot.slashcommands.DJCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author John Grosh <john.a.grosh@gmail.com> | edit: ryuuta0217
 */
public class RepeatCmd extends DJCommand {
    Logger log = LoggerFactory.getLogger("Repeat");

    public RepeatCmd(Bot bot) {
        super(bot);
        this.name = "repeat";
        this.help = "Repeats the currently playing track when it finishes";
        this.arguments = "[all|on|single|one|off]";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = true;

        this.children = new SlashCommand[]{new SingleCmd(bot), new AllCmd(bot), new OffCmd(bot)};

    }

    // override musiccommand's execute because we don't actually care where this is used
    @Override
    protected void execute(CommandEvent event) {
        RepeatMode value;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());

        String args = event.getArgs();

        if (args.isEmpty()) {
            log.info("変更前の再生モード:" + settings.getRepeatMode());
            value = (settings.getRepeatMode() == RepeatMode.OFF ? RepeatMode.ALL : (settings.getRepeatMode() == RepeatMode.ALL ? RepeatMode.SINGLE : (settings.getRepeatMode() == RepeatMode.SINGLE ? RepeatMode.OFF : settings.getRepeatMode())));
        } else if (args.equalsIgnoreCase("true") || args.equalsIgnoreCase("all") || args.equalsIgnoreCase("on")) {
            value = RepeatMode.ALL;
        } else if (args.equalsIgnoreCase("false") || args.equalsIgnoreCase("off")) {
            value = RepeatMode.OFF;
        } else if (args.equalsIgnoreCase("one") || args.equalsIgnoreCase("single")) {
            value = RepeatMode.SINGLE;
        } else {
            event.replyError("Valid options are:\n" +
                    "```\n" +
                    "Full song repeat: true, all, on\n" +
                    "Single song repeat: one, single\n" +
                    "Repeat off: false, off" +
                    "```\n" +
                    "are valid options\n" +
                    "(or it can be toggled without options)");
            return;
        }

        settings.setRepeatMode(value);
        log.info(event.getGuild().getName() + " executed the repeat command and set the configuration to " + value + ".");
        event.replySuccess("Repeat was set to `" + (value == RepeatMode.ALL ? "Enabled (Full Song Repeat)" : (value == RepeatMode.SINGLE ? "Enabled (Single Song Repeat)" : "Disabled")) + "`.");
    }

    @Override
    public void doCommand(CommandEvent event) { /* Intentionally Empty */ }

    @Override
    public void doCommand(SlashCommandEvent event) {
    }

    private class SingleCmd extends DJCommand {
        public SingleCmd(Bot bot) {
            super(bot);
            this.name = "single";
            this.help = "Changes repeat mode to single track.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Insufficient permission to execute.").queue();
                return;
            }
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setRepeatMode(RepeatMode.SINGLE);
            event.reply("Repeat set to `Enabled (Single Track Repeat)`.").queue();
        }

        @Override
        public void doCommand(CommandEvent event) {
        }
    }

    private class AllCmd extends DJCommand {
        public AllCmd(Bot bot) {
            super(bot);
            this.name = "all";
            this.help = "Changes repeat mode to all tracks.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Insufficient permission to execute.").queue();
                return;
            }
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setRepeatMode(RepeatMode.ALL);
            event.reply("Repeat enabled (Full Song Repeat).").queue();
        }

        @Override
        public void doCommand(CommandEvent event) {
        }
    }

    private class OffCmd extends DJCommand {
        public OffCmd(Bot bot) {
            super(bot);
            this.name = "off";
            this.help = "Changes repeat mode to off.";
            this.guildOnly = true;
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            if (!checkDJPermission(event.getClient(), event)) {
                event.reply(event.getClient().getWarning() + "Insufficient permission to execute.").queue();
                return;
            }
            Settings settings = event.getClient().getSettingsFor(event.getGuild());
            settings.setRepeatMode(RepeatMode.OFF);
            event.reply("Repeat set to `Disabled`.").queue();
        }

        @Override
        public void doCommand(CommandEvent event) {
        }
    }
}
