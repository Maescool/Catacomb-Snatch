function OnRender()
{
    //println('Hi there! Render!');
}

function OnLevelTick(level)
{
    println('Level Tick!');
    println(level.width);
}

function OnTick(){}
    
function AfterTick(){}
    
    function OnStartRender(){}
    
    function RunOnce(){}
    
    function OnClose(){}
    
    function OnSendPacket(packet){}
    
function OnStop()
{
    println('Quitting...');
    /*var manager =new javax.script.ScripEngineManager();
    var factoryList = manager.getEngineFactories();
    for(var i = 0; i < factoryList.size();i++)
    {
        println(factoryList.get(i).getLanguageName());
    }
    */  
}
    
    function OnVictory(team){}
    
    function OnReceivePacket(packet){}
    
    function HandlePacket(packet){}
    
    function CreateLevel(level){}