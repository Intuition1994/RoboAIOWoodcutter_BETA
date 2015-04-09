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
public class CutWillowTrees extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs  = BotUtil.PossibleLogs.WILLOW;
    public final String treeName        = "willow";
    public final int[] logsIDs          = new int[] { 1519 };
    private String actionToUse          = "chop";

    public Area bankAreaToUse           = null;
    public Area treeAreaToUse           = null;
    public Tile[] pathToBankToUse       = null;
    public Tile[] pathToTreeToUse       = null;

    //region [ Draynor Data ]...

    private final Area BankArea_Draynor = new Area(
            new Tile(3097, 3249, 0),
            new Tile(3087, 3249, 0),
            new Tile(3086, 3249, 0),
            new Tile(3086, 3238, 0),
            new Tile(3099, 3238, 0)
    );
    private final Area TreeArea_Draynor = new Area(
            new Tile(3077, 3244, 0),
            new Tile(3087, 3244, 0),
            new Tile(3092, 3235, 0),
            new Tile(3093, 3225, 0),
            new Tile(3093, 3215, 0),
            new Tile(3086, 3223, 0),
            new Tile(3081, 3232, 0),
            new Tile(3077, 3242, 0)
    );

    private final Tile[] PathToBank_Draynor = new Tile[] {
            new Tile(3088, 3232, 0),
            new Tile(3084, 3241, 0),
            new Tile(3085, 3247, 0),
            new Tile(3092, 3245, 0),
            new Tile(3093, 3243, 0)
    };

    private final Tile[] PathToTree_Draynor = new Tile[] {
            new Tile(3091, 3246, 0),
            new Tile(3086, 3249, 0),
            new Tile(3085, 3241, 0),
            new Tile(3087, 3233, 0)
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
            case Draynor:
                bankAreaToUse       = BankArea_Draynor;
                treeAreaToUse       = TreeArea_Draynor;
                pathToBankToUse     = PathToBank_Draynor;
                pathToTreeToUse     = PathToTree_Draynor;
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
