package com.cigar.client.mixin;



import com.cigar.client.visual.ESPController;

import com.mojang.blaze3d.opengl.GlStateManager;

import net.minecraft.client.render.Camera;

import net.minecraft.client.render.RenderTickCounter;

import net.minecraft.client.render.WorldRenderer;

import net.minecraft.client.util.math.MatrixStack;

import org.joml.Matrix4f;

import org.joml.Vector4f;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(WorldRenderer.class)

public class WorldRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))

    private void onRender(net.minecraft.client.util.ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f viewMatrix, Matrix4f positionMatrix, Matrix4f projectionMatrix, com.mojang.blaze3d.buffers.GpuBufferSlice bufferSlice, Vector4f vector4f, boolean someBool, CallbackInfo ci) {

       



        ESPController.lastModelViewMatrix.set(positionMatrix);

        ESPController.lastProjectionMatrix.set(projectionMatrix);



        MatrixStack matrices = new MatrixStack();

 

        matrices.multiplyPositionMatrix(viewMatrix);



        GlStateManager._depthFunc(519);

       

        ESPController.render3D(matrices, camera, tickCounter);

        GlStateManager._depthFunc(515);

    }

}
