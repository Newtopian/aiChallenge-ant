#!/usr/bin/env sh
make clean
make
python ../tools/playgame.py "java -jar MyBot.jar" "python ../tools/sample_bots/python/HunterBot.py" --map_file ../tools/maps/example/tutorial1.map --log_dir game_logs --turns 60 --scenario --food none --player_seed 7 --verbose -e