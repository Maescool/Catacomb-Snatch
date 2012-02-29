function OnRender()
{
    lastFrame = frame;
    frame = Snatch.nanoTime;
    fps = 1000000000/(frame-lastFrame);
    Font.draw(Snatch.getMojam().screen, Snatch.getMojam.texts.FPS(fps), 10, 10);
}

function OnLevelTick(level)
{
}

var Font = new Font();
var invulnTimer = 0;
var Snatch;
var fps = 0.0;
var frame = 0;
var lastFrame = 0;

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
    
    function RunOnce(){}
    
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