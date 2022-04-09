package io.github.hellinfernal.werewolf.discord.examples.werewolf;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;

import io.github.hellinfernal.werewolf.core.Game;

import java.util.List;

public class WerewolfCollector {
    List<Member> userList;

    public Game generateGame(){
        return new Game(userList);
    }

}
