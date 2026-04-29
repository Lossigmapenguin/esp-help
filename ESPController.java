package com.cigar.client.visual;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import java.awt.Color;

public class ESPController {

    public static final Matrix4f lastModelViewMatrix = new Matrix4f();

    public static final Matrix4f lastProjectionMatrix = new Matrix4f();

 

    public static void register() {

    }

 

    public static void render3D(MatrixStack matrices, Camera camera, RenderTickCounter tickCounter) {

        if (!ESPConfig.enabled) return;

 

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || client.player == null) return;

 

        Vec3d cam = camera.getCameraPos();

        float tickDelta = tickCounter.getTickProgress(false);

 

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();


        GlStateManager._depthFunc(519);

 

        for (Entity entity : client.world.getEntities()) {

            if (entity == client.player || !entity.isAlive()) continue;

 

            boolean isPlayer = entity instanceof PlayerEntity;

            boolean isMob = entity instanceof MobEntity;

            if ((isPlayer && !ESPConfig.players) || (isMob && !ESPConfig.mobs)) continue;

 


            double dx = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - cam.x;

            double dy = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - cam.y;

            double dz = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - cam.z;

 

            matrices.push();

           

            Box box = entity.getBoundingBox();

            Box relativeBox = box.offset(-entity.getX() + dx, -entity.getY() + dy, -entity.getZ() + dz);

           

            Color color = isPlayer ? Color.RED : Color.ORANGE;

            float r = color.getRed() / 255f;

            float g = color.getGreen() / 255f;

            float b = color.getBlue() / 255f;

 

            VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayers.lines());

            drawBox(matrices, vertexConsumer, relativeBox, r, g, b, 1.0f);

 

            if (ESPConfig.tracers) {

                Matrix4f mat = matrices.peek().getPositionMatrix();

                float centerHeight = (float) entity.getHeight() / 2.0f;

                vertexConsumer.vertex(mat, 0, 0, 0).color(r, g, b, 1.0f).normal(0, 1, 0).lineWidth(ESPConfig.lineWidth);

                vertexConsumer.vertex(mat, (float)dx, (float)(dy + centerHeight), (float)dz).color(r, g, b, 1.0f).normal(0, 1, 0).lineWidth(ESPConfig.lineWidth);

            }

 


            VertexConsumer fillConsumer = immediate.getBuffer(RenderLayers.debugFilledBox());

            drawFilledBox(matrices, fillConsumer, relativeBox, r, g, b, 0.3f);

 

 

            matrices.pop();

        }



        immediate.draw(RenderLayers.lines());

        immediate.draw(RenderLayers.debugFilledBox());

       



        GlStateManager._depthFunc(515);

    }

 

    private static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float r, float g, float b, float a) {

        Matrix4f mat = matrices.peek().getPositionMatrix();

        float x1 = (float)box.minX; float y1 = (float)box.minY; float z1 = (float)box.minZ;

        float x2 = (float)box.maxX; float y2 = (float)box.maxY; float z2 = (float)box.maxZ;

 

        line(mat, vertexConsumer, x1, y1, z1, x2, y1, z1, r, g, b, a);

        line(mat, vertexConsumer, x2, y1, z1, x2, y1, z2, r, g, b, a);

        line(mat, vertexConsumer, x2, y1, z2, x1, y1, z2, r, g, b, a);

        line(mat, vertexConsumer, x1, y1, z2, x1, y1, z1, r, g, b, a);

        line(mat, vertexConsumer, x1, y2, z1, x2, y2, z1, r, g, b, a);

        line(mat, vertexConsumer, x2, y2, z1, x2, y2, z2, r, g, b, a);

        line(mat, vertexConsumer, x2, y2, z2, x1, y2, z2, r, g, b, a);

        line(mat, vertexConsumer, x1, y2, z2, x1, y2, z1, r, g, b, a);

        line(mat, vertexConsumer, x1, y1, z1, x1, y2, z1, r, g, b, a);

        line(mat, vertexConsumer, x2, y1, z1, x2, y2, z1, r, g, b, a);

        line(mat, vertexConsumer, x2, y1, z2, x2, y2, z2, r, g, b, a);

        line(mat, vertexConsumer, x1, y1, z2, x1, y2, z2, r, g, b, a);

    }

 

    private static void line(Matrix4f mat, VertexConsumer v, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {

        v.vertex(mat, x1, y1, z1).color(r, g, b, a).normal(0, 1, 0).lineWidth(ESPConfig.lineWidth);

        v.vertex(mat, x2, y2, z2).color(r, g, b, a).normal(0, 1, 0).lineWidth(ESPConfig.lineWidth);

    }

 

    private static void drawFilledBox(MatrixStack matrices, VertexConsumer v, Box box, float r, float g, float b, float a) {

        Matrix4f mat = matrices.peek().getPositionMatrix();

        float x1 = (float)box.minX; float y1 = (float)box.minY; float z1 = (float)box.minZ;

        float x2 = (float)box.maxX; float y2 = (float)box.maxY; float z2 = (float)box.maxZ;

 

        // Bottom

        v.vertex(mat, x1, y1, z1).color(r, g, b, a); v.vertex(mat, x2, y1, z1).color(r, g, b, a); v.vertex(mat, x2, y1, z2).color(r, g, b, a); v.vertex(mat, x1, y1, z2).color(r, g, b, a);

        // Top

        v.vertex(mat, x1, y2, z1).color(r, g, b, a); v.vertex(mat, x1, y2, z2).color(r, g, b, a); v.vertex(mat, x2, y2, z2).color(r, g, b, a); v.vertex(mat, x2, y2, z1).color(r, g, b, a);

        // North

        v.vertex(mat, x1, y1, z1).color(r, g, b, a); v.vertex(mat, x1, y2, z1).color(r, g, b, a); v.vertex(mat, x2, y2, z1).color(r, g, b, a); v.vertex(mat, x2, y1, z1).color(r, g, b, a);

        // South

        v.vertex(mat, x1, y1, z2).color(r, g, b, a); v.vertex(mat, x2, y1, z2).color(r, g, b, a); v.vertex(mat, x2, y2, z2).color(r, g, b, a); v.vertex(mat, x1, y2, z2).color(r, g, b, a);

        // West

        v.vertex(mat, x1, y1, z1).color(r, g, b, a); v.vertex(mat, x1, y1, z2).color(r, g, b, a); v.vertex(mat, x1, y2, z2).color(r, g, b, a); v.vertex(mat, x1, y2, z1).color(r, g, b, a);

        // East

        v.vertex(mat, x2, y1, z1).color(r, g, b, a); v.vertex(mat, x2, y2, z1).color(r, g, b, a); v.vertex(mat, x2, y2, z2).color(r, g, b, a); v.vertex(mat, x2, y1, z2).color(r, g, b, a);

    }

}

