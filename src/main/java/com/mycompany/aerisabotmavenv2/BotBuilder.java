/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aerisabotmavenv2;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BotBuilder extends ListenerAdapter {
    public static void main(String[] args) throws Exception {
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken("Mzc5MDYyNDI2NjU1Nzg0OTYw.DTPBmA.PX1m-Tlq7dVrqbT_F9SbN-bLunY")
                .buildBlocking();

        jda.addEventListener(new BotBuilder());
        jda.getPresence().setGame(Game.playing("Skyrim 2 | -help"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(",", 2);
        Guild guild = event.getGuild();
        
        if (guild != null) {
            if (null != command[0]) {
                switch (command[0]) {
                   
                }
            }
        }

        super.onMessageReceived(event);
    }
}
