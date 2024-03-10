/*
 * Copyright 2018 John Grosh (jagrosh).
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
package dev.cosgy.jmusicbot.slashcommands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import dev.cosgy.jmusicbot.slashcommands.MusicCommand;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand {
    public SkipCmd(Bot bot) {
        super(bot);
        this.name = "skip";
        this.help = "Request to skip the currently playing song";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getAuthor().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio.net/") ? "GensokyoRadio" : handler.getPlayer().getPlayingTrack().getInfo().title) + "** was skipped.");
            handler.getPlayer().stopTrack();
        } else {
            // Number of people in voicechat (excluding Bots, Speaker Mutes)
            int listeners = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()).count();

            // Message to be sent
            String msg;

            // Checks if the message sender is already in the current vote
            if (handler.getVotes().contains(event.getAuthor().getId())) {
                msg = event.getClient().getWarning() + " The currently playing song has already been skip requested. `[";
            } else {
                msg = event.getClient().getSuccess() + " Requested to skip the current song. `[";
                handler.getVotes().add(event.getAuthor().getId());
            }

            // Count the number of people who have voted to skip in the voicechat
            int skippers = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            int required = (int) Math.ceil(listeners * bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio());
            msg += skippers + " votes, " + required + "/" + listeners + " required]`";

            // If the number of votes required is different from the number of people in the voicechat
            if (required != listeners) {
                // Add a message
                msg += " The skip request count is currently " + skippers + ". To skip, " + required + "/" + listeners + " are required.`";
            } else {
                msg = "";
            }

            // Check if the number of voters has reached the required number of votes
            if (skippers >= required) {
                msg += "\n" + event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio.net/") ? "GensokyoRadio" : handler.getPlayer().getPlayingTrack().getInfo().title)
                        + "** has been skipped. " + (rm.getOwner() == 0L ? "(Autoplay)" : "(**" + rm.user.username + "** requested)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }

    @Override
    public void doCommand(SlashCommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();

        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getUser().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio.net/") ? "GensokyoRadio" : handler.getPlayer().getPlayingTrack().getInfo().title) + "** has been skipped.").queue();
            handler.getPlayer().stopTrack();
        } else {
            // Count the number of people (excluding bots and deafened) in the voice channel
            int listeners = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened() && m.getUser().getIdLong() != handler.getRequestMetadata().getOwner()).count();

            // Message to send
            String msg;

            // Check if the message sender is already in the vote
            if (handler.getVotes().contains(event.getUser().getId())) {
                msg = event.getClient().getWarning() + " A skip request for the currently playing song has already been made. `[";
            } else {
                msg = event.getClient().getSuccess() + " A skip request for the current song has been made. `[";
                handler.getVotes().add(event.getUser().getId());
            }

            // Count the number of people who have voted to skip in the voicechat
            int skippers = (int) event.getGuild().getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();

            // Required number of votes (listeners * 0.55)
            int required = (int) Math.ceil(listeners * 0.55);

            // If the number of voters is different from the number of people in the voicechat
            if (required != listeners) {
                // Add a message
                msg += "Skip requests so far: " + skippers + ". " + required + "/" + listeners + " are needed to skip.`";
            } else {
                msg = "";
            }

            // Check if the number of voters has reached the required number of votes
            if (skippers >= required) {
                msg += "\n" + event.getClient().getSuccess() + "**" + (handler.getPlayer().getPlayingTrack().getInfo().uri.contains("https://stream.gensokyoradio.net/") ? "GensokyoRadio" : handler.getPlayer().getPlayingTrack().getInfo().title)
                        + "** has been skipped. " + (rm.getOwner() == 0L ? "(Autoplay)" : "(**" + rm.user.username + "** requested)");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg).queue();
        }
    }
}
