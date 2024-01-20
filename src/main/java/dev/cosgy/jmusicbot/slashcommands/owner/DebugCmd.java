/*
 *  Copyright 2024 Cosgy Dev (info@cosgy.dev).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package dev.cosgy.jmusicbot.slashcommands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import dev.cosgy.jmusicbot.slashcommands.OwnerCommand;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.FileUpload;

public class DebugCmd extends OwnerCommand {
    private final static String[] PROPERTIES = {"java.version", "java.vm.name", "java.vm.specification.version",
            "java.runtime.name", "java.runtime.version", "java.specification.version", "os.arch", "os.name"};

    private final Bot bot;

    public DebugCmd(Bot bot) {
        this.bot = bot;
        this.name = "debug";
        this.help = "デバッグ情報を表示します";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("システムプロパティ:");
        for (String key : PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key));
        sb.append("\n\nJMusicBot JPの情報:")
                .append("\n  Version = ").append(OtherUtil.getCurrentVersion())
                .append("\n  Owner = ").append(bot.getConfig().getOwnerId())
                .append("\n  Prefix = ").append(bot.getConfig().getPrefix())
                .append("\n  AltPrefix = ").append(bot.getConfig().getAltPrefix())
                .append("\n  MaxSeconds = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  NPImages = ").append(bot.getConfig().useNPImages())
                .append("\n  SongInStatus = ").append(bot.getConfig().getSongInStatus())
                .append("\n  StayInChannel = ").append(bot.getConfig().getStay())
                .append("\n  UseEval = ").append(bot.getConfig().useEval())
                .append("\n  UpdateAlerts = ").append(bot.getConfig().useUpdateAlerts());
        sb.append("\n\n依存情報:")
                .append("\n  JDA Version = ").append(JDAInfo.VERSION)
                .append("\n  JDA-Utilities Version = ").append(JDAUtilitiesInfo.VERSION)
                .append("\n  Lavaplayer Version = ").append(PlayerLibrary.VERSION);
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        sb.append("\n\nランタイム情報:")
                .append("\n  Total Memory = ").append(total)
                .append("\n  Used Memory = ").append(used);
        sb.append("\n\nDiscord情報:")
                .append("\n  ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  Guilds = ").append(event.getJDA().getGuildCache().size())
                .append("\n  Users = ").append(event.getJDA().getUserCache().size());
        sb.append("\nこのファイルを開発者に送信する場合は編集せずに送信するようにお願いします。")
                .append("\nこのファイルには個人情報を特定、アカウントの乗っ取りなどに使用できるデータは含んでいません。");

        if (event.isFromGuild() || event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            event.reply("デバック情報").queue();
            event.getChannel().sendFiles(FileUpload.fromData(sb.toString().getBytes(), "debug_information.txt")).queue();
        } else {
            event.reply("デバッグ情報: ```\n" + sb + "\n```").queue();
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("システムプロパティ:");
        for (String key : PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key));
        sb.append("\n\nJMusicBot JPの情報:")
                .append("\n  Version = ").append(OtherUtil.getCurrentVersion())
                .append("\n  Owner = ").append(bot.getConfig().getOwnerId())
                .append("\n  Prefix = ").append(bot.getConfig().getPrefix())
                .append("\n  AltPrefix = ").append(bot.getConfig().getAltPrefix())
                .append("\n  MaxSeconds = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  NPImages = ").append(bot.getConfig().useNPImages())
                .append("\n  SongInStatus = ").append(bot.getConfig().getSongInStatus())
                .append("\n  StayInChannel = ").append(bot.getConfig().getStay())
                .append("\n  UseEval = ").append(bot.getConfig().useEval())
                .append("\n  UpdateAlerts = ").append(bot.getConfig().useUpdateAlerts());
        sb.append("\n\n依存情報:")
                .append("\n  JDA Version = ").append(JDAInfo.VERSION)
                .append("\n  JDA-Utilities Version = ").append(JDAUtilitiesInfo.VERSION)
                .append("\n  Lavaplayer Version = ").append(PlayerLibrary.VERSION);
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        sb.append("\n\nランタイム情報:")
                .append("\n  Total Memory = ").append(total)
                .append("\n  Used Memory = ").append(used);
        sb.append("\n\nDiscord情報:")
                .append("\n  ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  Guilds = ").append(event.getJDA().getGuildCache().size())
                .append("\n  Users = ").append(event.getJDA().getUserCache().size());
        sb.append("\nこのファイルを開発者に送信する場合は編集せずに送信するようにお願いします。");

        if (event.isFromType(ChannelType.PRIVATE)
                || event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES))
            event.getChannel().sendFiles(FileUpload.fromData(sb.toString().getBytes(), "debug_information.txt")).queue();
        else
            event.reply("デバッグ情報: ```\n" + sb + "\n```");
    }
}