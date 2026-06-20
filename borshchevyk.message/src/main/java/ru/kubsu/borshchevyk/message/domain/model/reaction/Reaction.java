package ru.kubsu.borshchevyk.message.domain.model.reaction;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Reaction {
    LIKE("👍"),
    DISLIKE("👎"),
    HEART("❤️"),
    FIRE("🔥"),
    LAUGH("😂"),
    SURPRISE("😮"),
    CELEBRATION("🎉"),
    HEART_EYES("😍"),
    CRY("😢"),

    SMILE("😁"),
    SHOCK("😱"),
    ANGRY("😡"),
    THINKING("🤔"),
    POOP("💩"),
    CLAP("👏"),
    PRAY("🙏"),
    COOL("😎"),
    MIND_BLOWN("🤯"),
    SCREAM("😱"),
    VOMIT("🤮"),

    WAVE("👋"),
    BLUSH("😊"),
    DONKEY("🐴"),
    MONKEY("🙈"),
    EYES("👀"),
    DIAMOND("💎"),
    ROBOT("🤖"),
    GHOST("👻"),
    ALIEN("👽"),
    PARTY("🥳"),
    SAD("😔"),
    DASH("💨"),
    WINK("😉"),
    HUNDRED("💯"),
    ROLL_EYES("🙄"),
    NAUSEA("🤢"),
    PROUD("😏");

    private final String value;

    Reaction(String value) {
        this.value = value;
    }

    public static Set<String> getAllReactions() {
        return Arrays.stream(Reaction.values()).map(Reaction::getValue).collect(Collectors.toSet());
    }
}