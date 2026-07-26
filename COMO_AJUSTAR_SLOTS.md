# Como Ajustar os Slots para Caberem na sua Nova Textura

Como o background da imagem (a textura) foi centralizado no seu arquivo PNG, todos os "buracos" onde os itens desenham mudaram de posição na textura. **Isso é normal!** Você só precisa dizer ao jogo as novas posições desses buracos.

Use os comandos in-game que nós criamos anteriormente.

## 1. Ajustando os Principais (Inventário e Crafting)

Seu inventário e hotbar moveram? Use esses dois comandos:
```bash
# Move todos os slots principais do inventário e do hotbar ao mesmo tempo
/inventorypos inventory x <valor>
/inventorypos inventory y <valor>

# Move o crafting grid inteiro e o slot de saída ao mesmo tempo
/inventorypos crafting x <valor>
/inventorypos crafting y <valor>
```
*Dica: o `<valor>` pode ser negativo (ex: `-20`) para mover para a esquerda, ou positivo para mover para direita.*

## 2. Ajustando as Armaduras (Helmet, Chestplate, Leggings, Boots)

Para as armaduras, você ajusta as posições **exatas e diretas**.
```bash
/inventorypos helmet x <valor>
/inventorypos helmet y <valor>

/inventorypos chestplate x <valor>
/inventorypos chestplate y <valor>

/inventorypos leggings x <valor>
/inventorypos leggings y <valor>

/inventorypos boots x <valor>
/inventorypos boots y <valor>
```

## 3. Ajustando o Offhand e a Renderização do Player

```bash
# O slot da mão secundária
/inventorypos offhand x <valor>
/inventorypos offhand y <valor>

# Onde o player aparece desenhado na tela
/inventorypos player x <valor>
```

---

## 🚀 PASSO A PASSO NO JOGO

1. Entre no mundo no Minecraft (`./gradlew runClient`).
2. Abra o inventário com "E".
3. Feche o inventário.
4. Digite, por exemplo: `/inventorypos inventory x 20`
5. Abra o inventário com "E" e veja se o slot principal alinhou.
6. Repita os ajustes e comandos (`/inventorypos inventory y ...`) até alinhar tudo certinho!
7. Quando tudo estiver perfeito, digite o seguinte para ver seus números finais:
   ```bash
   /inventorypos show
   ```
8. **MUITO IMPORTANTE:** Anote esses valores finais e edite o arquivo `dev/joaq/ancestralpowers/client/InventoryLayoutConfig.java` com esses números para que eles fiquem salvos para sempre!
