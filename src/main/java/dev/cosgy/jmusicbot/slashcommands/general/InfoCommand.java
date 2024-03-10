package dev.cosgy.jmusicbot.slashcommands.general;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import dev.cosgy.jmusicbot.util.MaintenanceInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Kosugi_kun
 */
public class InfoCommand extends SlashCommand {

    public InfoCommand(Bot bot) {
        this.name = "info";
        this.help = "Inform about maintenance.";
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Calendar Now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date NowTime = Now.getTime();
        event.reply("Receiving information...").queue(m -> {
            try {
                if (MaintenanceInfo.Verification()) {
                    MaintenanceInfo InfoResult = MaintenanceInfo.GetInfo();

                    MessageCreateBuilder builder = new MessageCreateBuilder().addContent("**").addContent(InfoResult.Title).addContent("**");
                    EmbedBuilder ebuilder = new EmbedBuilder()
                            .setColor(Color.orange)
                            .setDescription(InfoResult.Content);
                    if (!InfoResult.StartTime.equals("")) {
                        ebuilder.addField("Start time:", InfoResult.StartTime, false);
                    }
                    if (!InfoResult.EndTime.equals("")) {
                        ebuilder.addField("End time:", InfoResult.EndTime, false);
                    }
                    ebuilder.addField("Last update:", InfoResult.LastUpdate, false)
                            .addField("Current time", sdf.format(NowTime), false)
                            .setFooter("*Note: Maintenance period may be changed without notice.", null);
                    m.editOriginalEmbeds(ebuilder.build()).queue();
                } else {
                    m.editOriginal("No information.").queue();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void execute(CommandEvent event) {
        Calendar Now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date NowTime = Now.getTime();
        Message m = event.getChannel().sendMessage("Receiving information...").complete();
        try {
            if (MaintenanceInfo.Verification()) {
                MaintenanceInfo InfoResult = MaintenanceInfo.GetInfo();

                MessageCreateBuilder builder = new MessageCreateBuilder().addContent("**").addContent(InfoResult.Title).addContent("**");
                EmbedBuilder ebuilder = new EmbedBuilder()
                        .setColor(Color.orange)
                        .setDescription(InfoResult.Content);
                if (!InfoResult.StartTime.equals("")) {
                    ebuilder.addField("Start time:", InfoResult.StartTime, false);
                }
                if (!InfoResult.EndTime.equals("")) {
                    ebuilder.addField("End time:", InfoResult.EndTime, false);
                }
                ebuilder.addField("Last update:", InfoResult.LastUpdate, false)
                        .addField("Current time", sdf.format(NowTime), false)
                        .setFooter("*Note: Maintenance period may be changed without notice.", null);
                m.editMessageEmbeds(ebuilder.build()).queue();

            } else {
                m.editMessage("No information.").queue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}