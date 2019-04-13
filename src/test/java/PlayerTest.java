
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class PlayerTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        player = new Player();
        dungeon = new Dungeon(1, 12);
        dungeon.spawnEntity(player, dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY());
    }

    // These should work, as the UI relies on that to display stats.
    @Test
    public void examinationTextIsNullWithoutLastEntity() {
        moveToExamine();
        player.resetLastEntityInteractedWith();
        Assert.assertNull(player.getExaminationText());
    }

    @Test
    public void examinationTextIsNotNullWithLastEntity() {
        moveToExamine();
        Assert.assertNotNull(player.getExaminationText());
    }

    private void moveToExamine() {
        dungeon.movePlayerNTimes(-8, 0);
        dungeon.movePlayerNTimes(0, -4);
        dungeon.movePlayerNTimes(-3, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(-1, 0);
    }

    @Test
    public void collisionWorksWhenItHappens() {
        Assert.assertFalse(player.move(1, 0));
    }

    @Test
    public void collisionWorksWhenItDoesNotHappen() {
        Assert.assertTrue(player.move(-1, 0));
    }
}
