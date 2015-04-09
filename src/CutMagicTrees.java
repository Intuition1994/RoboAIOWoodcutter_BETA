import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.Walking;
import com.epicbot.api.rs3.methods.interactive.NPCs;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.node.SceneEntities;
import com.epicbot.api.rs3.methods.tab.inventory.Inventory;
import com.epicbot.api.rs3.methods.widget.Bank;
import com.epicbot.api.rs3.methods.widget.Camera;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;
import com.epicbot.api.rs3.wrappers.interactive.NPC;
import com.epicbot.api.rs3.wrappers.map.TilePath;
import com.epicbot.api.rs3.wrappers.node.Item;
import com.epicbot.api.rs3.wrappers.node.SceneObject;
import com.epicbot.api.util.Random;
import com.epicbot.api.util.Time;

/**
 * Created by jt13602 on 25/03/2015.
 */
public class CutMagicTrees extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs  = BotUtil.PossibleLogs.MAGIC;
    public final String treeName        = "magic";
    public final int[] logsIDs          = new int[] { 1513 };
    private String actionToUse          = "chop";

    public Area bankAreaToUse           = null;
    public Area treeAreaToUse           = null;
    public Tile[] pathToBankToUse       = null;
    public Tile[] pathToTreeToUse       = null;

    //region [ Seer's Village Data ]...

    private final Area TreeArea_SeersVillage = new Area(
            new Tile(2693, 3404, 0),
            new Tile(2694, 3391, 0),
            new Tile(2709, 3390, 0),
            new Tile(2711, 3411, 0)
    );

    private final Area BankArea_SeersVillage = new Area(
            new Tile(2717, 3500, 0),
            new Tile(2718, 3484, 0),
            new Tile(2734, 3484, 0),
            new Tile(2733, 3502, 0)
    );

    private final Tile[] PathToTree_SeersVillage = new Tile[] {
            new Tile(2725, 3489, 0),
            new Tile(2725, 3479, 0),
            new Tile(2725, 3469, 0),
            new Tile(2725, 3459, 0),
            new Tile(2721, 3449, 0),
            new Tile(2719, 3439, 0),
            new Tile(2717, 3429, 0),
            new Tile(2716, 3419, 0),
            new Tile(2714, 3409, 0),
            new Tile(2711, 3399, 0),
            new Tile(2702, 3399, 0)
    };

    private final Tile[] PathToBank_SeersVillage = new Tile[] {
            new Tile(2701, 3400, 0),
            new Tile(2706, 3409, 0),
            new Tile(2712, 3417, 0),
            new Tile(2717, 3428, 0),
            new Tile(2719, 3438, 0),
            new Tile(2722, 3448, 0),
            new Tile(2724, 3458, 0),
            new Tile(2725, 3468, 0),
            new Tile(2726, 3478, 0),
            new Tile(2727, 3488, 0),
            new Tile(2725, 3492, 0)
    };
    //endregion

    // --------------------------
    //  Task Execution condition
    // --------------------------
    @Override
    public boolean shouldExecute()
    {
        boolean retVal = false;

        if( !BotUtil.BOT_IS_RUNNING ) return false;

        if( BotUtil.CHOSENLOGS == myLogs )
        {
            if( Players.getLocal() != null )
            {
                retVal = true;
            }
        }

        return retVal;
    }

    @Override
    public void run()
    {
        if( BotUtil.BANK_LOGS )
        {
            DetermineTreeAreaAndPathData();
        }

        // If the player inventory is not full.
        if( Inventory.getCount() < 28 && !BotUtil.DroppingIventory )
        {
            // We want to cut logs.
            // Is the player in the Tree Area
            if( treeAreaToUse != null )
            {
                if (treeAreaToUse.contains(Players.getLocal().getLocation()))
                {
                    // if they are, start chopping tree's
                    BotUtil.CutLogs(treeName, treeAreaToUse, actionToUse);
                }
                else
                {
                    // If they aren't, start walking there.
                    BotUtil.WalkAlongPath(pathToTreeToUse);
                }
            }
            else
            {
                BotUtil.CutLogs(treeName, null, actionToUse);       // When not using an area, pass null for that parameter.
            }
        }
        else
        {
            if( BotUtil.BANK_LOGS )
            {
                // First Check they are at the bank?
                if (bankAreaToUse.contains(Players.getLocal().getLocation()))
                {
                    BotUtil.BankItems();
                }
                else
                {
                    // if not, time to walk there.
                    BotUtil.WalkAlongPath(pathToBankToUse);
                }
            }
            else
            {
                BotUtil.DropLogs(logsIDs);
            }
        }
    }

    public void DetermineTreeAreaAndPathData()
    {
        switch(BotUtil.CHOSENBANK)
        {
            case Seers_Village:
                bankAreaToUse       = BankArea_SeersVillage;
                treeAreaToUse       = TreeArea_SeersVillage;
                pathToBankToUse     = PathToBank_SeersVillage;
                pathToTreeToUse     = PathToTree_SeersVillage;
                break;
            default:
                bankAreaToUse       = null;
                treeAreaToUse       = null;
                pathToBankToUse     = null;
                pathToTreeToUse     = null;
                break;
        }
    }
}
