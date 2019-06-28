# PingWars

PingWars is a bot based around a simple Discord mechanic... Pings!

PingWars was made for Discord Hack Week 2019

# Discord

The bot will be running 24/7 on the Discord
https://discord.gg/afkZuqW

# Features

- The bot uses pings as a currency
    - Pinging players removed 1 ping from their ping count, and adds one to yours (no going below 0)

# Generators
- Generators can be bought with pings/BP (more on that later)
- Generators constantly generate pings you can unleash on a single enemy

# Guilds
- Players can team up in Guilds
- Guilds cannot ping each other
- Guilds can use GP (See the war section) to upgrade their guild (Max players, Ranks, etc...)
- Guild leaders can transfer ownership, and promote elders to help manage the guild
    
# War
- Guilds can declare war on other guild
    - War lasts for 1 hour
    - At the end of a war, the each member of the winning team gets all the pings they sent + all the pings against them
    - Winning guilds also get Guild Poings (GP)
    
# Bosses
- Randomly once every hour, a boss can spawn
    - Bosses are a random player in the guild
    - Bosses have a health meter that can be depleted by pinging them
    - When defeated, bosses drop Boss Points (BP) to every player that damaged them (scaled to match the amount of damage)
    
# Prestige
- Sacrifice generators to prestige them, giving a 15% bonus to pings

# Planned features
- Prestiging generators, taking a large amount of generators for a bonus precent of pings when claimed
- Special events (New Years, Thanksgiving, Easter, etc...)
- Big Boss events?
- Looking for other ideas on Discord, want to focus on Guilds.
- Switch from unsigned Longs to BitIngeters to allow for infinite pings.

# Needed improvements

This economy bot was made in a week, so there is 1 balance update so far. I will try to remain vigilent with balancing the economy, but
it won't be perfect right now.

# Current balance mechanics

The claim system is setup so new plyers being targetted won't be punished. Claimed pings can be immediately invested into generators, and
players can gain small amounts of BP from helping with boss battles. Initiating Guild Wars also requires knowing the Guild ID, meaning
a guild member from the guild must do !guild info for other guilds to be able to declare war. Guild Leaders can also stop a war, instantly
making the opposing guild win.

# Misc

I added configuration to limit pings/commands to certain channels.
I created my own flat file system to store data files as bytes to save player data ( I worked on it some before with another project but refined it for my use here)
