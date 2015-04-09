import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.tab.inventory.Inventory;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;

/**
 * Created by jt13602 on 25/03/2015.
 */
public class CutYewTrees extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs  = BotUtil.PossibleLogs.YEW;
    public final String treeName        = "yew";
    public final int[] logsIDs          = new int[] { 1515 };
    private String actionToUse          = "chop";

    public Area bankAreaToUse           = null;
    public Area treeAreaToUse           = null;
    public Tile[] pathToBankToUse       = null;
    public Tile[] pathToTreeToUse       = null;

    //region [ Seer's Village Data ]...

    private final Area TreeArea_GrandExchange = new Area(
            new Tile(3197, 3508, 0),
            new Tile(3197, 3495, 0),
            new Tile(3228, 3494, 0),
            new Tile(3228, 3508, 0)
    );

    private final Area BankArea_GrandExchange = new Area(
            new Tile(3189, 3516, 0),
            new Tile(3166, 3516, 0),
            new Tile(3165, 3466, 0),
            new Tile(3189, 3467, 0)
    );

    private final Tile[] PathToTree_GrandExchange = new Tile[] {
            new Tile(3179, 3497, 0),
            new Tile(3180, 3487, 0),
            new Tile(3181, 3477, 0),
            new Tile(3188, 3485, 0),
            new Tile(3191, 3495, 0),
            new Tile(3201, 3496, 0),
            new Tile(3210, 3501, 0),
            new Tile(3213, 3503, 0)
    };

    private final Tile[] PathToBank_GrandExchange = new Tile[] {
            new Tile(3211, 3500, 0),
            new Tile(3201, 3500, 0),
            new Tile(3193, 3493, 0),
            new Tile(3180, 3489, 0)
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
