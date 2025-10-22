package eva.spreak.mixin;

import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Gui.class)
public abstract class GuiMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private Player getCameraPlayer() {
        return null;
    }

    @Inject(method = "renderItemHotbar", at = @At(value = "HEAD"))
    private void afterRenderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player basePlayer = this.getCameraPlayer();
        if (!(basePlayer instanceof LocalPlayer player)) {
            return;
        }

        if (!player.isCrouching() && !player.isSprinting()) {
            return;
        }

        int i = guiGraphics.guiWidth() / 2;
        int n = guiGraphics.guiHeight() - 20;
        boolean option = Minecraft.getInstance().options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR;
        int a = option ? 24 : 0;
        int o = i + 91 + 6 + a;
        int o2 = o + 24;
        if (player.getMainArm().getOpposite() == HumanoidArm.RIGHT) {
            o = i - 91 - 22 - a;
            o2 = o - 24;
        }
        if (player.isSprinting()) {
            TextureAtlasSprite sprint = this.minecraft.getMobEffectTextures().get(MobEffects.MOVEMENT_SPEED);
            guiGraphics.blitSprite(RenderType::guiTextured, sprint, o, n, 18, 18);
        }
        if (player.isCrouching()) {
            TextureAtlasSprite sneak = this.minecraft.getMobEffectTextures().get(MobEffects.MOVEMENT_SLOWDOWN);
            guiGraphics.blitSprite(RenderType::guiTextured, sneak, o2, n, 18, 18);
        }
    }

    @Unique
    private int adjustAttackIndicatorX(int original) {
        Player player = this.getCameraPlayer();
        if (player == null) {
            return original;
        }
        HumanoidArm arm = player.getMainArm().getOpposite();
        return original + (arm == HumanoidArm.LEFT ? 24 : -24);
    }
}
