function lookatblock(x, y, z)
    x = math.floor(x) + 0.5 -- center of the block is 1.500, not 1
    z = math.floor(z) + 0.5
    playerpos = player.getPos()

    posdiff = {playerpos.X - x, (playerpos.Y + 1) - y, playerpos.Z - z} -- y + 1 because camera isn't at the feet
    yaw = math.atan2(posdiff[1], -posdiff[3])
    dist = math.sqrt(posdiff[1] * posdiff[1] + posdiff[3] * posdiff[3])
    pitch = math.atan2(posdiff[2], dist)

    player.setPitch(math.deg(pitch))
    player.setYaw(math.deg(yaw))

    return nil
end
