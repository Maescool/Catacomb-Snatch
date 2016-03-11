require 'java'
java_import com.mojang.mojam.mod.ModSystem
java_import com.mojang.mojam.MojamComponent

mods = ModSystem.new
mojam = ModSystem.getMojam()

def log(s)
    mojam.console.log(s)
end