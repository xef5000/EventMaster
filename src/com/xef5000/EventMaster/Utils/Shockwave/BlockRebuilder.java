package com.xef5000.EventMaster.Utils.Shockwave;

import java.util.List;

import org.bukkit.block.BlockState;

public class BlockRebuilder implements Runnable {

    private final List<BlockState> blocks;


    public BlockRebuilder(List<BlockState> blocks) {
        this.blocks = blocks;
    }


    @Override
    public void run() {
        for(BlockState state : blocks) {
            state.update(true);
        }
    }

}
