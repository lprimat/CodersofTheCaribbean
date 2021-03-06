import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Player {

    private static boolean debug = true;

    public static void main(String args[]) {
        Scanner in;
        if (debug) {
            in = new Scanner("30 1 24 25\n" +
                    "30 1 25 25\n" +
                    "5 6\n" +
                    "37 2 0 0 6 5 7 ------ 0 0 1\n" +
                    "61 4 0 0 9 10 10 ------ 0 0 0\n" +
                    "75 6 0 0 5 6 5 B----- 0 0 0\n" +
                    "96 8 0 0 2 3 2 ---G-- 0 0 0\n" +
                    "83 10 0 0 0 1 1 -C---- 0 0 0\n" +
                    "55 12 0 0 2 0 5 ---G-- 0 0 0");
        } else {
            in = new Scanner(System.in);
        }

        Gamer myGamer = null;
        Gamer enGamer = null;
        while (true) {
            for (int i = 0; i < 2; i++) {
                int playerHealth = in.nextInt();
                int playerMana = in.nextInt();
                int playerDeck = in.nextInt();
                int playerRune = in.nextInt();

                Gamer g = new Gamer(playerHealth, playerMana, playerDeck, playerRune);
                myGamer = i == 0 ? g : myGamer;
                enGamer = i == 1 ? g : enGamer;

                if (debug) {
                    System.err.println(playerHealth + " " + playerMana + " " + playerDeck + " " + playerRune);
                }
            }
            int opponentHand = in.nextInt();
            int cardCount = in.nextInt();
            if (debug) {
                System.err.println(opponentHand + " " + cardCount);
            }

            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < cardCount; i++) {
                int cardNumber = in.nextInt();
                int instanceId = in.nextInt();
                int location = in.nextInt();
                int cardType = in.nextInt();
                int cost = in.nextInt();
                int attack = in.nextInt();
                int defense = in.nextInt();
                String abilities = in.next();
                int myHealthChange = in.nextInt();
                int opponentHealthChange = in.nextInt();
                int cardDraw = in.nextInt();

                Card c = new Card(cardNumber, instanceId, location, cardType, cost, attack, defense, abilities, myHealthChange, opponentHealthChange, cardDraw);
                cards.add(c);

                if (debug) {
                    System.err.println(cardNumber + " " + instanceId + " " + location + " " + cardType + " " + cost
                            + " " + attack + " " + defense + " " + abilities  + " " + myHealthChange  + " " + opponentHealthChange + " " + cardDraw);
                }
            }

            if (myGamer.mana == 0) {
                System.out.println("PASS");
            } else {
                Gamer finalMyGamer = myGamer;
                List<Card> cardsSummonable = cards.stream()
                        .filter(card -> card.location == 0 && card.cost <= finalMyGamer.mana)
                        .collect(Collectors.toList());
                List<Action> actions = summonBestCreature(myGamer, cardsSummonable);
                List<Card> cardToAttack = cards.stream()
                        .filter(card -> card.location == 1)
                        .collect(Collectors.toList());
                actions.addAll(attackPlayer(cardToAttack));
                System.err.println("ACTIONS");
                printActions(actions);
            }
        }
    }

    private static List<Action> summonBestCreature(Gamer myGamer, List<Card> cardsInHand) {
        List<Action> cardsToSummon = new ArrayList<>();
        while (myGamer.mana != 0) {
            int maxAttack = 0;
            Card cardToSummon = null;
            for (Card c : cardsInHand) {
                if (c.cost <= myGamer.mana && c.attack >= maxAttack) {
                    cardToSummon = c;
                    maxAttack = c.attack;
                }
            }
            if(cardToSummon != null) {
                cardsInHand.remove(cardToSummon);
                cardsToSummon.add(new Action(Name.SUMMON, String.valueOf(cardToSummon.instanceId)));
                myGamer.mana -= cardToSummon.cost;
            } else {
                break;
            }
        }
        return cardsToSummon;
    }

    private static List<Action> attackPlayer(List<Card> cardToAttack) {
        return  cardToAttack.stream().
                map(card -> new Action(Name.ATTACK, String.valueOf(card.instanceId) + " -1")).
                collect(Collectors.toList());
    }

    private static void printActions(List<Action> actions) {
        String actionsToPrint = actions.stream().
                map(Action::toString).
                collect(Collectors.joining(";"));
        if (actionsToPrint.isEmpty()) {
            System.out.println(Name.PASS);
        } else {
            System.out.println(actionsToPrint);
        }
    }
}

class Action {
    Name name;
    String param;

    public Action(Name name, String param) {
        this.name = name;
        this.param = param;
    }

    @Override
    public String toString() {
        return name + " " + param;
    }
}

enum Name {
    SUMMON,
    ATTACK,
    PASS
}

class Gamer {
    int health;
    int mana;
    int deck;
    int rune;

    public Gamer(int playerHealth, int playerMana, int playerDeck, int playerRune) {
        this.health = playerHealth;
        this.mana = playerMana;
        this.deck = playerDeck;
        this.rune = playerRune;
    }
}

class Card {

    int cardNumber;
    int instanceId;
    int location;
    int cardType;
    int cost;
    int attack;
    int defense;
    String abilities;
    int myhealthChange;
    int opponentHealthChange;
    int cardDraw;

    public Card(int cardNumber, int instanceId, int location, int cardType, int cost, int attack, int defense, String abilities, int myhealthChange, int opponentHealthChange, int cardDraw) {
        this.cardNumber = cardNumber;
        this.instanceId = instanceId;
        this.location = location;
        this.cardType = cardType;
        this.cost = cost;
        this.attack = attack;
        this.defense = defense;
        this.abilities = abilities;
        this.myhealthChange = myhealthChange;
        this.opponentHealthChange = opponentHealthChange;
        this.cardDraw = cardDraw;
    }
}