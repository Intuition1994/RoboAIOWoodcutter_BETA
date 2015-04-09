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
public class CutMapleTrees extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs  = BotUtil.PossibleLogs.MAPLE;
    public final String treeName        = "maple";
    public final int[] logsIDs          = new int[] { 1517 };
    private String actionToUse          = "chop";

    public Area bankAreaToUse           = null;
    public Area treeAreaToUse           = null;
    public Tile[] pathToBankToUse       = null;
    public Tile[] pathToTreeToUse       = null;

    //region [ Seers Village Data ]...

    private final Area BankArea_SeersVillage = new Area(
            new Tile(2718, 3499, 0),
            new Tile(2719, 3486, 0),
            new Tile(2729, 3486, 0),
            new Tile(2732, 3489, 0),
            new Tile(2731, 3500, 0)
    );

    private final Area TreeArea_SeersVillage = new Area(
            new Tile(2715, 3513, 0),
            new Tile(2713, 3498, 0),
            new Tile(2731, 3498, 0),
            new Tile(2731, 3486, 0),
            new Tile(2722, 3480, 0),
            new Tile(2722, 3471, 0),
            new Tile(2739, 3478, 0),
            new Tile(2747, 3481, 0),
            new Tile(2744, 3495, 0),
            new Tile(2731, 3503, 0),
            new Tile(2728, 3511, 0)
    );

    private final Tile[] PathToTree_SeersVillage = new Tile[] {
            new Tile(2724, 3491, 0),
            new Tile(2726, 3483, 0),
            new Tile(2736, 3487, 0),
            new Tile(2732, 3501, 0)
    };

    private final Tile[] PathToBank_SeersVillage = new Tile[] {
            new Tile(2732, 3500, 0),
            new Tile(2738, 3489, 0),
            new Tile(2729, 3477, 0),
            new Tile(2726, 3491, 0)
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

        if( BotUtil.CHOSENLOGS == myLogs && BotUtil.CHOSENBANK != BotUtil.Banks.Daemonheim)
        {
            if( Players.getLocal() != null )
            {
                if( BotUtil.CHOSENBANK != BotUtil.Banks.Daemonheim )
                {
                    retVal = true;
                }
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
