# The Buddha's Journey: Demo Card Game

A turn-based card game built in Java, inspired by *Journey to the West* and *Yu-Gi-Oh*. Draw from a variety of 15+ unique cards, trigger special effects, and battle enemy AI to reach enlightenment.

<img width="2672" height="1338" alt="image" src="https://github.com/user-attachments/assets/1ab83163-2b08-4790-a11d-712f8b4e7246" />

## Functionality

Game starts with both sides drawing a hand from their deck. On your turn, select a card and place it in one of four lanes. Placing cards may trigger card effect (drain enemy life force, buff other cards, etc). The enemy AI draws and places cards on its own turn using the same rules. The first to drain the opponent's life force to 0 wins.

- **Cards**: 19 total, each with its own design, power, and unique card effects.
- **Lanes**: 4 lanes per side. Card placement, power totals, and effects are all resolved independently for each lane.
- **Turns**: Alternating player/AI turns with start, battle, and end phases, including timed status effects that persist across turns.
- **Progression**: Winning a match earns a scripture and unlocks a random enemy bonus card. Progress carries between sessions.

## Personal Setup

Requires a JDK (17+) and Maven.

```
git clone https://github.com/d0d0yang/the-buddhas-journey.git
cd the-buddhas-journey
```

Maven pulls in the Processing core library automatically

```
mvn compile exec:java
```

Note: first run may be slow as Maven downloads `org.processing:core` and its JOGL/Gluegen graphics dependencies.

## Project Structure

```
src/
  Main.java          entry point, launches the Processing sketch
  Sketch.java         main game loop, rendering, and UI/mouse event handling
  GameState.java       turn/phase management, win-loss checks, status effects
  CardManager.java      loads and parses card data, builds card effects
  Cards.java            individual card model (stats, effect, sprite, clone logic)
  Decks.java             full deck storage and current-deck draw/shuffle logic
  Hands.java              hand management and card layout/positioning
  Lanes.java               lane placement, layout, and click detection
  Character.java            base class for Player and Enemy
  Player.java                 player-specific state (scriptures)
  Enemy.java                   enemy-specific state (portrait, dialogue)
  SaveManager.java              save/load scriptures, bonus cards, tutorial flag
  Card_Data.txt                  card stat/effect definitions, parsed at runtime
  Save_Data.txt                   persisted player save data

Images/               card art, board background, win/lose screens, tutorial slides
pom.xml               Maven build config, pulls in the Processing core library
```

## Tools

Java, and the [Processing](https://processing.org/) core library for 2D rendering, sprite drawing, and mouse input handling. Built and run with Maven.

## Known Limitations

- The AI enemy places cards without any strategy. Martin chess.com equivalent.
- Card art isn't bundled in this repo (see the setup note below). Uou'll need your own `Images/` folder.
- The save file is a single flat text file so there's only one save slot.

Note this is a personal project built for learning purposes, not a polished commercial game. Balance, save data, and AI behavior are all incredibly rough around the edges.
