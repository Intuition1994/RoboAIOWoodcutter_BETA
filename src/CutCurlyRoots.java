import com.epicbot.api.concurrent.Task;
import com.epicbot.api.concurrent.node.Node;
import com.epicbot.api.rs3.methods.Walking;
import com.epicbot.api.rs3.methods.interactive.Players;
import com.epicbot.api.rs3.methods.node.SceneEntities;
import com.epicbot.api.rs3.methods.tab.Skills;
import com.epicbot.api.rs3.methods.tab.inventory.Inventory;
import com.epicbot.api.rs3.methods.widget.Camera;
import com.epicbot.api.rs3.wrappers.Area;
import com.epicbot.api.rs3.wrappers.Tile;
import com.epicbot.api.rs3.wrappers.node.SceneObject;
import com.epicbot.api.util.Random;
import com.epicbot.api.util.Time;

/**
 * Created by Admin on 06/04/2015.
 */
public class CutCurlyRoots extends Node implements Task
{
    public BotUtil.PossibleLogs myLogs = BotUtil.PossibleLogs.CURLY_ROOTS;

    public String rootName = "jade root";
    public int[] curlyJadeRootIDs = new int[] { 12274, 12279 };
    public Tile rootToCutLocation = null;

    public boolean dropping = false;
    public int lastInventoryCheck = 0;

    public int[] rootIDs = new int[] { 21350 };

    public Area curlyRootArea = new Area(
            new Tile( 3070, 9270 ),
            new Tile( 3070, 9220 ),
            new Tile( 3050, 9230 ),
            new Tile( 3050, 9256 )
    );

    public CutCurlyRoots()
    {

    }

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
        if(Inventory.getCount()<28 && !dropping)
        {
            CutRoots();
        }
        else
        {
            if( Skills.Skill.FIREMAKING.getCurrentLevel() > 82  )
            {
                dropping = true;
                BurnRoots();
            }
            else
            {
                dropping = true;
                BotUtil.DropLogs( rootIDs );
            }

            if( Inventory.getCount(rootIDs) < 1 )
            {
                dropping = false;
            }
        }
    }

    public void BurnRoots()
    {
        SceneObject firepit = BotUtil.FindNearestSceneObject("firepit", "add");
        if( firepit == null ) firepit = BotUtil.FindNearestSceneObject("firepit", "light");

        if( firepit != null )
        {
            String[] actions = firepit.getDefinition().getActions();
            String actionToUse = "";

            for (String a : actions)
            {
                if( a != null)
                {
                    if (a.toLowerCase().contains("add") || a.toLowerCase().contains("light"))
                    {
                        actionToUse = a;
                    }
                }
            }

            if (firepit.isOnScreen() && Players.getLocal().isIdle())
            {
                Time.sleep(500, 1000);
                firepit.interact(actionToUse);

                if(actionToUse.toLowerCase().contains("light"))
                {
                    Time.sleep(2000, 3000);
                }
                else
                {
                    Time.sleep(4000, 10000);
                }
            }
            else
            {
                double distance = firepit.getLocation().distance(Players.getLocal().getLocation());
                if( !Players.getLocal().isMoving() && distance > 5d)
                {
                    Walking.walk(firepit);
                }

                Camera.turnTo(firepit, 5);
                Time.sleep(250, 500);
            }
        }


    }

    public void CutRoots()
    {
        if( rootToCutLocation == null )
        {
            SceneObject rootToCut = FindNearestRoot();
            rootToCutLocation = rootToCut.getLocation();
            lastInventoryCheck = Inventory.getCount();
        }
        else
        {
            SceneObject root = SceneEntities.getAt(rootToCutLocation);

            if (root != null) {
                String[] actions = root.getDefinition().getActions();
                String actionToUse = "";

                for (String a : actions)
                {
                    if (a != null)
                    {
                        if (a.toLowerCase().contains("chop"))
                        {
                            actionToUse = a;
                        }
                        else if (a.toLowerCase().contains("collect"))
                        {
                            actionToUse = a;
                        }
                    }
                }

                if (root.getDefinition().getID() != curlyJadeRootIDs[0] && root.getDefinition().getID() != curlyJadeRootIDs[1])
                {
                    rootToCutLocation = null;
                }

                if (Players.getLocal().isIdle())
                {
                    if (root.isOnScreen())
                    {
                        if (!Players.getLocal().isMoving())
                        {
                            root.interact(actionToUse);
                            Time.sleep(1000, 2000);
                        }
                    }
                    else
                    {
                        if (!Players.getLocal().isMoving())
                        {
                            double distance = root.getLocation().distance(Players.getLocal().getLocation());
                            if (distance > 5d)
                            {
                                Walking.findPath(root);
                            }
                        }

                        Camera.turnTo(root, 5);
                        Time.sleep(1000, 2000);
                    }
                }
            }
            else
            {
                rootToCutLocation = null;
            }
        }
    }

    public SceneObject FindNearestRoot()
    {
        SceneObject retValue = null;
        SceneObject[] objects = SceneEntities.getLoaded();
        double curDist = 99999d;


        for( SceneObject obj : objects )
        {
            if( obj.getDefinition().getName().toLowerCase().contains(rootName.toLowerCase()) )
            {
                if( obj.getDefinition().getID() == curlyJadeRootIDs[0] )
                {
                    if( curlyRootArea.contains(obj.getLocation()) )
                    {
                        double distance = obj.getLocation().distance(Players.getLocal().getLocation());

                        if( distance < curDist )
                        {
                            curDist = distance;
                            retValue = obj;
                        }
                    }
                }
            }
        }


        return retValue;
    }


}
