package dev.joaq.ancestralpowers.client;

public class InventoryLayoutConfig {
    // === O DESLOCAMENTO LÓGICO GERAL DA TEXTURA (256x256) ===
    // Quando a textura pulou de 176 para 256, os elementos migraram +40 no eixo X
    public static int BASE_TEXTURE_SHIFT_X = 40;

    // === INVENTORY DEFAULT ===
    // O inventário e a hotbar terão o deslocamento de +40 automaticamente.
    // Se quiser mover todo o inventário/hotbar um pouco mais, altere aqui:
    public static int inventoryOffsetX = 0;
    public static int inventoryOffsetY = 0;

    // === CRAFTING (Esquerda) ===
    public static int craftingGridX = 3;
    public static int craftingGridY = 37;
    public static int craftingResultX = 11;
    public static int craftingResultY = 103;

    // === ARMOR & PLAYER (Direita) ===
    public static int helmetSlotX = 224;
    public static int helmetSlotY = 8;

    public static int chestplateSlotX = 224;
    public static int chestplateSlotY = 26;

    public static int leggingsSlotX = 224;
    public static int leggingsSlotY = 44;

    public static int bootsSlotX = 224;
    public static int bootsSlotY = 62;

    public static int offhandSlotX = 224;
    public static int offhandSlotY = 80;

    // Posição baseX de onde o player vai ser desenhado
    public static int playerRenderX = 240;
    public static int playerRenderY = 8;
    public static int playerRenderEndX = 75;
    public static int playerRenderEndY = 78;
    public static int playerRenderSize = 50;
    
    public static void reset() {
        BASE_TEXTURE_SHIFT_X = 40;
        inventoryOffsetX = 0;
        inventoryOffsetY = 0;
        craftingGridX = 3;
        craftingGridY = 37;
        craftingResultX = 11;
        craftingResultY = 103;
        helmetSlotX = 224;
        helmetSlotY = 8;
        chestplateSlotX = 224;
        chestplateSlotY = 26;
        leggingsSlotX = 224;
        leggingsSlotY = 44;
        bootsSlotX = 224;
        bootsSlotY = 62;
        offhandSlotX = 224;
        offhandSlotY = 80;
        playerRenderX = 240;
        playerRenderY = 8;
        playerRenderEndX = 75;
        playerRenderEndY = 78;
        playerRenderSize = 50;
    }
}
