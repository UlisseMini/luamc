function lookatblock(x, y, z)
    if x == math.floor(x) or z == math.floor(z) then
        x = math.floor(x) + 0.5 -- center of the block is 1.500, not 1
        z = math.floor(z) + 0.5
    end
    playerpos = player.getPos()

    posdiff = {playerpos.X - x, (playerpos.Y + 1) - y, playerpos.Z - z} -- y + 1 because camera isn't at the feet
    yaw = math.atan2(posdiff[1], -posdiff[3])
    dist = math.sqrt(posdiff[1] * posdiff[1] + posdiff[3] * posdiff[3])
    pitch = math.atan2(posdiff[2], dist)

    player.setPitch(math.deg(pitch))
    player.setYaw(math.deg(yaw))

    return nil
end

function round(num, numDecimalPlaces) -- thank you lua-users.org
    local mult = 10^(numDecimalPlaces or 0)
    return math.floor(num * mult + 0.5) / mult
end

function getdist(x1, y1, z1, x2, y2, z2)
    distx = math.abs(x1 - x2)
    disty = math.abs((y1 + 1) - y2)
    distz = math.abs(z1 - z2)
    return round(math.sqrt(distx^2 + disty^2 +distz^2), 1)
end

function mineblock(x, y, z)
    if player.inspect(x, y, z) == "minecraft:air" then
        return false
    end
    pp = player.getPos() -- pp is a good variable name here, fight me
    if getdist(pp.X, pp.Y, pp.Z, x, y, z) > 6 then
        return false
    end
    -- okay now we know that it's possible to mine the block (unless it's surrounded with something, fuck)

    lookatblock(x, y, z)
    sleep(30)
    seenblock = player.getLookedAtBlock()
    if seenblock.X ~= x or seenblock.Y ~= y or seenblock.Z ~= z then
        return false -- TODO: check corners of block to see if seenblock changes
    end
    key = player.key(settings.keyBindAttack:getKeyCode())
    key.down()
    while player.inspect(x, y, z) ~= "minecraft:air" do
        lookatblock(x, y, z)
        sleep(10)
    end
    key.up()
        
    return true
end

diditwork = mineblock(5, 3, 5) -- go die yourself null
print(diditwork)