#!/usr/bin/env sh
cd ../tools
./playgame.py \
  -So --player_seed 42 --end_wait=0.25 --verbose \
  --log_dir game_logs --turns 1000 \
  --map_file maps/maze/maze_9.map \
    "$@" \
	"java -jar ../myFirstJBot/MyBot.jar" \
	"python sample_bots/python/LeftyBot.py" \
	"python sample_bots/python/HunterBot.py" \
	"python sample_bots/python/LeftyBot.py" |
java -jar ./visualizer.jar
