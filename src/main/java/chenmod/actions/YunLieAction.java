package chenmod.actions;

import chenmod.character.ChenCharacter;
import chenmod.util.Sounds;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.player;

public class YunLieAction extends AbstractGameAction {

    private boolean isFirstEffect = true;

    public final int magicNumber;

    private final AbstractCard card;

    public YunLieAction(AbstractCard card, int magicNumber) {
        this.source = player;

        this.actionType = ActionType.DAMAGE;

        this.duration = AbstractGameAction.DEFAULT_DURATION;
        this.magicNumber = magicNumber;
        this.card = card;
    }

    @Override
    public void update() {

//        if(isFirstEffect){
//            if (player instanceof ChenCharacter) {
//                // 第二步：安全强转（100%不会报错）
//                CardCrawlGame.sound.play(Sounds.attackVoice_9);
//
//                ChenCharacter p = (ChenCharacter) player;
//
//                p.changeSpine38ToChen3(()->{
//
//                    p.useSkillAttackAnimation();
//
//                    this.addToBot(new YunLieAttackDamageRandomEnemyAction(this.card, this.magicNumber));
//
//                    this.isDone = true;
//                });
//
//            }else{
//
//                this.addToBot(new YunLieAttackDamageRandomEnemyAction(this.card, this.magicNumber));
//
//                this.isDone = true;
//            }
//
//            isFirstEffect = false;
//        }
    }
}
