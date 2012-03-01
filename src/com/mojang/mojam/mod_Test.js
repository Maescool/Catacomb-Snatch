function OnRender()
{
    //if((frame % 500) < (lastFrame % 500))
    {
        lastFrame = frame;
    }
    frame = Snatch.currentTimeMillis();
    if((frame % 500) < (lastFrame % 500))fps = 1000/(frame-lastFrame);
    Snatch.getFont().draw(Snatch.getMojam().screen, Snatch.getMojam().texts.FPS(fps), 10, 10);
}

function OnLevelTick(level)
{
}

var invulnTimer = 5;
var Snatch;
var fps = 1;
var frame = 1;
var lastFrame = 1;

function OnTick(){
        var player = Snatch.getMojam().player;
        if(invulnTimer > 0)
        {
            player.isImmortal = true;
            invulnTimer--;
        }
        else if(player.useMoney(1))
        {
            invulnTimer = 100;
        }
        else
        {
            player.isImmortal = false;
        }
}
    
function AfterTick(){}
    
    function OnStartRender(){}
    
    function RunOnce(){Snatch.setGamemode(new com.mojang.mojam.level.gamemode.GameModeGoldRush());}
    
    function OnClose(){}
    
    function OnSendPacket(packet){}
    
function OnStop()
{
    println('Quitting...');
    
}
    
    function OnVictory(team){}
    
    function OnReceivePacket(packet){}
    
    function HandlePacket(packet){}
    
    function CreateLevel(level){}