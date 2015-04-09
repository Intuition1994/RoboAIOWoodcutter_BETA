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
public class CutNormalTrees extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs  = BotUtil.PossibleLogs.NORMAL;
    public final String treeName        = "tree";
    public final int[] logsIDs          = new int[] { 1511 };
    private String actionToUse          = "chop";

    public Area bankAreaToUse           = null;
    public Area treeAreaToUse           = null;
    public Tile[] pathToBankToUse       = null;
    public Tile[] pathToTreeToUse       = null;

    //region [ Grand Exchange Data ]...

    private final Area BankArea_GrandExchange = new Area(
            new Tile(3137, 3516, 0),
            new Tile(3137, 3473, 0),
            new Tile(3143, 3468, 0),
            new Tile(3150, 3467, 0),
            new Tile(3151, 3465, 0),
            new Tile(3177, 3465, 0),
            new Tile(3181, 3469, 0),
            new Tile(3186, 3469, 0),
            new Tile(3187, 3475, 0),
            new Tile(3191, 3480, 0),
            new Tile(3191, 3499, 0),
            new Tile(3198, 3506, 0),
            new Tile(3189, 3518, 0)
    );

    private final Area TreeArea_GrandExchange = new Area(
            new Tile(3137, 3516, 0),
            new Tile(3137, 3473, 0),
            new Tile(3143, 3468, 0),
            new Tile(3150, 3467, 0),
            new Tile(3151, 3465, 0),
            new Tile(3177, 3465, 0),
            new Tile(3181, 3469, 0),
            new Tile(3186, 3469, 0),
            new Tile(3187, 3475, 0),
            new Tile(3191, 3480, 0),
            new Tile(3191, 3499, 0),
            new Tile(3198, 3506, 0),
            new Tile(3189, 3518, 0)
    );

    public final Tile[] PathToBank_GrandExchange = new Tile[] {};
    public final Tile[] PathToTree_GrandExchange = new Tile[] {};

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
            case Grand_Exchange:
                bankAreaToUse       = BankArea_GrandExchange;
                treeAreaToUse       = TreeArea_GrandExchange;
                pathToBankToUse     = PathToBank_GrandExchange;
                pathToTreeToUse     = PathToTree_GrandExchange;
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
